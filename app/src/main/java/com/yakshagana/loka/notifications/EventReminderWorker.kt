package com.yakshagana.loka.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.yakshagana.loka.MainActivity
import com.yakshagana.loka.R
import com.yakshagana.loka.data.FakeContentRepository
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class EventReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val eventId = inputData.getString(KEY_EVENT_ID) ?: return Result.failure()
        val repository = FakeContentRepository()
        val event = repository.findEvent(eventId) ?: return Result.failure()

        val openIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra(KEY_EVENT_ID, eventId)
        }
        val openPendingIntent = PendingIntent.getActivity(
            applicationContext,
            event.id.hashCode(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val shareIntent = Intent(applicationContext, ShareEventReceiver::class.java).apply {
            putExtra(KEY_EVENT_ID, eventId)
        }
        val sharePendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            (event.id + "_share").hashCode(),
            shareIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, NotificationChannels.EVENT_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Upcoming Show: ${event.title}")
            .setContentText("${event.venue} | ${event.melaName}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .addAction(0, "Share Poster", sharePendingIntent)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(event.id.hashCode(), notification)
        return Result.success()
    }

    companion object {
        const val KEY_EVENT_ID = "event_id"

        fun scheduleReminders(context: Context, eventId: String, eventDateTime: LocalDateTime) {
            listOf(24L, 3L, 0L).forEach { hoursBefore ->
                val reminderTime = if (hoursBefore == 0L) eventDateTime.minusMinutes(30) else eventDateTime.minusHours(hoursBefore)
                val duration = Duration.between(LocalDateTime.now(), reminderTime).toMillis()
                if (duration <= 0) return@forEach

                val inputData = Data.Builder().putString(KEY_EVENT_ID, eventId).build()
                val request = OneTimeWorkRequestBuilder<EventReminderWorker>()
                    .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .build()
                WorkManager.getInstance(context).enqueue(request)
            }
        }
    }
}
