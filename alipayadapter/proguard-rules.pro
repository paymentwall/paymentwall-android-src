# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/paymentwall/Android/Sdk/tools/proguard/proguard-android.txt
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

-dontshrink
-keepparameternames
-optimizationpasses 5
-verbose
-dontusemixedcaseclassnames
-renamesourcefileattribute SourceFile
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-allowaccessmodification
-dontpreverify

-keepattributes *Annotation*,Signature,InnerClasses
-dontwarn android.support.**
-whyareyoukeeping class **

-keep class android.support.**
-keepclasseswithmembernames class android.support.** { *; }
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.**
-keep class android.support.annotation.**
-keep class com.android.**

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class com.paymentwall.alipayadapter.AlipayAdapter {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.alipayadapter.PsAlipay {
    <fields>;
    <methods>;
}
