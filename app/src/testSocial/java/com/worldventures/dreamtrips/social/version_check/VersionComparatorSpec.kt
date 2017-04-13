package com.worldventures.dreamtrips.social.version_check

import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.modules.version_check.util.VersionComparator
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionComparatorSpec : BaseSpec ({

   describe("Compare versions of app tests") {
      context("check if comparing is calculated normally") {
         assertTrue { versionComparator.currentVersionIsOlderThanSuggested(stable1_7_3, suggestedVersion1_18_1) }
         assertFalse { versionComparator.currentVersionIsOlderThanSuggested(stable1_0_0, suggestedVersion1_0_0) }
         assertFalse { versionComparator.currentVersionIsOlderThanSuggested(stable1_7_3, suggestedVersion0_8_1) }
         assertTrue { versionComparator.currentVersionIsOlderThanSuggested(stable1_0_0, suggestedVersion1_18_0) }
      }

      context("check that comparator performs normally even if build types of current versions are different ") {
         assertFalse { versionComparator.currentVersionIsOlderThanSuggested(socialDebug1_18_0, suggestedVersion1_18_0) }
         assertFalse { versionComparator.currentVersionIsOlderThanSuggested(socialRelease1_18_1, suggestedVersion1_18_0) }
         assertTrue { versionComparator.currentVersionIsOlderThanSuggested(stablePreprod1_17_0, suggestedVersion1_18_0) }
         assertTrue { versionComparator.currentVersionIsOlderThanSuggested(stable1_7_3, suggestedVersion1_18_1) }
      }
   }

}) {
   companion object {
      val versionComparator = VersionComparator()

      val socialDebug1_18_0   = "1.18.0-dev-social-stage-0-debug"
      val socialRelease1_18_1   = "1.18.0-dev-social-stage-1"
      val stablePreprod1_17_0  = "1.17.0-preprod-23"
      val stable1_7_3     = "1.7.3"
      val stable1_0_0     = "1.0.0"
      val suggestedVersion1_18_0  = "1.18.0"
      val suggestedVersion1_18_1  = "1.18.01"
      val suggestedVersion0_8_1 = "0.8.1"
      val suggestedVersion1_0_0 = "1.0.0"
   }
}