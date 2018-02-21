package com.worldventures.dreamtrips.modules.mapping.converter

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.common.base.BaseTestBody
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.Random
import kotlin.test.assertTrue
import com.worldventures.core.model.session.Feature as AppFeature
import com.worldventures.dreamtrips.api.session.model.Feature as ApiFeature

class FeatureConverterSpec : BaseBodySpec(object : BaseTestBody {

   private val feature: ApiFeature = mock()

   override fun create(): Spec.() -> Unit = {
      describe("FeatureConverter ") {
         it("Should return right permission value or unknown value if permission doesn't exist") {
            val appFeatures = listOf(
                  AppFeature.TRIPS, AppFeature.REP_TOOLS, AppFeature.SOCIAL, AppFeature.DTL,
                  AppFeature.REP_SUGGEST_MERCHANT, AppFeature.BOOK_TRAVEL, AppFeature.BOOK_TRIP, AppFeature.MEMBERSHIP,
                  AppFeature.WALLET, AppFeature.WALLET_PROVISIONING, AppFeature.BUCKET_LIST, AppFeature.TRIP_IMAGES,
                  AppFeature.INVITATIONS, AppFeature.SEND_FEED_BACK, AppFeature.SETTINGS, AppFeature.DLC
            )

            val apiFeatures = ApiFeature.FeatureName.values().toMutableList()
            val position = Random().nextInt(apiFeatures.size - 1)

            whenever(feature.name()).thenAnswer { apiFeatures[position] }

            assertTrue {
               FeatureConverter().convert(mock(), feature).let {
                  (position <= appFeatures.size - 1 && it.name == appFeatures[position]) || it.name == AppFeature.UNKNOWN
               }
            }
         }
      }
   }
})
