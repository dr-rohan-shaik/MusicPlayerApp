package com.musicapp.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.session.MediaController
import android.media.session.MediaSession
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.session.MediaButtonReceiver
import androidx.media3.session.MediaControllerCompat
import androidx.media3.session.MediaSessionCompat
import com.musicapp.core.data.PlaybackState
import com.musicapp.core.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaNotificationProvider @Inject constructor(
    private val context: Context,
    private val mediaSession: MediaSessionCompat,
    private val mediaController: MediaControllerCompat
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val CHANNEL_ID = "media_playback_channel"
    private val NOTIFICATION_ID = 1

    private val _currentNotification = MutableStateFlow<Notification?>(null)
    val currentNotification: StateFlow<Notification?> = _currentNotification

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for media playback controls"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNotification() {
        val state = mediaController.playbackState
        val metadata = mediaController.metadata

        val notification = buildNotification(state, metadata)
        _currentNotification.value = notification

        if (state?.state == MediaControllerCompat.PlaybackState.STATE_PLAYING ||
            state?.state == MediaControllerCompat.PlaybackState.STATE_PAUSED) {
            // Start foreground service
            val service = context.getSystemService(android.content.Context.FOREGROUND_SERVICE)
            // Note: In practice, this would be called from the MediaPlaybackService
        } else {
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

    private fun buildNotification(state: MediaControllerCompat.PlaybackState?, metadata: android.media.MediaMetadata?): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setStyle(androidx.media3.session.MediaStyleNotificationHelper.from(context, mediaSession))
            .setSmallIcon(R.drawable.ic_music_note)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setOngoing(state?.state == MediaControllerCompat.PlaybackState.STATE_PLAYING)
            .setShowWhen(false)

        metadata?.let {
            val title = it.getString(android.media.MediaMetadata.METADATA_KEY_TITLE) ?: ""
            val artist = it.getString(android.media.MediaMetadata.METADATA_KEY_ARTIST) ?: ""
            val artwork = it.getBitmap(android.media.MediaMetadata.METADATA_KEY_ART)

            builder
                .setContentTitle(title)
                .setContentText(artist)
                .setLargeIcon(artwork ?: getDefaultArtwork())
                .setContentIntent(createPendingIntent())
        }

        // Add actions
        builder.addAction(createAction(R.drawable.ic_skip_previous, "Previous", PREVIOUS_ACTION))
        builder.addAction(createAction(
            if (state?.state == MediaControllerCompat.PlaybackState.STATE_PLAYING) R.drawable.ic_pause else R.drawable.ic_play,
            if (state?.state == MediaControllerCompat.PlaybackState.STATE_PLAYING) "Pause" else "Play",
            PLAY_PAUSE_ACTION
        ))
        builder.addAction(createAction(R.drawable.ic_skip_next, "Next", NEXT_ACTION))

        return builder.build()
    }

    private fun createAction(iconRes: Int, title: String, action: String): NotificationCompat.Action {
        val intent = Intent(context, MediaButtonReceiver::class.java)
            .setAction(action)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(
            iconRes, title, pendingIntent
        ).build()
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, com.musicapp.MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getDefaultArtwork(): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.ic_music_note_large)
    }

    companion object {
        const val PREVIOUS_ACTION = "com.musicapp.action.PREVIOUS"
        const val PLAY_PAUSE_ACTION = "com.musicapp.action.PLAY_PAUSE"
        const val NEXT_ACTION = "com.musicapp.action.NEXT"
    }
}