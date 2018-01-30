package com.worldventures.dreamtrips.modules.config.model.converter

import com.nhaarman.mockito_kotlin.mock
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.config.model.ImmutableCategory
import com.worldventures.dreamtrips.api.config.model.ImmutableConfigSetting
import com.worldventures.dreamtrips.modules.config.model.VideoRequirement
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import kotlin.test.assertFailsWith
import com.worldventures.dreamtrips.api.config.model.ImmutableConfiguration as ApiConfiguration

class ConfigurationConverterSpec : BaseSpec({
   describe("Configuration converter") {

      xit("Configuration comes full") {
         setup()
         val convertedConfiguration = converter.convert(mapperyContext, apiConfiguration)

         assert(convertedConfiguration.travelBannerRequirement != null)
         convertedConfiguration.travelBannerRequirement?.let {
            assert(it.enabled)
            assert(it.url == MOCK_URL)
            assert(it.title == MOCK_TEXT)
         }
         assert(convertedConfiguration.updateRequirement != null)
         convertedConfiguration.updateRequirement?.let {
            assert(it.appVersion == MOCK_VERSION)
            assert(it.timeStamp == MOCK_TIMESTAMP)
         }
         assert(convertedConfiguration.videoRequirement.videoMaxLength == MOCK_LENGTH)
      }

      it("Video configuration is missed") {
         setup(defaultConfig(categories = listOf(defaultBannerCategory(), defaultVersionCategory())))
         val convertedConfiguration = converter.convert(mapperyContext, apiConfiguration)

         assert(convertedConfiguration.videoRequirement.videoMaxLength == VideoRequirement.DEFAULT_MAX_VIDEO_DURATION_SEC)
      }

      it("Banner configuration is missed") {
         setup(defaultConfig(categories = listOf(defaultVideoCategory(), defaultVersionCategory())))
         val convertedConfiguration = converter.convert(mapperyContext, apiConfiguration)

         assert(convertedConfiguration.travelBannerRequirement == null)
      }

      it("Version configuration is missed") {
         setup(defaultConfig(categories = listOf(defaultVideoCategory(), defaultBannerCategory())))
         val convertedConfiguration = converter.convert(mapperyContext, apiConfiguration)

         assert(convertedConfiguration.updateRequirement == null)
      }

      it("Wrong scope provided") {
         setup(defaultConfig("WRONG_SCOPE"))

         assertFailsWith(IllegalStateException::class) {
            converter.convert(mapperyContext, apiConfiguration)
         }
      }
   }
}) {
   companion object {
      private val MOCK_VERSION = "1.25.0"
      private val MOCK_LENGTH = 10000
      private val MOCK_URL = "dummy_url"
      private val MOCK_TEXT = "dummy_text"
      private val MOCK_TIMESTAMP = 10000000L

      lateinit var converter: ConfigurationConverter
      val mapperyContext: MapperyContext = mock()

      lateinit var apiConfiguration: ApiConfiguration

      fun setup(apiConfiguration: ApiConfiguration = defaultConfig()) {
         this.apiConfiguration = apiConfiguration
         this.converter = ConfigurationConverter()
      }

      private fun defaultConfig(scopeName: String = ConfigurationConverter.SCOPE_NAME,
                                categories: List<ImmutableCategory> = listOf(defaultBannerCategory(),
                                      defaultVersionCategory(), defaultVideoCategory())
      ) = ApiConfiguration.builder()
            .scope(scopeName)
            .addAllCategories(categories)
            .build()

      private fun defaultVideoCategory() = ImmutableCategory.builder()
            .name(ConfigurationConverter.CATEGORY_NAME_VIDEO)
            .addConfigSettings(configSetting(ConfigurationConverter.CONFIG_NAME_VIDEO_LENGTH, MOCK_LENGTH.toString()))
            .build()

      private fun defaultVersionCategory() = ImmutableCategory.builder()
            .name(ConfigurationConverter.CATEGORY_NAME_VERSION)
            .addConfigSettings(configSetting(ConfigurationConverter.CONFIG_NAME_ANDROID_DATE, MOCK_TIMESTAMP.toString()))
            .addConfigSettings(configSetting(ConfigurationConverter.CONFIG_NAME_ANDROID_VERSION, MOCK_VERSION))
            .build()

      private fun defaultBannerCategory() = ImmutableCategory.builder()
            .name(ConfigurationConverter.CATEGORY_BANNER)
            .addConfigSettings(configSetting(ConfigurationConverter.CONFIG_TRAVEL_BANNER_ENABLED, "true"))
            .addConfigSettings(configSetting(ConfigurationConverter.CONFIG_TRAVEL_BANNER_URL, MOCK_URL))
            .addConfigSettings(configSetting(ConfigurationConverter.CONFIG_TRAVEL_BANNER_TITLE, MOCK_TEXT))
            .build()

      private fun configSetting(key: String, value: String) = ImmutableConfigSetting.builder()
            .name(key)
            .value(value)
            .build()

   }
}
