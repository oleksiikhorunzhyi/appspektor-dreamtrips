# keep hidden mechanism for file picking on SDK < 20
-keepclassmembers class * extends android.webkit.WebChromeClient {
     public void openFileChooser(...);
}