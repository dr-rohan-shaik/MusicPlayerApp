# Music Player ProGuard Rules

# Keep Hilt generated code
-keep class dagger.hilt.** { *; }
-keep class com.musicapp.**Hilt** { *; }

# Keep Room entities and DAOs
-keep class com.musicapp.data.local.entities.** { *; }
-keep class com.musicapp.data.local.dao.** { *; }
-keep class com.musicapp.data.local.AppDatabase { *; }

# Keep Retrofit/Gson models
-keep class com.musicapp.data.remote.** { *; }
-keep class com.musicapp.core.model.** { *; }

# Keep Media3/ExoPlayer classes
-keep class androidx.media3.** { *; }
-keep class com.google.android.exoplayer2.** { *; }

# Keep Coil image loading
-keep class coil.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep Kotlin serialization
-keep class kotlinx.serialization.** { *; }

# Keep Hilt ViewModels
-keep class androidx.hilt.** { *; }

# Keep Material3 components
-keep class androidx.compose.material3.** { *; }

# Keep Navigation
-keep class androidx.navigation.** { *; }

# Keep Paging
-keep class androidx.paging.** { *; }

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep WorkManager
-keep class androidx.work.** { *; }

# Keep OkHttp/Retrofit
-keep class okhttp3.** { *; }
-keep class retrofit2.** { *; }

# Keep Gson
-keep class com.google.gson.** { *; }

# Keep Kotlinx datetime if used
-keep class kotlinx.datetime.** { *; }

# Keep custom classes
-keep class com.musicapp.** { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Suppress warnings
-dontwarn kotlinx.coroutines.**
-dontwarn kotlinx.serialization.**
-dontwarn androidx.hilt.**
-dontwarn dagger.hilt.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn com.google.gson.**
-dontwarn coil.**
-dontwarn androidx.media3.**

# Optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable

# Keep annotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod