package com.yakshagana.loka.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yakshagana.loka.data.FakeContentRepository
import com.yakshagana.loka.util.PosterShare

class ShareEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getStringExtra(EventReminderWorker.KEY_EVENT_ID) ?: return
        val repository = FakeContentRepository()
        val event = repository.findEvent(eventId) ?: return
        PosterShare.shareEventPoster(context, event)
    }
}
