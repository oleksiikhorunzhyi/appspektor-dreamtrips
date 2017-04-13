package com.worldventures.dreamtrips.social.version_check

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.common.delegate.system.AppInfoProvider
import com.worldventures.dreamtrips.modules.version_check.delegate.VersionUpdateDelegate
import com.worldventures.dreamtrips.modules.version_check.delegate.VersionUpdateUiDelegate
import com.worldventures.dreamtrips.modules.version_check.model.UpdateRequirement
import com.worldventures.dreamtrips.modules.version_check.util.VersionComparator
import org.mockito.Mockito.*

class VersionUpdateDelegateSpec : BaseSpec ({

   describe("Version update delegate") {

      setupForSuite()

      beforeEach {
         setupForTest();
      }

      it ("should show optional update dialog") {
         processRequirement(UpdateRequirement(NEWER_VERSION, getTimeInFuture()))
         verify(versionUpdateUiDelegate, times(1)).showOptionalUpdateDialog(anyLong())
      }

      it ("should show force update dialog") {
         processRequirement(UpdateRequirement(NEWER_VERSION, getTimeInThePast()))
         verify(versionUpdateUiDelegate, times(1)).showForceUpdateDialog()
      }

      it ("should not show any dialogs when suggested version same as current") {
         processRequirement(UpdateRequirement(CURRENT_VERSION, getTimeInThePast()))
         verify(versionUpdateUiDelegate, never()).showForceUpdateDialog()
         verify(versionUpdateUiDelegate, never()).showOptionalUpdateDialog(anyLong())
      }

      it ("should not show any dialogs when suggested version is older") {
         processRequirement(UpdateRequirement(OLDER_VERSION, getTimeInThePast()))
         verify(versionUpdateUiDelegate, never()).showForceUpdateDialog()
         verify(versionUpdateUiDelegate, never()).showForceUpdateDialog()
      }

      it ("should show dialog only once per session") {
         processRequirement(UpdateRequirement(NEWER_VERSION, getTimeInThePast()))
         verify(versionUpdateUiDelegate, times(1)).showForceUpdateDialog()
         processRequirement(UpdateRequirement(NEWER_VERSION, getTimeInThePast()))
         verify(versionUpdateUiDelegate, times(1)).showForceUpdateDialog()
      }
   }
}) {
   companion object {
      val OLDER_VERSION = "1.17"
      val NEWER_VERSION = "1.18.1"
      val CURRENT_VERSION = "1.18.0"

      val mockDb: SnappyRepository = spy()
      val versionComparator = VersionComparator()
      lateinit var versionUpdateUiDelegate: VersionUpdateUiDelegate
      val appInfoProvider = mock<AppInfoProvider>()

      lateinit var versionUpdateDelegate: VersionUpdateDelegate

      fun setupForSuite() {
         `when`(appInfoProvider.appVersion).thenReturn(CURRENT_VERSION)
      }

      fun setupForTest() {
         versionUpdateUiDelegate = spy<VersionUpdateUiDelegate>()
         versionUpdateDelegate = VersionUpdateDelegate(mockDb, versionComparator, versionUpdateUiDelegate,
               appInfoProvider)
      }

      fun getTimeInFuture(): Long {
         return System.currentTimeMillis() + 1000 * 1000
      }

      fun getTimeInThePast(): Long {
         return System.currentTimeMillis() - 1000 * 1000
      }

      fun processRequirement(updateRequirement: UpdateRequirement) {
         versionUpdateDelegate.processUpdateRequirement(updateRequirement)
      }
   }
}
