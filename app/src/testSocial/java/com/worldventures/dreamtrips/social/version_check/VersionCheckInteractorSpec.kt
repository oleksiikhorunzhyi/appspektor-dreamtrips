package com.worldventures.dreamtrips.social.version_check

import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.version_check.model.UpdateRequirement
import com.worldventures.dreamtrips.modules.version_check.model.api.Category
import com.worldventures.dreamtrips.modules.version_check.model.api.ConfigSetting
import com.worldventures.dreamtrips.modules.version_check.model.api.UpdateRequirementDTO
import com.worldventures.dreamtrips.modules.version_check.service.VersionCheckInteractor
import com.worldventures.dreamtrips.modules.version_check.service.command.VersionCheckCommand
import com.worldventures.dreamtrips.modules.version_check.service.storage.UpdateRequirementStorage
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import org.mockito.internal.verification.VerificationModeFactory
import rx.observers.TestSubscriber
import kotlin.test.assertEquals

class VersionCheckInteractorSpec : BaseSpec({

   describe("Get feedback command") {
      setup(makeCorrectConfigHttpService())

      context ("should get correct update requirement") {
         val testSub = TestSubscriber<ActionState<VersionCheckCommand>>()
         versionCheckInteractor.versionCheckPipe().createObservable(VersionCheckCommand()).subscribe(testSub)
         assertActionSuccess(testSub) {
            checkUpdateRequirementCorrect(it.result, STUB_UPDATE_REQUIREMENT)
            true
         }
      }

      context("should restore update requirement from DB") {
         verify(mockDb, VerificationModeFactory.calls(2))
      }
   }

}) {
   companion object {
      val mockDb: SnappyRepository = spy()
      val STUB_UPDATE_REQUIREMENT = makeStubUpdateRequirement()

      lateinit var versionCheckInteractor: VersionCheckInteractor

      fun setup(httpService: ActionService) {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(setOf(UpdateRequirementStorage(mockDb)))
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService.wrapStub().wrapCache())
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }

         versionCheckInteractor = VersionCheckInteractor(janet)
      }

      fun makeCorrectConfigHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(STUB_UPDATE_REQUIREMENT)) { it.url.contains("/ConfigService/api/config/application") }
               .build()
      }

      fun makeStubUpdateRequirement(): UpdateRequirementDTO {
         val updateRequirement = UpdateRequirementDTO()
         updateRequirement.scope = "APPLICATION"

         val category = Category()
         category.name = "version"

         val androidVersionConfigSetting = ConfigSetting()
         androidVersionConfigSetting.name = "android_version"
         androidVersionConfigSetting.value = "1.8.1"

         val timestampConfigSetting = ConfigSetting()
         timestampConfigSetting.name = "date"
         timestampConfigSetting.value = "1488279258"

         category.configSettings = listOf(androidVersionConfigSetting, timestampConfigSetting)

         updateRequirement.categories = listOf(category)

         return updateRequirement
      }

      fun checkUpdateRequirementCorrect(updateRequirement: UpdateRequirement,
                                          updateRequirementDTO: UpdateRequirementDTO) {
         assertEquals(updateRequirement.appVersion, updateRequirementDTO.categories[0].configSettings[0].value)
         val mappedTimestamp = java.lang.Long.valueOf(updateRequirementDTO.categories[0].configSettings[1].value) * 1000
         assertEquals(updateRequirement.timeStamp, mappedTimestamp)
      }
   }
}
