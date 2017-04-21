package com.worldventures.dreamtrips.social.common.webview

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.techery.spares.session.SessionHolder
import com.techery.spares.storage.complex_objects.Optional
import com.worldventures.dreamtrips.core.session.UserSession
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider
import com.worldventures.dreamtrips.modules.common.model.User
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals

class StaticPageProviderSpec: Spek({

   describe("StaticPageProvider") {
      setup()

      it("should provide correct enroll member url") {
         val expectedUrl = "$API_URL/gateway/enroll_member?username=$USERNAME&deviceId=$DEVICE_ID"
         assertEquals(expectedUrl, provider.enrollMemberUrl)
      }

      it ("should provide correct enroll rep url") {
         val expectedUrl = API_URL + "/gateway/enroll_rep?username=" + USERNAME
         assertEquals(expectedUrl, provider.enrollRepUrl)
      }

      it ("should provide correct enroll upgrade url") {
         val expectedUrl = API_URL + "/gateway/enroll_upgrade"
         assertEquals(expectedUrl, provider.enrollUpgradeUrl)
      }

      it ("should provide correct trip booking url") {
         val expectedUrl = API_URL + "/gateway/booking_page/" + TRIP_ID
         assertEquals(expectedUrl, provider.getBookingPageUrl(TRIP_ID))
      }

      it ("should provide correct ota page url") {
         val expectedUrl = API_URL + "/gateway/ota_page"
         assertEquals(expectedUrl, provider.otaPageUrl)
      }

      it ("should provide correct uploadery url") {
         assertEquals(UPLOADERY_URL, provider.uploaderyUrl)
      }

      it ("should provide correct faq url") {
         val expectedUrl = API_URL + "/gateway/faq";
         assertEquals(expectedUrl, provider.faqUrl)
      }

      it ("should provide correct terms of service url") {
         val expectedUrl = API_URL + "/gateway/terms_of_use";
         assertEquals(expectedUrl, provider.termsOfServiceUrl)
      }

      it ("should provide correct cookies policy url") {
         val expectedUrl = API_URL + "/gateway/cookies_policy";
         assertEquals(expectedUrl, provider.cookiesPolicyUrl)
      }

      it ("should provide correct privacy policy url") {
         val expectedUrl = API_URL + "/gateway/privacy_policy";
         assertEquals(expectedUrl, provider.privacyPolicyUrl)
      }

      it ("should provide correct enroll merchant url without bundle") {
         val bundle = null
         val actualUrl = provider.getEnrollMerchantUrl(bundle)
         val params = parseParamsFromUrl(actualUrl)
         checkEnrollMerchantParamsWithoutMerchantId(params)
      }

      it ("should provide correct enroll merchant url with merchant bundle") {
         val bundle = MerchantIdBundle(MERCHANT_ID)
         val actualUrl = provider.getEnrollMerchantUrl(bundle)
         val params = parseParamsFromUrl(actualUrl)
         checkEnrollMerchantParams(params)
      }
   }

}) {
   companion object {
      val API_URL = "http://some-api.io/";
      val UPLOADERY_URL = "http://some-uploadery-api.io";

      val USERNAME = "515661"
      val TRIP_ID = "12410101"
      val DEVICE_ID = "fsadfas"
      val LEGACY_API_TOKEN = "asdb11a"
      val LOCALE = "en-us"
      val MERCHANT_ID = "10191"

      val mockSessionHolder: SessionHolder<UserSession> = mock()
      val deviceInfoProvider: DeviceInfoProvider = mock()
      val provider = StaticPageProvider(mockSessionHolder, deviceInfoProvider, API_URL, UPLOADERY_URL)
      val userSession: UserSession = mock()

      fun setup() {
         val mockUser = mock<User>()

         whenever(mockUser.username).thenReturn(USERNAME)
         whenever(deviceInfoProvider.uniqueIdentifier).thenReturn(DEVICE_ID)
         whenever(userSession.user).thenReturn(mockUser)
         whenever(userSession.legacyApiToken).thenReturn(LEGACY_API_TOKEN)
         whenever(userSession.locale).thenReturn(LOCALE)
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