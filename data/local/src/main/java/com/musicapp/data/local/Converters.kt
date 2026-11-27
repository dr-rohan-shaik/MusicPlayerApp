package com.musicapp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.List

class Converters {
    private val gson = Gson()
    private val stringListType: Type = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(json: String?): List<String> {
        return json?.let { gson.fromJson(it, stringListType) } ?: emptyList()
    }

    @TypeConverter
    fun fromInt(value: Int?): Boolean? = value?.let { it == 1 }

    @TypeConverter
    fun toInt(value: Boolean?): Int? = value?.let { if (it) 1 else 0 }

    @TypeConverter
    fun fromTrackSource(source: com.musicapp.core.model.TrackSource?): Int? = source?.ordinal

    @TypeConverter
    fun toTrackSource(value: Int?): com.musicapp.core.model.TrackSource? = value?.let { com.musicapp.core.model.TrackSource.values()[it] }

    @TypeConverter
    fun fromPlaylistSource(source: com.musicapp.core.model.PlaylistSource?): Int? = source?.ordinal

    @TypeConverter
    fun toPlaylistSource(value: Int?): com.musicapp.core.model.PlaylistSource? = value?.let { com.musicapp.core.model.PlaylistSource.values()[it] }

    @TypeConverter
    fun fromAlbumType(type: com.musicapp.core.model.AlbumType?): Int? = type?.ordinal

    @TypeConverter
    fun toAlbumType(value: Int?): com.musicapp.core.model.AlbumType? = value?.let { com.musicapp.core.model.AlbumType.values()[it] }

    @TypeConverter
    fun fromShuffleMode(mode: com.musicapp.core.data.ShuffleMode?): Int? = mode?.ordinal

    @TypeConverter
    fun toShuffleMode(value: Int?): com.musicapp.core.data.ShuffleMode? = value?.let { com.musicapp.core.data.ShuffleMode.values()[it] }

    @TypeConverter
    fun fromRepeatMode(mode: com.musicapp.core.data.RepeatMode?): Int? = mode?.ordinal

    @TypeConverter
    fun toRepeatMode(value: Int?): com.musicapp.core.data.RepeatMode? = value?.let { com.musicapp.core.data.RepeatMode.values()[it] }

    @TypeConverter
    fun fromPlaybackQueue(queue: com.musicapp.core.data.PlaybackQueue?): String? = queue?.let { gson.toJson(it) }

    @TypeConverter
    fun toPlaybackQueue(json: String?): com.musicapp.core.data.PlaybackQueue? = json?.let { gson.fromJson(it, com.musicapp.core.data.PlaybackQueue::class.java) }
}