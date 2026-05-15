package com.yakshagana.loka.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakshagana.loka.data.ContentRepository
import com.yakshagana.loka.data.FirebaseEventSync
import com.yakshagana.loka.data.LocalEventStorage
import com.yakshagana.loka.model.Artist
import com.yakshagana.loka.model.AudioClip
import com.yakshagana.loka.model.Event
import com.yakshagana.loka.model.VeshaProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class AppState(
    val tonightEvents: List<Event> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedDateEvents: List<Event> = emptyList(),
    val allEvents: List<Event> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val veshaProfiles: List<VeshaProfile> = emptyList(),
    val selectedVeshaId: String? = null,
    val audioClips: List<AudioClip> = emptyList(),
    val artistQuery: String = "",
    val managerEvents: List<Event> = emptyList(),
    val firebaseEvents: List<Event> = emptyList(),
    val managerStatus: String? = null
)

class MainViewModel(
    private val repository: ContentRepository,
    private val localStorage: LocalEventStorage
) : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    init {
        loadLocalEvents()
        refresh()
        loadFirebaseEvents()
    }

    private fun loadLocalEvents() {
        _state.update {
            it.copy(managerEvents = localStorage.getEvents())
        }
    }

    private fun loadFirebaseEvents() {
        viewModelScope.launch {
            val firebaseEvents = FirebaseEventSync.fetchAllEvents()
            _state.update {
                val all = combinedEvents(repository.allEvents(), it.managerEvents, firebaseEvents)
                it.copy(
                    firebaseEvents = firebaseEvents,
                    tonightEvents = tonightFrom(all),
                    selectedDateEvents = all.filter { e -> e.dateTime.toLocalDate() == it.selectedDate },
                    allEvents = all
                )
            }
        }
    }

    private fun combinedEvents(base: List<Event>, managed: List<Event>, firebase: List<Event> = emptyList()): List<Event> =
        (base + managed + firebase).distinctBy { it.id }.sortedBy { it.dateTime }

    private fun tonightFrom(events: List<Event>, now: LocalDateTime = LocalDateTime.now()): List<Event> {
        val today = now.toLocalDate()
        val tomorrow = today.plusDays(1)
        return events.filter { 
            val eventDate = it.dateTime.toLocalDate()
            eventDate == today || (eventDate == tomorrow && it.dateTime.hour < 3)
        }.sortedBy { it.dateTime }
    }

    fun refresh() {
        _state.update {
            val base = repository.allEvents()
            val all = combinedEvents(base, it.managerEvents, it.firebaseEvents)
            it.copy(
                tonightEvents = tonightFrom(all),
                selectedDateEvents = all.filter { e -> e.dateTime.toLocalDate() == it.selectedDate },
                allEvents = all,
                artists = repository.artists(it.artistQuery),
                veshaProfiles = repository.veshaProfiles(),
                selectedVeshaId = it.selectedVeshaId ?: repository.veshaProfiles().firstOrNull()?.id,
                audioClips = repository.audioClips()
            )
        }
    }

    fun updateSelectedDate(date: LocalDate) {
        _state.update {
            val all = it.allEvents
            it.copy(
                selectedDate = date,
                selectedDateEvents = all.filter { e -> e.dateTime.toLocalDate() == date }
            )
        }
    }

    fun updateArtistQuery(query: String) {
        _state.update {
            it.copy(
                artistQuery = query,
                artists = repository.artists(query)
            )
        }
    }

    fun updateSelectedVesha(veshaId: String) {
        _state.update { it.copy(selectedVeshaId = veshaId) }
    }

    fun clearManagerStatus() {
        _state.update { it.copy(managerStatus = null) }
    }

    fun deleteManagerEvent(eventId: String) {
        localStorage.deleteEvent(eventId)
        _state.update {
            val managed = localStorage.getEvents()
            val all = combinedEvents(repository.allEvents(), managed, it.firebaseEvents)
            it.copy(
                managerEvents = managed,
                allEvents = all,
                tonightEvents = tonightFrom(all),
                selectedDateEvents = all.filter { e -> e.dateTime.toLocalDate() == it.selectedDate },
                managerStatus = "Event deleted successfully."
            )
        }
        
        viewModelScope.launch {
            FirebaseEventSync.deleteEvent(eventId)
            loadFirebaseEvents()
        }
    }

    fun addManagerEvent(
        melaName: String,
        title: String,
        venue: String,
        dateTime: LocalDateTime,
        endDateTime: LocalDateTime?,
        latitude: Double,
        longitude: Double,
        thumbnailUri: String? = null,
        invitationProof: String? = null,
        contactInfo: String? = null,
        briefDescription: String? = null,
        description: String? = null
    ) {
        viewModelScope.launch {
            try {
                val finalThumbnailUri = try {
                    if (thumbnailUri != null && 
                        (thumbnailUri.startsWith("file://") || thumbnailUri.startsWith("content://"))) {
                        FirebaseEventSync.uploadImage(android.net.Uri.parse(thumbnailUri))
                    } else {
                        thumbnailUri
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    thumbnailUri
                }

                val event = Event(
                    id = java.util.UUID.randomUUID().toString(),
                    melaName = melaName.trim(),
                    title = title.trim(),
                    venue = venue.trim(),
                    dateTime = dateTime,
                    endDateTime = endDateTime,
                    latitude = latitude,
                    longitude = longitude,
                    thumbnailUri = finalThumbnailUri,
                    invitationProof = invitationProof,
                    contactInfo = contactInfo,
                    briefDescription = briefDescription,
                    description = description
                )
                
                localStorage.saveEvent(event)
                
                _state.update {
                    val managed = localStorage.getEvents()
                    val all = combinedEvents(repository.allEvents(), managed, it.firebaseEvents)
                    it.copy(
                        managerEvents = managed,
                        allEvents = all,
                        tonightEvents = tonightFrom(all),
                        selectedDateEvents = all.filter { e -> e.dateTime.toLocalDate() == it.selectedDate },
                        managerStatus = "Event added locally and visible in schedule."
                    )
                }

                val remote = try {
                    FirebaseEventSync.pushEvent(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
                
                loadFirebaseEvents()
                _state.update {
                    it.copy(
                        managerStatus = if (remote) {
                            "Event added and synced to Firebase with image."
                        } else {
                            "Event added locally. Firebase is not configured yet, so cloud sync was skipped."
                        }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(managerStatus = "Error adding event, but it's saved locally.")
                }
            }
        }
    }

    fun updateManagerEvent(
        eventId: String,
        melaName: String,
        title: String,
        venue: String,
        dateTime: LocalDateTime,
        endDateTime: LocalDateTime?,
        latitude: Double,
        longitude: Double,
        thumbnailUri: String? = null,
        invitationProof: String? = null,
        contactInfo: String? = null,
        briefDescription: String? = null,
        description: String? = null
    ) {
        viewModelScope.launch {
            try {
                val finalThumbnailUri = try {
                    if (thumbnailUri != null && 
                        (thumbnailUri.startsWith("file://") || thumbnailUri.startsWith("content://"))) {
                        FirebaseEventSync.uploadImage(android.net.Uri.parse(thumbnailUri))
                    } else {
                        thumbnailUri
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    thumbnailUri
                }

                val event = Event(
                    id = eventId,
                    melaName = melaName.trim(),
                    title = title.trim(),
                    venue = venue.trim(),
                    dateTime = dateTime,
                    endDateTime = endDateTime,
                    latitude = latitude,
                    longitude = longitude,
                    thumbnailUri = finalThumbnailUri,
                    invitationProof = invitationProof,
                    contactInfo = contactInfo,
                    briefDescription = briefDescription,
                    description = description
                )
                
                localStorage.saveEvent(event)
                
                _state.update {
                    val managed = localStorage.getEvents()
                    val all = combinedEvents(repository.allEvents(), managed, it.firebaseEvents)
                    it.copy(
                        managerEvents = managed,
                        allEvents = all,
                        tonightEvents = tonightFrom(all),
                        selectedDateEvents = all.filter { e -> e.dateTime.toLocalDate() == it.selectedDate },
                        managerStatus = "Event updated successfully."
                    )
                }

                val remote = try {
                    FirebaseEventSync.pushEvent(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
                
                loadFirebaseEvents()
                _state.update {
                    it.copy(
                        managerStatus = if (remote) {
                            "Event updated and synced to Firebase."
                        } else {
                            "Event updated locally. Firebase is not configured yet, so cloud sync was skipped."
                        }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(managerStatus = "Error updating event, but it's saved locally.")
                }
            }
        }
    }
}
