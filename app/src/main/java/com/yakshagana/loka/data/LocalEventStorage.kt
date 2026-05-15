package com.yakshagana.loka.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.yakshagana.loka.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalEventStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("event_storage", Context.MODE_PRIVATE)
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()
    private val _managerEvents = MutableStateFlow<List<Event>>(emptyList())
    val managerEvents: StateFlow<List<Event>> = _managerEvents

    init {
        loadEvents()
    }

    private fun loadEvents() {
        try {
            val json = prefs.getString("manager_events", null)
            if (json != null) {
                val type = object : TypeToken<List<Event>>() {}.type
                _managerEvents.value = gson.fromJson(json, type) ?: emptyList()
            }
        } catch (e: Exception) {
            _managerEvents.value = emptyList()
        }
    }

    fun saveEvent(event: Event) {
        val currentEvents = _managerEvents.value.toMutableList()
        currentEvents.add(event)
        saveEvents(currentEvents)
    }

    fun deleteEvent(eventId: String) {
        val currentEvents = _managerEvents.value.filterNot { it.id == eventId }
        saveEvents(currentEvents)
    }

    private fun saveEvents(events: List<Event>) {
        _managerEvents.value = events
        val json = gson.toJson(events)
        prefs.edit().putString("manager_events", json).apply()
    }

    fun getEvents(): List<Event> = _managerEvents.value

    private class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        override fun write(writer: JsonWriter, value: LocalDateTime?) {
            if (value == null) {
                writer.nullValue()
            } else {
                writer.value(formatter.format(value))
            }
        }

        override fun read(reader: JsonReader): LocalDateTime? {
            if (reader.peek() == com.google.gson.stream.JsonToken.NULL) {
                reader.nextNull()
                return null
            }
            return LocalDateTime.parse(reader.nextString(), formatter)
        }
    }
}
