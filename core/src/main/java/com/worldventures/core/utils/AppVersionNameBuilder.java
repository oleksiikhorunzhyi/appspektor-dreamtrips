package com.worldventures.core.utils;

/**
 * For generate build version name for server side.
 */
public class AppVersionNameBuilder {
   private final String versionMajor;
   private final String versionMinor;
   private final String versionPatch;
   private final String versionBuild;
   private final String flavor;
   private final String buildType;

   public AppVersionNameBuilder(String versionMajor, String versionMinor, String versionPatch,
         String versionBuild, String flavor, String buildType) {
      this.versionMajor = versionMajor;
      this.versionMinor = versionMinor;
      this.versionPatch = versionPatch;
      this.versionBuild = versionBuild;
      this.flavor = flavor;
      this.buildType = buildType;
   }

   /**
    * According to http://semver.org/
    * example: 1.5.0-beta-16
    */
   public String getSemanticVersionName() {
      String name = "";
      if (isCurrentFlavor("dev")) {
         name = "dev";
      } else {
         boolean release = isCurrentBuildType("release");
         boolean debug = isCurrentBuildType("debug");
         if (isCurrentFlavor("stage")) {
            if (debug) name = "alpha";
            else if (release) name = "beta";

         } else if (isCurrentFlavor("prod")) {
            if (release) name = "prod";
         }
      }
      return generateName(name);
   }

   /**
    * According to http://semver.org/
    * example: 1.5.0
    */
   public String getReleaseSemanticVersionName() {
      return String.format("%s.%s.%s", versionMajor, versionMinor, versionPatch);
   }

   private boolean isCurrentFlavor(String flavor) {
      return this.flavor.contains(flavor);
   }

   private boolean isCurrentBuildType(String flavor) {
      return buildType.equals(flavor);
   }

   private String generateName(String name) {
      String result = String.format("%s.%s.%s-%s", versionMajor, versionMinor, versionPatch, versionBuild);
      if (!android.text.TextUtils.isEmpty(name)) {
         result = String.format("%s-%s", result, name);
      }
      return result;
   }
}
