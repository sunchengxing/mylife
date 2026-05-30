# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.mylife.app.data.** { *; }
-keep class com.google.gson.** { *; }

# Room - keep all entities, DAOs, and database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**

# Keep recipe data classes
-keep class com.mylife.app.data.recipe.** { *; }

# Coil
-dontwarn coil.**
-keep class coil.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile **;
}

# Compose
-dontwarn androidx.compose.**
