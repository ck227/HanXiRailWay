# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\ck\android_studio_sdk/tools/proguard/proguard-android.txt
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

-keep public class com.tencent.bugly.**{*;}
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

-keep class android.support.v4.** { *; }
-dontwarn android.support.v4.**
-dontskipnonpubliclibraryclassmembers


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep  com.google.gson.stream.** { *;class }

# Application classes that will be serialized/deserialized over Gson
-keep class com.cnbs.entity.** { *; }

##---------------End: proguard configuration for Gson  ----------



# 以下类过滤不混淆
-keep public class * extends com.umeng.**
# 以下包不进行过滤
-keep class com.umeng.** { *; }

-dontwarn com.umeng.update.c$a
-dontwarn com.umeng.update.net.DownloadingService$1
-dontwarn com.umeng.update.net.DownloadingService$b
-dontwarn com.umeng.update.net.c$c


