# These options are the minimal options for a functioning application
# using Proguard and the AWS SDK 2.1.5 for Android

-keep class com.amazonaws.org.apache.commons.**         { *; }
-keep class com.amazonaws.services.sqs.QueueUrlHandler  { *; }
-keep class com.amazonaws.javax.xml.transform.sax.*     { public *; }
-keep class com.amazonaws.javax.xml.stream.**           { *; }
-keep class com.amazonaws.services.**.model.*Exception* { *; }
-keep class com.amazonaws.internal.**                   { *; }
-keep class org.codehaus.**                             { *; }
-keep class org.joda.time.tz.Provider                   { *; }
-keep class org.joda.time.tz.NameProvider               { *; }
-keepattributes Signature,*Annotation*,EnclosingMethod
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class com.amazonaws.** { *; }

-dontwarn com.fasterxml.jackson.databind.**
-dontwarn javax.xml.stream.events.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.http.conn.scheme.**
-dontwarn org.apache.http.annotation.**
-dontwarn org.ietf.jgss.**
-dontwarn org.joda.convert.**
-dontwarn com.amazonaws.org.joda.convert.**
-dontwarn org.w3c.dom.bootstrap.**

#SDK split into multiple jars so certain classes may be referenced but not used
-dontwarn com.amazonaws.org.apache.http.impl.auth.**
-dontwarn com.amazonaws.services.s3.**
-dontwarn com.amazonaws.services.sqs.**
-dontwarn com.amazonaws.util.json.JacksonFactory*
-dontwarn com.fasterxml.jackson.core.JsonFactory*
-dontwarn com.fasterxml.jackson.core.JsonGenerator*
-dontwarn com.fasterxml.jackson.core.JsonToken*
-dontwarn com.fasterxml.jackson.core.JsonParser*
-dontwarn com.fasterxml.jackson.core.JsonFactory*
-dontwarn com.fasterxml.jackson.core.JsonFactory*
-dontwarn org.apache.commons.logging.LogFactory*

-dontnote com.amazonaws.services.sqs.QueueUrlHandler