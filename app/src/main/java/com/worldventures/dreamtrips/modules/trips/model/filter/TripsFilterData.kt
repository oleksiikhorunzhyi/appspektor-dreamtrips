package com.worldventures.dreamtrips.modules.trips.model.filter

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import org.joda.time.DateTime
import org.joda.time.DurationFieldType
import java.io.Serializable
import java.util.ArrayList
import java.util.Date

@DefaultSerializer(CompatibleFieldSerializer::class)
class TripsFilterData : Serializable {

   var minNights: Int = 0
   var maxNights: Int = 0
   var minPrice: Double = 0.toDouble()
   var maxPrice: Double = 0.toDouble()
   var isShowSoldOut: Boolean = false
   var isShowFavorites: Boolean = false
   var isShowRecentlyAdded: Boolean = false
   lateinit var startDate: Date
   lateinit var endDate: Date
   var allRegions: List<RegionModel> = ArrayList()
   var allParentActivities: List<ActivityModel> = ArrayList()

   val minNightsForRequest: Int?
      get() = if (minNights <= MIN_NIGHTS) null else minNights

   val maxNightsForRequest: Int?
      get() = if (maxNights >= MAX_NIGHTS) null else maxNights

   val minPriceForRequest: Double?
      get() = if (minPrice <= MIN_PRICE) null else minPrice

   val maxPriceForRequest: Double?
      get() = if (maxPrice >= MAX_PRICE) null else maxPrice

   val startDateForRequest: Date
      get() = DateTime(startDate.time).withMillisOfDay(0).toDate()

   val endDateForRequest: Date
      get() = DateTime(endDate.time).withMillisOfDay(0).toDate()

   val acceptedRegions: List<Int>?
      get() = if (allRegions.firstOrNull { !it.isChecked } == null) {
         null
      } else allRegions.filter { it.isChecked }.map { it.id }

   val acceptedActivities: List<Int>?
      get() = if (allParentActivities.firstOrNull { !it.isChecked } == null) null
      else allParentActivities.filter { it.isChecked }.map { it.id }

   init {
      reset()
   }

   fun reset() {
      maxPrice = MAX_PRICE.toDouble()
      minPrice = MIN_PRICE.toDouble()
      maxNights = MAX_NIGHTS
      minNights = MIN_NIGHTS
      startDate = DateTime.now().toDate()
      endDate = DateTime.now().withFieldAdded(DurationFieldType.years(), 1).toDate()
      isShowFavorites = false
      isShowRecentlyAdded = false
      isShowSoldOut = false
   }

   companion object {
      val MIN_NIGHTS = 0
      val MAX_NIGHTS = 9
      val MIN_PRICE = 100
      val MAX_PRICE = 500
   }
}
