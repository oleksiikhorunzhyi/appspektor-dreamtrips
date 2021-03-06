package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

import rx.Observable;

public interface MasterToolbarScreen extends DtlScreen {

   void updateToolbarLocationTitle(@Nullable DtlLocation dtlLocation);

   void updateToolbarSearchCaption(@Nullable String searchCaption);

   void connectToggleUpdate();

   void toggleOffersOnly(boolean enabled);

   void setFilterButtonState(boolean enabled);

   void locationResolutionRequired(Status status);

   void setItems(List<DtlLocation> locations, boolean addLocationHeader);

   void showProgress();

   void hideProgress();

   void toggleSearchPopupVisibility(boolean show);

   boolean isSearchPopupShowing();

   Observable<String> provideLocationSearchObservable();

   void showNoTransactionMessage();

   void goToTransactionPage();
}
