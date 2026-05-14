# Add project specific ProGuard rules here.
-keep class com.hjplan.app.data.model.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
