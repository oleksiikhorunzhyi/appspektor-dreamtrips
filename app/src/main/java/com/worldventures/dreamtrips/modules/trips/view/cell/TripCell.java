package com.worldventures.dreamtrips.modules.trips.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.util.TripFeedViewInjector;

import javax.inject.Inject;

@Layout(R.layout.adapter_item_trip)
public class TripCell extends AbstractCell<TripModel> {

   @Inject SessionHolder<UserSession> appSessionHolder;
   @Inject Presenter.TabletAnalytic tabletAnalytic;

   private TripFeedViewInjector tripFeedViewInjector;

   public TripCell(View view) {
      super(view);
   }

   @Override
   public void afterInject() {
      super.afterInject();
      tripFeedViewInjector = new TripFeedViewInjector(itemView, router, getEventBus());
      tripFeedViewInjector.setSyncStateListener(this::syncUIStateWithModel);
   }

   @Override
   protected void syncUIStateWithModel() {
      Optional<UserSession> userSessionOptional = appSessionHolder.get();
      if (userSessionOptional.isPresent()) tripFeedViewInjector.initTripData(getModelObject(), userSessionOptional.get()
            .getUser());
   }
}
