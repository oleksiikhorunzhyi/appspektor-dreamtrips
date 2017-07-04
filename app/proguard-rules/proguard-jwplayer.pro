-keepattributes *longtailvideo*
-keep class com.longtailvideo.*{ *; }
-dontwarn com.longtailvideo.*
-keepclasseswithmembernames class * {
native <methods>;
}