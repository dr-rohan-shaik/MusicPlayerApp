package com.musicapp.di

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.rx.preferenceDataStoreFactory
import androidx.datastore.rx.RxDataStore
import androidx.room.Room
import com.musicapp.data.local.AppDatabase
import com.musicapp.data.local.dao.*
import com.musicapp.data.remote.AuthInterceptor
import com.musicapp.data.remote.MusicApiService
import com.musicapp.data.remote.TokenManager
import com.musicapp.media.ExoPlayerController
import com.musicapp.media.MediaNotificationProvider
import com.musicapp.media.PlayerController
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.Singleton
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import dagger.hilt.components.ViewModelComponent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "music.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideTrackDao(db: AppDatabase): TrackDao = db.trackDao()

    @Provides
    @Singleton
    fun provideAlbumDao(db: AppDatabase): AlbumDao = db.albumDao()

    @Provides
    @Singleton
    fun provideArtistDao(db: AppDatabase): ArtistDao = db.artistDao()

    @Provides
    @Singleton
    fun providePlaylistDao(db: AppDatabase): PlaylistDao = db.playlistDao()

    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun providePlaybackStateDao(db: AppDatabase): PlaybackStateDao = db.playbackStateDao()

    @Provides
    @Singleton
    fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()

    @Provides
    @Singleton
    fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): RxDataStore<Preferences> {
        return preferenceDataStoreFactory("music_prefs").create(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMusicApiService(
        okHttpClient: OkHttpClient
    ): MusicApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.musicapp.example.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context,
        dataStore: RxDataStore<Preferences>
    ): TokenManager = TokenManager(context, dataStore)
}

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideMediaSession(@ApplicationContext context: Context): androidx.media3.session.MediaSession {
        return androidx.media3.session.MediaSession.Builder(context, "MusicPlayerSession")
            .setFlags(
                androidx.media3.session.MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or
                androidx.media3.session.MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideMediaController(mediaSession: androidx.media3.session.MediaSession): androidx.media3.session.MediaController {
        return mediaSession.controller
    }

    @Provides
    @Singleton
    fun provideMediaSessionCompat(@ApplicationContext context: Context): androidx.media3.session.MediaSessionCompat {
        return androidx.media3.session.MediaSessionCompat(context, "MusicPlayerSession").apply {
            setFlags(
                androidx.media3.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                androidx.media3.session.MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            isActive = true
        }
    }

    @Provides
    @Singleton
    fun provideMediaControllerCompat(mediaSessionCompat: androidx.media3.session.MediaSessionCompat): androidx.media3.session.MediaControllerCompat {
        return mediaSessionCompat.controller
    }

    @Provides
    @Singleton
    fun provideNotificationProvider(
        @ApplicationContext context: Context,
        mediaSessionCompat: androidx.media3.session.MediaSessionCompat,
        mediaControllerCompat: androidx.media3.session.MediaControllerCompat
    ): MediaNotificationProvider = MediaNotificationProvider(context, mediaSessionCompat, mediaControllerCompat)

    @Provides
    @Singleton
    fun providePlayerController(
        @ApplicationContext context: Context,
        mediaSession: androidx.media3.session.MediaSession,
        notificationProvider: MediaNotificationProvider
    ): PlayerController = ExoPlayerController(
        context = context,
        mediaSession = mediaSession,
        mediaController = mediaSession.controller,
        notificationProvider = notificationProvider,
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO + kotlinx.coroutines.SupervisorJob())
    )
}