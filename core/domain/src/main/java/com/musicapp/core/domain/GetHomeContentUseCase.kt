package com.musicapp.core.domain

import com.musicapp.core.data.AlbumRepository
import com.musicapp.core.data.PlaylistRepository
import com.musicapp.core.data.TrackRepository
import com.musicapp.core.model.HomeContent
import javax.inject.Inject

class GetHomeContentUseCase @Inject constructor(
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepository,
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(): Result<HomeContent> = try {
        val recentlyPlayed = trackRepository.getRecentlyPlayed(10).getOrThrow()
        val recommended = trackRepository.getRecommendedTracks(20).getOrThrow()
        val newReleases = albumRepository.getNewReleases(10).getOrThrow()
        val madeForYou = playlistRepository.getMadeForYouPlaylists(10).getOrThrow()
        val popularNearYou = playlistRepository.getPopularNearYou(10).getOrThrow()

        Result.success(HomeContent(
            recentlyPlayed = recentlyPlayed,
            recommended = recommended,
            newReleases = newReleases,
            madeForYou = madeForYou,
            popularNearYou = popularNearYou
        ))
    } catch (e: Exception) {
        Result.failure(e)
    }
}