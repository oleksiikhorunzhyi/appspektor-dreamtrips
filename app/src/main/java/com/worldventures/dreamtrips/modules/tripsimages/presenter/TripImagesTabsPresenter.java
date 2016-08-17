package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.event.TripImageAnalyticEvent;
import com.worldventures.dreamtrips.modules.tripsimages.events.MyImagesSelectionEvent;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

public class TripImagesTabsPresenter extends Presenter<TripImagesTabsPresenter.View> {


   public static final String SELECTION_EXTRA = "selection_extra";

   private int selection;

   public TripImagesTabsPresenter(Bundle args) {
      if (args != null) {
         selection = args.getInt(SELECTION_EXTRA);
      }
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      view.setSelection(selection);
   }

   @Override
   public void dropView() {
      eventBus.removeAllStickyEvents();
      super.dropView();
   }

   public void trackState(int position) {
      if (position == TripImagesType.ACCOUNT_IMAGES.ordinal()) {
         TrackingHelper.mine(getAccountUserId());
      } else if (position == TripImagesType.YOU_SHOULD_BE_HERE.ordinal()) {
         TrackingHelper.ysbh(getAccountUserId());
      } else if (position == TripImagesType.MEMBERS_IMAGES.ordinal()) {
         TrackingHelper.all(getAccountUserId());
      } else if (position == TripImagesType.VIDEO_360.ordinal()) {
         TrackingHelper.video360(getAccountUserId());
      } else if (position == TripImagesType.INSPIRE_ME.ordinal()) {
         TrackingHelper.inspr(getAccountUserId());
      }
   }

   public void onEventMainThread(MyImagesSelectionEvent event) {
      view.setSelection(1);
   }

   public void onEvent(TripImageAnalyticEvent event) {
      TrackingHelper.actionTripImage(event.getActionAttribute(), event.getTripImageId());
   }

   public interface View extends Presenter.View {

      void setSelection(int selection);
   }
}
