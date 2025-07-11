# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- Preferences Data Store ---

-keepclassmembers class androidx.datastore.preferences.PreferencesProto$PreferenceMap {
    private androidx.datastore.preferences.protobuf.MapFieldLite preferences_;
}

-keepclassmembers class androidx.datastore.preferences.PreferencesProto$Value {
     private java.lang.Object value_;
     private int valueCase_;
}

# --- Retrofit & Gson Rules ---

# Keep Retrofit interfaces to avoid stripping API definitions
-keep interface retrofit2.**

# Keep Retrofit annotations (e.g., @GET, @POST, etc.)
-keepattributes *Annotation*

# Keep Retrofit response model classes (if using Gson converter)
-keep class * implements com.google.gson.TypeAdapter { *; }
-keep class * implements com.google.gson.JsonDeserializer { *; }
-keep class * implements com.google.gson.JsonSerializer { *; }

# Keep classes annotated with @SerializedName (needed for Gson)
-keep class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Gson model classes (if using GsonConverterFactory)
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter { *; }

# Prevent Gson from stripping @Expose or @SerializedName annotations
-keepattributes *Annotation*

# Keep classes that use @Serializable (for kotlinx.serialization)
-keep @kotlinx.serialization.Serializable class * { *; }

# Keep Kotlin serialization-related metadata
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Keep the generated companion object methods (important for decoding)
-keepclassmembers class * {
    static ** Companion;
}

# Prevent obfuscation of the Serialization constructor
-keepclassmembers class * {
    <init>(kotlinx.serialization.descriptors.SerialDescriptor, ...);
}