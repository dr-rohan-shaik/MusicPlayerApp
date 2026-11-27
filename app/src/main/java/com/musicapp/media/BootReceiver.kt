package com.musicapp.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.media3.session.MediaSessionService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_QUICKBOOT_POWERON) {

            // Start the media playback service to restore playback state
            val serviceIntent = Intent(context, MediaPlaybackService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}