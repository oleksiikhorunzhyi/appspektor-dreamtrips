package com.worldventures.dreamtrips.social.version_check

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.config.model.Category
import com.worldventures.dreamtrips.api.config.model.ConfigSetting
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.version_check.model.Configuration
import com.worldventures.dreamtrips.modules.version_check.model.converter.ConfigurationConverter
import com.worldventures.dreamtrips.modules.version_check.service.AppConfigurationInteractor
import com.worldventures.dreamtrips.modules.version_check.service.command.LoadConfigurationCommand
import com.worldventures.dreamtrips.modules.version_check.service.storage.UpdateRequirementStorage
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import kotlin.test.assertEquals

typealias ApiConfiguration = com.worldventures.dreamtrips.api.config.model.Configuration

class AppConfigurationInteractorSpec : BaseSpec({

   describe("Get feedback command") {
      setup(makeCorrectConfigHttpService())

      it("should get correct update requirement") {
         val testSub = TestSubscriber<ActionState<LoadConfigurationCommand>>()
         configurationInteractor.loadConfigurationPipe().createObservable(LoadConfigurationCommand()).subscribe(testSub)
         assertActionSuccess(testSub) {
            checkConfigurationCorrect(it.result, STUB_UPDATE_REQUIREMENT)
            true
         }
      }

      it("should restore update requirement from DB") {
         verify(mockDb, times(1)).appUpdateRequirement
      }
   }

}) {
   companion object {
      val mockDb: SnappyRepository = spy()
      val STUB_UPDATE_REQUIREMENT = makeStubUpdateRequirement()

      lateinit var configurationInteractor: AppConfigurationInteractor

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
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { getMappery() }

         configurationInteractor = AppConfigurationInteractor(janet)
      }

      fun getMappery(): MapperyContext {
         val mappery = Mappery.Builder()
         val converter = ConfigurationConverter()
         mappery.map(converter.sourceClass()).to(converter.targetClass(), converter)
         return mappery.build()
      }

      fun makeCorrectConfigHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(STUB_UPDATE_REQUIREMENT)) { it.url.contains("api/config") }
               .build()
      }

      fun makeStubUpdateRequirement(): ApiConfiguration {
         val updateRequirement = mock<ApiConfiguration>()
         whenever(updateRequirement.scope()).thenReturn("APPLICATION")
         val versionCategory = makeVersionCategory()
         val videoCategory = makeVideoCategory()
         whenever(updateRequirement.categories()).thenReturn(listOf(versionCategory, videoCategory))
         return updateRequirement
      }

      private fun makeVideoCategory(): Category {
         val videoCategory = mock<Category>()
         whenever(videoCategory.name()).thenReturn("video")
         val videoSetting = makeSetting("android_max_selectable_video_duration", "90")
         whenever(videoCategory.configSettings()).thenReturn(listOf(videoSetting))
         return videoCategory
      }

      private fun makeVersionCategory(): Category {
         val versionCategory = mock<Category>()
         whenever(versionCategory.name()).thenReturn("version")
         val androidVersionConfigSetting = makeSetting("android_version", "1.8.1")
         val timestampConfigSetting = makeSetting("android_date", "1488279258")
         whenever(versionCategory.configSettings()).thenReturn(listOf(androidVersionConfigSetting, timestampConfigSetting))
         return versionCategory
      }

      fun makeSetting(name: String, value: String): ConfigSetting {
         val configSetting = mock<ConfigSetting>()
         whenever(configSetting.name()).thenReturn(name)
         whenever(configSetting.value()).thenReturn(value)
         return configSetting
      }

      fun checkConfigurationCorrect(configuration: Configuration,
                                    apiCongiguration: ApiConfiguration) {
         val updateRequirement = configuration.updateRequirement
         assertEquals(updateRequirement.appVersion, apiCongiguration.categories()[0].configSettings()[0].value())
         val mappedTimestamp = java.lang.Long.valueOf(apiCongiguration.categories()[0].configSettings()[1].value()) * 1000
         assertEquals(updateRequirement.timeStamp, mappedTimestamp)

         assertEquals(configuration.videoRequirement.videoMaxLength.toString(),
               apiCongiguration.categories()[1].configSettings()[0].value())
      }
   }
}
