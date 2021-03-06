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

#-injars       /home/paymentwall/temp/pwlocal-jar-build/pwlocalsdk-unsigned-original.jar
                                                               #-outjars      /home/paymentwall/temp/pwlocal-jar-build/pwlocalsdk-unsigned-obfuscated.jar
                                                               #-libraryjars  /home/paymentwall/Android/Sdk/platforms/android-22/android.jar
                                                               #-printmapping /home/paymentwall/temp/pwlocal-jar-build/out.map
-dontshrink
-keepparameternames
-optimizationpasses 5
-verbose
-dontusemixedcaseclassnames
-renamesourcefileattribute SourceFile
-optimizations !class/unboxing/enum,!code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
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

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class com.paymentwall.pwunifiedsdk.R {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.R.** {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.core.UnifiedRequest {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.core.PaymentSelectionActivity {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.object.ExternalPs {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.util.ResponseCode {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.util.Key {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.brick.core.Brick {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.brick.core.BrickError {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.brick.core.BrickError$Kind {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.brick.core.BrickHelper {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.core.BaseFragment {
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.util.MiscUtils{
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.util.PwUtils{
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.core.LocalPsFragment{
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.core.LocalPsFragment$IOnRequestPermissionCallback{
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.mobiamo.core.MobiamoDialogActivity{
    <fields>;
    <methods>;
}

-keep class com.paymentwall.pwunifiedsdk.mobiamo.core.MobiamoBroadcastReceiver{
    <fields>;
    <methods>;
}

-keep public class com.paymentwall.sdk.pwlocal.utils.*{
    <fields>;
    <methods>;
}

-keep public class com.paymentwall.sdk.pwlocal.message.*{
    <fields>;
    <methods>;
}

-keep public class com.paymentwall.sdk.pwlocal.ui.*{
    <fields>;
    <methods>;
}

-keep class io.card.payments.CardScannerActivity{
    <fields>;
    <methods>;
}

-keep class io.card.payments.CardScanner{
    <fields>;
    <methods>;
}

-keep class io.card.payments.CreditCard{
    <fields>;
    <methods>;
}

-keep class org.jsoup.**

-keep class com.paymentwall.pwunifiedsdk.object.AlipayPs {
    <fields>;
    <methods>;
}

-keep class glide.** {
    <fields>;
    <methods>;
}