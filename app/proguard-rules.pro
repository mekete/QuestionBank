# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/makata/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepattributes Signature


-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

#https://stackoverflow.com/questions/15760581/android-proguard-settings-for-facebook/19141837#19141837
-keep class com.facebook.** { *; }
-keep class com.google.gson.** { *; }


#firebase
-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**

#model classes
-keepclassmembers class net.kerod.android.questionbank.model.** {*;}


-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

#start  google-play-services
# Needed to keep generic types and @Key annotations accessed via reflection
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault
-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}
# Needed by google-http-client-android when linking against an older platform version
-dontwarn com.google.api.client.extensions.android.**
# Needed by google-api-client-android when linking against an older platform version
-dontwarn com.google.api.client.googleapis.extensions.android.**
# Needed by google-play-services when linking against an older platform version
-dontwarn com.google.android.gms.**
# com.google.client.util.IOUtils references java.nio.file.Files when on Java 7+
-dontnote java.nio.file.Files, java.nio.file.Path
# Suppress notes on LicensingServices
-dontnote **.ILicensingService
# Suppress warnings on sun.misc.Unsafe
-dontnote sun.misc.Unsafe
-dontwarn sun.misc.Unsafe
#///???????
-dontwarn sun.misc.BASE64Encoder  # check
#end  google-play-services

#MathView  Warning:com.x5.util.ObjectDataMap$
#these doesnt fix
-keep class net.minidev.json.** { *; }
-keep class com.madrobot.beans.** { *; }
-keep class java.beans.** { *; }
-keep class org.cheffo.jeplite.** { *; }
-keep class com.x5.util.** { *; }
-dontwarn com.x5.util.**