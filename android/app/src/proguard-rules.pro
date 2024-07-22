-keep class com.securemetric.myidreader.** { *; }
-keep class com.securemetric.reader.myid.License { *; }
-keep class com.securemetric.reader.myid.License$LicenseInfo { *; }
-keepclassmembers class com.securemetric.reader.myid.License$LicenseInfo {
    *;
}
-keepclassmembers class com.securemetric.reader.myid.License {
    *;
}
-dontwarn com.securemetric.**
