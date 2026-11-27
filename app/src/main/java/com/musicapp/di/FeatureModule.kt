package com.musicapp.di

import com.musicapp.core.data.TrackRepository
import com.musicapp.core.data.PlaylistRepository
import com.musicapp.core.data.AlbumRepository
import com.musicapp.core.data.ArtistRepository
import com.musicapp.core.data.SearchRepository
import com.musicapp.core.data.PlaybackRepository
import com.musicapp.core.domain.GetHomeContentUseCase
import com.musicapp.core.domain.SearchUseCase
import com.musicapp.core.domain.PlaybackUseCase
import com.musicapp.core.domain.PlaylistUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class FeatureModule {

    @Binds
    abstract fun bindTrackRepository(impl: com.musicapp.data.repository.TrackRepositoryImpl): TrackRepository

    @Binds
    abstract fun bindPlaylistRepository(impl: com.musicapp.data.repository.PlaylistRepositoryImpl): PlaylistRepository

    @Binds
    abstract fun bindAlbumRepository(impl: com.musicapp.data.repository.AlbumRepositoryImpl): AlbumRepository

    @Binds
    abstract fun bindArtistRepository(impl: com.musicapp.data.repository.ArtistRepositoryImpl): ArtistRepository

    @Binds
    abstract fun bindSearchRepository(impl: com.musicapp.data.repository.SearchRepositoryImpl): SearchRepository

    @Binds
    abstract fun bindPlaybackRepository(impl: com.musicapp.data.repository.PlaybackRepositoryImpl): PlaybackRepository

    @Binds
    abstract fun bindGetHomeContentUseCase(impl: GetHomeContentUseCase): GetHomeContentUseCase

    @Binds
    abstract fun bindSearchUseCase(impl: SearchUseCase): SearchUseCase

    @Binds
    abstract fun bindPlaybackUseCase(impl: PlaybackUseCase): PlaybackUseCase

    @Binds
    abstract fun bindPlaylistUseCase(impl: PlaylistUseCase): PlaylistUseCase
}