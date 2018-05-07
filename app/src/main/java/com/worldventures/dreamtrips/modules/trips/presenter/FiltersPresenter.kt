package com.worldventures.dreamtrips.modules.trips.presenter

import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.trips.model.analytics.TripsFilterDataAnalyticsWrapper
import com.worldventures.dreamtrips.modules.trips.model.filter.DateFilterItem
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterFavoriteModel
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterModel
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterRecentlyAddedModel
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterSoldOutModel
import com.worldventures.dreamtrips.modules.trips.model.filter.RegionHeaderModel
import com.worldventures.dreamtrips.modules.trips.model.filter.ThemeHeaderModel
import com.worldventures.dreamtrips.modules.trips.model.filter.TripsFilterData
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor
import com.worldventures.dreamtrips.modules.trips.service.analytics.AcceptDreamTripsFiltersAnalyticAction
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsFilterDataCommand
import com.worldventures.dreamtrips.modules.trips.service.command.TripFiltersAppliedCommand
import icepick.State
import io.techery.janet.helper.ActionStateSubscriber
import java.util.Date
import javax.inject.Inject

class FiltersPresenter : Presenter<FiltersPresenter.View>() {

   @Inject lateinit var tripsInteractor: TripsInteractor

   @JvmField @State internal var tripFilterData: TripsFilterData? = null
   @JvmField @State internal var regionsExpanded = false
   @JvmField @State internal var regionsSelected = true
   @JvmField @State internal var themesExpanded = false
   @JvmField @State internal var themesSelected = true

   override fun takeView(view: View) {
      super.takeView(view)
      if (tripFilterData == null) tripFilterData = TripsFilterData()
      subscribeToFiltersLoading()
      loadFilters()
   }

   private fun subscribeToFiltersLoading() {
      tripsInteractor.tripFiltersPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetTripsFilterDataCommand>()
                  .onStart { view.showProgress() }
                  .onSuccess {
                     tripFilterData?.let { data ->
                        it.result?.let { cachedFilters ->
                           data.allParentActivities = cachedFilters.activities.filter { it.isParent }
                           data.allRegions = cachedFilters.regions
                        }
                     }
                     fillData()
                  }
                  .onFinish { view.hideProgress() }
                  .onFail { _, _ -> view.showErrorContainer() })
   }

   fun loadFilters() = tripsInteractor.tripFiltersPipe.send(GetTripsFilterDataCommand())

   private fun fillData() {
      tripFilterData?.let {
         view.fillData(mutableListOf<Any>().apply {
            add(DateFilterItem(it.startDate, it.endDate))
            add(FilterModel(it))
            add(FilterSoldOutModel(it.isShowSoldOut))
            add(FilterFavoriteModel(it.isShowFavorites))
            add(FilterRecentlyAddedModel(it.isShowRecentlyAdded))
            add(RegionHeaderModel(regionsSelected))
            if (regionsExpanded) addAll(it.allRegions)
            add(ThemeHeaderModel(themesSelected))
            if (themesExpanded) addAll(it.allParentActivities)
         })
      }
   }

   private fun setRegionsChecked(isChecked: Boolean) {
      tripFilterData?.let { for (region in it.allRegions) region.isChecked = isChecked }
   }

   private fun setThemesChecked(isChecked: Boolean) {
      tripFilterData?.let { for (activity in it.allParentActivities) activity.isChecked = isChecked }
   }

   fun acceptFilters() {
      tripFilterData?.let { tripsInteractor.tripFiltersAppliedPipe.send(TripFiltersAppliedCommand(it)) }
      trackApplyFilter()
   }

   fun resetFilters() {
      themesSelected = true
      regionsSelected = true
      setRegionsChecked(true)
      setThemesChecked(true)
      tripFilterData?.reset()
      acceptFilters()
      fillData()
   }

   private fun trackApplyFilter() {
      tripFilterData?.let {
         analyticsInteractor.analyticsActionPipe().send(AcceptDreamTripsFiltersAnalyticAction(TripsFilterDataAnalyticsWrapper(it)))
      }
   }

   fun onRangeBarDurationEvent(minNights: Int, maxNights: Int) {
      tripFilterData?.let {
         it.minNights = minNights
         it.maxNights = maxNights
      }
   }

   fun onRangeBarPriceEvent(minPrice: Double, maxPrice: Double) {
      tripFilterData?.let {
         it.minPrice = minPrice
         it.maxPrice = maxPrice
      }
   }

   fun toggleThemeVisibility() {
      themesExpanded = !themesExpanded
      fillData()
   }

   fun toggleRegionVisibility() {
      regionsExpanded = !regionsExpanded
      fillData()
   }

   fun onFilterShowSoldOutEvent(enabled: Boolean) {
      tripFilterData?.isShowSoldOut = enabled
   }

   fun onFilterShowFavoritesEvent(enabled: Boolean) {
      tripFilterData?.isShowFavorites = enabled
   }

   fun onFilterShowRecentlyAddedEvent(enabled: Boolean) {
      tripFilterData?.isShowRecentlyAdded = enabled
   }

   fun onCheckBoxAllRegionsPressedEvent(isChecked: Boolean) {
      regionsSelected = isChecked
      setRegionsChecked(isChecked)
      fillData()
   }

   fun onCheckBoxAllThemePressedEvent(isChecked: Boolean) {
      themesSelected = isChecked
      setThemesChecked(isChecked)
      fillData()
   }

   fun onThemeSetChangedEvent() {
      themesSelected = true
      tripFilterData?.let {
         for (activity in it.allParentActivities) {
            if (!activity.isChecked) {
               themesSelected = false
               break
            }
         }
      }
      fillData()
   }

   fun onRegionSetChangedEvent() {
      regionsSelected = true
      tripFilterData?.let {
         for ((_, _, isChecked) in it.allRegions) {
            if (!isChecked) {
               regionsSelected = false
               break
            }
         }
      }
      fillData()
   }

   fun onStartDateChanged(date: Date) {
      tripFilterData?.startDate = date
   }

   fun onEndDateChanged(date: Date) {
      tripFilterData?.endDate = date
   }

   interface View : Presenter.View {
      fun showProgress()

      fun hideProgress()

      fun showErrorContainer()

      fun hideErrorContainer()

      fun fillData(data: List<*>)

      fun dataSetChanged()
   }
}
