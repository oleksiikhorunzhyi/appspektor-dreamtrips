package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

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

   public interface View extends Presenter.View {

      void setSelection(int selection);
   }
}
