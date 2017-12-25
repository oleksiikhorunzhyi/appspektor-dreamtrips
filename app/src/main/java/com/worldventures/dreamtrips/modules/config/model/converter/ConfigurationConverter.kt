package com.worldventures.dreamtrips.modules.config.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.config.model.ConfigSetting
import com.worldventures.dreamtrips.modules.config.model.Configuration
import com.worldventures.dreamtrips.modules.config.model.TravelBannerRequirement
import com.worldventures.dreamtrips.modules.config.model.UpdateRequirement
import com.worldventures.dreamtrips.modules.config.model.VideoRequirement
import io.techery.mappery.MapperyContext
import com.worldventures.dreamtrips.api.config.model.Configuration as ApiConfiguration

class ConfigurationConverter : Converter<com.worldventures.dreamtrips.api.config.model.Configuration, Configuration> {

   override fun convert(context: MapperyContext, source: ApiConfiguration): Configuration {
      if (SCOPE_NAME != source.scope()) {
         throw IllegalStateException("Scope is not correct")
      }
      source.let {
         return Configuration(getUpdateRequirement(it), getVideoRequirement(it), getTravelBannerRequirement(it))
      }
   }

   private fun getUpdateRequirement(configurationDTO: ApiConfiguration): UpdateRequirement? {
      val category = getCategoryByName(configurationDTO, CATEGORY_NAME_VERSION)
      category?.let {
         val configSettings = it.configSettings()
         val androidVersion = getConfigSettingByName(configSettings, CONFIG_NAME_ANDROID_VERSION)
         val dateConfig = getConfigSettingByName(configSettings, CONFIG_NAME_ANDROID_DATE)
         if (dateConfig != null && androidVersion != null) return UpdateRequirement(androidVersion.value(),
               java.lang.Long.parseLong(dateConfig.value()) * MILLISECONDS_IN_SECOND) // timestamp defined in seconds on server side
      }
      return null
   }

   private fun getVideoRequirement(configurationDTO: ApiConfiguration): VideoRequirement {
      val category = getCategoryByName(configurationDTO, CATEGORY_NAME_VIDEO)
      category?.let {
         val videoLength = getConfigSettingByName(it.configSettings(), CONFIG_NAME_VIDEO_LENGTH)
         return if (videoLength != null) VideoRequirement(Integer.parseInt(videoLength.value())) else VideoRequirement()

      }
      return VideoRequirement()
   }

   private fun getTravelBannerRequirement(configurationDTO: ApiConfiguration): TravelBannerRequirement? {
      val category = getCategoryByName(configurationDTO, CATEGORY_BANNER)
      category?.let {
         val title = getConfigSettingByName(it.configSettings(), CONFIG_TRAVEL_BANNER_TITLE)
         val url = getConfigSettingByName(it.configSettings(), CONFIG_TRAVEL_BANNER_URL)
         val enabled = getConfigSettingByName(it.configSettings(), CONFIG_TRAVEL_BANNER_ENABLED)
         if (title != null && url != null && enabled != null) return TravelBannerRequirement(title.value(), url.value(),
               java.lang.Boolean.parseBoolean(enabled.value()))
      }
      return null
   }

   private fun getCategoryByName(apiConfig: ApiConfiguration, name: String) = apiConfig.categories().firstOrNull { name == it.name() }

   private fun getConfigSettingByName(configSettings: List<ConfigSetting>, name: String) = configSettings.firstOrNull { name == it.name() }

   override fun sourceClass() = ApiConfiguration::class.java

   override fun targetClass() = Configuration::class.java

   companion object {
      private val SCOPE_NAME = "APPLICATION"
      private val CATEGORY_NAME_VERSION = "version"
      private val CATEGORY_NAME_VIDEO = "video"
      private val CATEGORY_BANNER = "travel_banner"

      private val CONFIG_NAME_ANDROID_VERSION = "android_version"
      private val CONFIG_NAME_ANDROID_DATE = "android_date"
      private val CONFIG_NAME_VIDEO_LENGTH = "android_max_selectable_video_duration"
      private val CONFIG_TRAVEL_BANNER_TITLE = "travel_banner_title"
      private val CONFIG_TRAVEL_BANNER_URL = "travel_banner_url"
      private val CONFIG_TRAVEL_BANNER_ENABLED = "travel_banner_enabled"
      private val MILLISECONDS_IN_SECOND = 1000
   }
}
