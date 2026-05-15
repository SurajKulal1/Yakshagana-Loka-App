package com.yakshagana.loka.model

import java.time.LocalDateTime

data class Event(
    val id: String,
    val melaName: String,
    val title: String,
    val venue: String,
    val dateTime: LocalDateTime,
    val endDateTime: LocalDateTime? = null,
    val latitude: Double,
    val longitude: Double,
    val thumbnailUri: String? = null,
    val invitationProof: String? = null,
    val contactInfo: String? = null,
    val briefDescription: String? = null,
    val description: String? = null
)

data class Artist(
    val id: String,
    val name: String,
    val role: String,
    val bio: String,
    val veshas: List<String>,
    val gallery: List<String>
)

data class VeshaArtistProfile(
    val name: String,
    val stageTitle: String,
    val place: String,
    val styleNote: String,
    val achievements: List<String>
)

data class VeshaProfile(
    val id: String,
    val name: String,
    val description: String,
    val characteristics: List<String>,
    val significance: String,
    val performanceContexts: List<String>,
    val historyNote: String = "Yakshagana grew in coastal Karnataka between the medieval Bhakti era and later temple-performance traditions, eventually becoming a full-night theatre discipline.",
    val evolutionNote: String = "Its evolution connected temple courtyards, village open stages, and touring melas, refining acting grammar, costume architecture, and audience-driven improvisation.",
    val prasangaNote: String = "Prasangas are narrative performance texts adapted from epic episodes and staged with song, dialogue exchange, dramatic entry patterns, and rhythmic transitions.",
    val literatureNote: String = "The literary foundation comes from the Ramayana, Mahabharata, Bhagavata, regional poetic traditions, and later authored prasanga manuscripts used by troupes.",
    val instrumentNote: String = "Core accompaniment uses chende, maddale, tala, and supporting shruti layers, creating a percussive and vocal framework for character movement.",
    val musicGenreNote: String = "Musically, Yakshagana blends devotional, dramatic, and folk-classical melodic treatment led by the Bhagavata, with strong tala orientation and improvisational response.",
    val famousMelas: List<String> = emptyList(),
    val artists: List<VeshaArtistProfile>,
    val imageRes: Int? = null
)

data class AudioClip(
    val id: String,
    val title: String,
    val artistName: String,
    val url: String
)
