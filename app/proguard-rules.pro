-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses, Exceptions
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**

-keep class com.essence.essenceapp.BuildConfig { *; }

-keep class com.essence.essenceapp.**.data.api.** { *; }
-keep class com.essence.essenceapp.**.data.dto.** { *; }
-keep class com.essence.essenceapp.**.data.remote.** { *; }
-keep class com.essence.essenceapp.**.domain.model.** { *; }

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep class dagger.hilt.** { *; }
-keep class * extends androidx.lifecycle.ViewModel { <init>(...); }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }
-keepclassmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

-keep class org.schabi.newpipe.extractor.** { *; }
-dontwarn org.schabi.newpipe.extractor.**
-dontwarn org.mozilla.javascript.**
-dontwarn org.mozilla.javascript.tools.**
-dontwarn org.slf4j.**
-dontwarn org.jsoup.**
-dontwarn org.jsoup.helper.Re2jRegex**
-dontwarn com.google.re2j.**
-dontwarn java.beans.**
-dontwarn javax.script.**

-keep class coil.** { *; }
-dontwarn coil.**

-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

-keepclassmembers class **$WhenMappings {
    <fields>;
}

-keepclassmembers class kotlin.Metadata { *; }