package com.worldventures.dreamtrips.modules.trips.presenter

import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.trips.model.TripModel

class TripMapListPresenter(private val trips: List<TripModel>) : Presenter<TripMapListPresenter.View>() {

   override fun takeView(view: View) {
      super.takeView(view)
      view.updateItems(trips)
   }

   interface View : Presenter.View {
      fun updateItems(trips: List<TripModel>)
   }
}
