package com.worldventures.dreamtrips.modules.version_check.util;

public class VersionComparator {

   public boolean currentVersionIsOlderThanSuggested(String currentVersion, String suggestedVersion) {
      String[] currentVersionTokenized = currentVersion.split("\\.");
      String lastSegmentOfCurrentVersion = currentVersionTokenized[currentVersionTokenized.length - 1];
      // remove env and stream codes from the last segment of current version
      lastSegmentOfCurrentVersion = lastSegmentOfCurrentVersion.split("-")[0];
      currentVersionTokenized[currentVersionTokenized.length - 1] = lastSegmentOfCurrentVersion;

      String[] suggestedVersionTokenized = suggestedVersion.split("\\.");
      int versionsSize = Math.max(currentVersionTokenized.length, suggestedVersionTokenized.length);
      int[] currentVersionVersionsPartsArray = parseVersionParts(versionsSize, currentVersionTokenized);
      int[] suggestedVersionVersionsPartsArray = parseVersionParts(versionsSize, suggestedVersionTokenized);
      return currentVersionIsOlderThanSuggested(currentVersionVersionsPartsArray, suggestedVersionVersionsPartsArray);
   }

   private int[] parseVersionParts(int size, String[] stringVersions) {
      int[] intVersions = new int[size];
      for (int i = 0; i < intVersions.length; i++) {
         int version = 0;
         if (i < stringVersions.length) {
            version = Integer.parseInt(stringVersions[i]);
         }
         intVersions[i] = version;
      }
      return intVersions;
   }

   private boolean currentVersionIsOlderThanSuggested(int[] currentVersion, int[] newerVersion) {
      for (int i = 0; i < currentVersion.length; i++) {
         if (currentVersion[i] == newerVersion[i]) continue;
         return currentVersion[i] < newerVersion[i];
      }
      return false;
   }
}
