# ── Source map ────────────────────────────────────────────────────────────────
# Keep line numbers so crash stack traces are readable.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Room ──────────────────────────────────────────────────────────────────────
# Entity fields are read reflectively when mapping cursor rows to objects.
-keep class com.amr.coursemate.data.model.** { *; }

# DAO interfaces are referenced by name in Room-generated code.
-keep interface com.amr.coursemate.data.dao.** { *; }

# Database class is instantiated via Room.databaseBuilder.
-keep class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    abstract !public *;
}

# ── Kotlin ────────────────────────────────────────────────────────────────────
-keepclassmembers class kotlin.Metadata { *; }

# Coroutine state machines use volatile fields that R8 must not strip.
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# ── Jetpack Lifecycle / ViewModel ─────────────────────────────────────────────
# ViewModelProvider instantiates ViewModels and their Factories by reflection.
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * implements androidx.lifecycle.ViewModelProvider$Factory {
    public <init>(...);
}

# ── Android general ───────────────────────────────────────────────────────────
# Custom views inflated from XML need their two- and three-argument constructors.
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Parcelable CREATOR fields are accessed by the framework via reflection.
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# R inner classes are sometimes accessed reflectively by libraries.
-keepclassmembers class **.R$* {
    public static <fields>;
}

# BuildConfig is read directly in AboutActivity.
-keep class com.amr.coursemate.BuildConfig { *; }

# ── Serialisation (enum safety) ───────────────────────────────────────────────
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}