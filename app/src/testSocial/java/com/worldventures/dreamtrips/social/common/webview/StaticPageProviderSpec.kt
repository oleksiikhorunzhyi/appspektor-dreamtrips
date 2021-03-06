package com.worldventures.dreamtrips.social.common.webview

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.modules.infopages.ImmutableStaticPageProviderConfig
import com.worldventures.core.modules.infopages.StaticPageProvider
import com.worldventures.core.modules.infopages.StaticPageProviderConfig
import com.worldventures.core.service.DeviceInfoProvider
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.dreamtrips.BaseSpec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.net.URLEncoder
import java.util.HashMap
import kotlin.test.assertEquals

class StaticPageProviderSpec: BaseSpec({

   describe("StaticPageProvider") {

      it("should provide correct enroll member url") {
         val expectedUrl = "$API_URL/gateway/enroll_member?username=$USERNAME&deviceId=$DEVICE_ID"
         assertEquals(expectedUrl, provider.enrollMemberUrl)
      }

      it ("should provide correct enroll rep url") {
         val expectedUrl = "$API_URL/gateway/enroll_rep?username=$USERNAME&deviceId=$DEVICE_ID"
         assertEquals(expectedUrl, provider.enrollRepUrl)
      }

      it ("should provide correct enroll upgrade url") {
         val expectedUrl = "$API_URL/gateway/enroll_upgrade"
         assertEquals(expectedUrl, provider.enrollUpgradeUrl)
      }

      it ("should provide correct trip booking url") {
         val expectedUrl = "$API_URL/gateway/booking_page/$TRIP_ID"
         assertEquals(expectedUrl, provider.getBookingPageUrl(TRIP_ID))
      }

      it ("should provide correct ota page url") {
         val expectedUrl = "$API_URL/gateway/ota_page"
         assertEquals(expectedUrl, provider.otaPageUrl)
      }

      it ("should provide correct uploadery url") {
         assertEquals(UPLOADERY_URL, provider.uploaderyUrl)
      }

      it ("should provide correct faq url") {
         val expectedUrl = "$API_URL/gateway/faq";
         assertEquals(expectedUrl, provider.faqUrl)
      }
      
      it ("should provide correct cookies policy url") {
         val expectedUrl = "$API_URL/gateway/cookies_policy"
         assertEquals(expectedUrl, provider.cookiesPolicyUrl)
      }

      it ("should provide correct privacy policy url") {
         val expectedUrl = "$API_URL/gateway/privacy_policy"
         assertEquals(expectedUrl, provider.privacyPolicyUrl)
      }
//       TODO : drop to merchant enroll test
//      it ("should provide correct enroll merchant url without bundle") {
//         val bundle = null
//         val actualUrl = provider.getEnrollMerchantUrl(bundle)
//         val params = parseParamsFromUrl(actualUrl)
//         checkEnrollMerchantParamsWithoutMerchantId(params)
//      }
//
//      it ("should provide correct enroll merchant url with merchant bundle") {
//         val bundle = MerchantIdBundle(MERCHANT_ID)
//         val actualUrl = provider.getEnrollMerchantUrl(bundle)
//         val params = parseParamsFromUrl(actualUrl)
//         checkEnrollMerchantParams(params)
//      }

      it ("should provide correct backoffice url") {
         val encodedPart = URLEncoder.encode("/Marketing/WorldVenturesAdvantage", "UTF-8")
         val expectedUrl = "$BACKOFFICE_URL/Account/Dispatch?url=$encodedPart"
         assertEquals(expectedUrl, provider.wvAdvantageUrl)
      }

      it("should provide correct forgot password url") {
         val expectedUrl = "$FORGOT_PASSWORD_URL?dreamtrips"
         assertEquals(expectedUrl, provider.forgotPasswordUrl)
      }

      it("should provide correct forgot member id url") {
         val expectedUrl = "$FORGOT_PASSWORD_URL/forgotLoginId?dreamtrips"
         assertEquals(expectedUrl, provider.forgotMemberIdUrl)
      }
   }

}) {
   companion object {
      val API_URL = "http://some-api.io/"
      val BACKOFFICE_URL = "http://backoffice.io/"
      val FORGOT_PASSWORD_URL = "http://forgot-password.io/"
      val UPLOADERY_URL = "http://some-uploadery-api.io"

      val USERNAME = "515661"
      val TRIP_ID = "12410101"
      val DEVICE_ID = "Pixel"
      val LEGACY_API_TOKEN = "272c463069"
      val LOCALE = "en-us"
      val MERCHANT_ID = "10191"

      val mockSessionHolder: SessionHolder = mock()
      val deviceInfoProvider: DeviceInfoProvider = mock()
      val staticPageProviderConfig: StaticPageProviderConfig =
            ImmutableStaticPageProviderConfig.builder()
                  .appSessionHolder(mockSessionHolder)
                  .deviceInfoProvider(deviceInfoProvider)
                  .apiUrl(API_URL)
                  .backofficeUrl(BACKOFFICE_URL)
                  .uploaderyUrl(UPLOADERY_URL)
                  .forgotPasswordUrl(FORGOT_PASSWORD_URL)
                  .build()
      val provider = StaticPageProvider(staticPageProviderConfig)
      val userSession: UserSession = mock()

      init {
         val mockUser = mock<User>()

         whenever(mockUser.username).thenReturn(USERNAME)
         whenever(deviceInfoProvider.uniqueIdentifier).thenReturn(DEVICE_ID)
         whenever(userSession.user()).thenReturn(mockUser)
         whenever(userSession.legacyApiToken()).thenReturn(LEGACY_API_TOKEN)
         whenever(userSession.locale()).thenReturn(LOCALE)
         whenever(mockSessionHolder.get()).thenReturn(Optional.of(userSession))
      }

      fun parseParamsFromUrl(actualUrl: String): HashMap<String, String> {
         val paramsString = actualUrl.substring(actualUrl.lastIndexOf("?") + 1, actualUrl.length)
         val paramsList: List<String> = paramsString.split("&");
         val paramsMap = HashMap<String, String>()
         for (unsplitParams in paramsList) {
            val params = unsplitParams.split("=")
            paramsMap.put(params[0], params[1])
         }
         return paramsMap
      }

      fun checkEnrollMerchantParams(params: HashMap<String, String>) {
         checkEnrollMerchantParamsWithoutMerchantId(params)
         assertEquals("suggestProspect", params.get("intent"))
         assertEquals(StaticPageProviderSpec.MERCHANT_ID, params.get("prospectId"))
      }

      fun checkEnrollMerchantParamsWithoutMerchantId(params: HashMap<String, String>) {
         assertEquals(StaticPageProviderSpec.USERNAME, params.get("username"))
         assertEquals(StaticPageProviderSpec.LEGACY_API_TOKEN, params.get("sso"))
         assertEquals(StaticPageProviderSpec.LOCALE, params.get("locale"))
      }
   }
}
