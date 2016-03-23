# Keep the BuildConfig
-keep class **.BuildConfig { *; }

# keep own classes
-keep class com.techery.** { *; }
-keep class com.worldventures.dreamtrips.** { *; }
-keep class com.messenger.** { *; }
-keep class cn.pedant.SweetAlert.** { *; }

-dontwarn pl.itako.iconversion.**

-keepattributes InnerClasses