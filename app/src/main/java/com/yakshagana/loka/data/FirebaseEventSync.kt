package com.yakshagana.loka.data

import android.net.Uri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yakshagana.loka.model.Event
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object FirebaseEventSync {
    private const val COLLECTION = "mela_events"
    private const val STORAGE_FOLDER = "event_thumbnails"

    suspend fun uploadImage(imageUri: Uri): String? = suspendCancellableCoroutine { cont ->
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("$STORAGE_FOLDER/${System.currentTimeMillis()}.jpg")

        val uploadTask = imageRef.putFile(imageUri)
        
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                if (cont.isActive) cont.resume(uri.toString())
            }.addOnFailureListener {
                if (cont.isActive) cont.resume(null)
            }
        }.addOnFailureListener {
            if (cont.isActive) cont.resume(null)
        }
    }

    suspend fun pushEvent(event: Event): Boolean = suspendCancellableCoroutine { cont ->
        val payload = hashMapOf(
            "id" to event.id,
            "melaName" to event.melaName,
            "title" to event.title,
            "venue" to event.venue,
            "dateTimeEpochSeconds" to event.dateTime.toEpochSecond(ZoneOffset.UTC),
            "latitude" to event.latitude,
            "longitude" to event.longitude,
            "thumbnailUri" to event.thumbnailUri,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            FirebaseFirestore.getInstance()
                .collection(COLLECTION)
                .document(event.id)
                .set(payload)
                .addOnSuccessListener { if (cont.isActive) cont.resume(true) }
                .addOnFailureListener { if (cont.isActive) cont.resume(false) }
        } catch (_: Throwable) {
            if (cont.isActive) cont.resume(false)
        }
    }

    suspend fun deleteEvent(eventId: String): Boolean = suspendCancellableCoroutine { cont ->
        try {
            FirebaseFirestore.getInstance()
                .collection(COLLECTION)
                .document(eventId)
                .delete()
                .addOnSuccessListener { if (cont.isActive) cont.resume(true) }
                .addOnFailureListener { if (cont.isActive) cont.resume(false) }
        } catch (_: Throwable) {
            if (cont.isActive) cont.resume(false)
        }
    }

    suspend fun fetchAllEvents(): List<Event> = suspendCancellableCoroutine { cont ->
        try {
            FirebaseFirestore.getInstance()
                .collection(COLLECTION)
                .get()
                .addOnSuccessListener { snapshot ->
                    val events = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.getString("id") ?: return@mapNotNull null
                            val melaName = doc.getString("melaName") ?: return@mapNotNull null
                            val title = doc.getString("title") ?: return@mapNotNull null
                            val venue = doc.getString("venue") ?: return@mapNotNull null
                            val dateTimeEpochSeconds = doc.getLong("dateTimeEpochSeconds") ?: return@mapNotNull null
                            val latitude = doc.getDouble("latitude") ?: return@mapNotNull null
                            val longitude = doc.getDouble("longitude") ?: return@mapNotNull null
                            val thumbnailUri = doc.getString("thumbnailUri")

                            Event(
                                id = id,
                                melaName = melaName,
                                title = title,
                                venue = venue,
                                dateTime = LocalDateTime.ofInstant(
                                    Instant.ofEpochSecond(dateTimeEpochSeconds),
                                    ZoneOffset.UTC
                                ),
                                latitude = latitude,
                                longitude = longitude,
                                thumbnailUri = thumbnailUri
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }
                    if (cont.isActive) cont.resume(events)
                }
                .addOnFailureListener { if (cont.isActive) cont.resume(emptyList()) }
        } catch (_: Throwable) {
            if (cont.isActive) cont.resume(emptyList())
        }
    }
}
