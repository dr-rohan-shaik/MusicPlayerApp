package com.musicapp.di

import com.musicapp.core.data.AlbumRepository
import com.musicapp.core.data.ArtistRepository
import com.musicapp.core.data.PlaylistRepository
import com.musicapp.core.data.SearchRepository
import com.musicapp.core.data.TrackRepository
import com.musicapp.core.data.PlaybackRepository
import com.musicapp.core.domain.GetHomeContentUseCase
import com.musicapp.core.domain.SearchUseCase
import com.musicapp.core.domain.PlaybackUseCase
import com.musicapp.core.domain.PlaylistUseCase
import com.musicapp.data.repository.AlbumRepositoryImpl
import com.musicapp.data.repository.ArtistRepositoryImpl
import com.musicapp.data.repository.PlaylistRepositoryImpl
import com.musicapp.data.repository.SearchRepositoryImpl
import com.musicapp.data.repository.TrackRepositoryImpl
import com.musicapp.data.repository.PlaybackRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideTrackRepository(impl: TrackRepositoryImpl): TrackRepository = impl

    @Provides
    fun providePlaylistRepository(impl: PlaylistRepositoryImpl): PlaylistRepository = impl

    @Provides
    fun provideAlbumRepository(impl: AlbumRepositoryImpl): AlbumRepository = impl

    @Provides
    fun provideArtistRepository(impl: ArtistRepositoryImpl): ArtistRepository = impl

    @Provides
    fun provideSearchRepository(impl: SearchRepositoryImpl): SearchRepository = impl

    @Provides
    fun providePlaybackRepository(impl: PlaybackRepositoryImpl): PlaybackRepository = impl
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetHomeContentUseCase(useCase: GetHomeContentUseCase): GetHomeContentUseCase = useCase

    @Provides
    fun provideSearchUseCase(useCase: SearchUseCase): SearchUseCase = useCase

    @Provides
    fun providePlaybackUseCase(useCase: PlaybackUseCase): PlaybackUseCase = useCase

    @Provides
    fun providePlaylistUseCase(useCase: PlaylistUseCase): PlaylistUseCase = useCase
}