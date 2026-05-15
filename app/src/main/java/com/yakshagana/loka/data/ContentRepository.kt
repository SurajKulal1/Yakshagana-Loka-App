package com.yakshagana.loka.data

import com.yakshagana.loka.model.Artist
import com.yakshagana.loka.model.AudioClip
import com.yakshagana.loka.model.Event
import com.yakshagana.loka.model.VeshaProfile
import java.time.LocalDate
import java.time.LocalDateTime

interface ContentRepository {
    fun tonightShows(now: LocalDateTime = LocalDateTime.now()): List<Event>
    fun eventsOnDate(date: LocalDate): List<Event>
    fun allEvents(): List<Event>
    fun artists(query: String): List<Artist>
    fun veshaProfiles(): List<VeshaProfile>
    fun audioClips(): List<AudioClip>
    fun findEvent(eventId: String): Event?
}

class FakeContentRepository : ContentRepository {
    override fun tonightShows(now: LocalDateTime): List<Event> {
        val start = now.toLocalDate().atTime(17, 0)
        val end = now.toLocalDate().plusDays(1).atTime(2, 0)
        return SampleData.events.filter { it.dateTime.isAfter(start) && it.dateTime.isBefore(end) }
    }

    override fun eventsOnDate(date: LocalDate): List<Event> {
        return SampleData.events.filter { it.dateTime.toLocalDate() == date }
    }

    override fun allEvents(): List<Event> = SampleData.events

    override fun artists(query: String): List<Artist> {
        if (query.isBlank()) return SampleData.artists
        return SampleData.artists.filter {
            it.name.contains(query, ignoreCase = true) || it.role.contains(query, ignoreCase = true)
        }
    }

    override fun veshaProfiles(): List<VeshaProfile> = SampleData.veshaProfiles

    override fun audioClips(): List<AudioClip> = SampleData.audioClips

    override fun findEvent(eventId: String): Event? = SampleData.events.find { it.id == eventId }
}
