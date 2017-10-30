package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.AttributesInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsFacadeInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationFacadeCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;

import static com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType.NEAR_ME;

public class DtlStartPresenterImpl extends DtlPresenterImpl<DtlStartScreen, ViewState.EMPTY> implements DtlStartPresenter {

   @Inject LocationDelegate gpsLocationDelegate;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject AttributesInteractor attributesInteractor;
   @Inject MerchantsFacadeInteractor interactor;

   public DtlStartPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      bindLocationObtaining();
   }

   private void bindLocationObtaining() {
      gpsLocationDelegate.requestLocationUpdate()
            .compose(bindViewIoToMainComposer())
            .take(1)
            .doOnSubscribe(getView()::showProgress)
            .subscribe(this::proceedNavigation, this::onLocationError);
   }

   public void onLocationResolutionGranted() {
      bindLocationObtaining();
   }

   public void onLocationResolutionDenied() {
      proceedNavigation(null);
   }

   private void proceedNavigation(@Nullable Location newGpsLocation) {
      locationInteractor.locationFacadePipe()
            .observeSuccessWithReplay()
            .take(1)
            .compose(bindViewIoToMainComposer())
            .map(LocationFacadeCommand::getResult)
            .subscribe(dtlLocation -> {
               switch (dtlLocation.locationSourceType()) {
                  case UNDEFINED:
                     if (newGpsLocation == null) {
                        navigatePath(DtlLocationsPath.getDefault());
                     } else {
                        DtlLocation dtlManualLocation = ImmutableDtlLocation.builder()
                              .isExternal(false)
                              .locationSourceType(NEAR_ME)
                              .longName(context.getString(R.string.dtl_near_me_caption))
                              .coordinates(new LatLng(newGpsLocation.getLatitude(), newGpsLocation.getLongitude()))
                              .build();
                        locationInteractor.changeSourceLocation(dtlManualLocation);
                        navigatePath(DtlMerchantsPath.withAllowedRedirection());
                     }
                     break;
                  case NEAR_ME:
                     if (newGpsLocation == null) { // we had location before, but not now - and we need it
                        locationInteractor.clear();
                        navigatePath(DtlLocationsPath.getDefault());
                        return;
                     }
                     if (dtlLocation.isOutOfMinDistance(newGpsLocation)) {
                        locationInteractor.changeSourceLocation(ImmutableDtlLocation.builder()
                              .from(dtlLocation)
                              .isExternal(false)
                              .coordinates(new LatLng(newGpsLocation.getLatitude(), newGpsLocation.getLongitude()))
                              .build());
                     }
                     navigatePath(DtlMerchantsPath.withAllowedRedirection());
                     break;
                  case FROM_MAP:
                  case EXTERNAL:
                     navigatePath(DtlMerchantsPath.getDefault());
                     break;
                  default:
                     break;
               }
            });
   }

   private void navigatePath(Path path) {
      History history = History.single(path);
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   /**
    * Check if given error's cause is insufficient GPS resolution or usual throwable and act accordingly
    *
    * @param e exception that {@link LocationDelegate} subscription returned
    */
   private void onLocationError(Throwable e) {
      locationInteractor.locationFacadePipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(LocationFacadeCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(location -> {
               // Determines whether we can proceed without locating device by GPS.
               if (location.locationSourceType() != NEAR_ME) {
                  proceedNavigation(null);
               } else {
                  if (e instanceof LocationDelegate.LocationException) {
                     getView().locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
                  } else {
                     onLocationResolutionDenied();
                  }
               }
            }, throwable -> navigatePath(DtlLocationsPath.getDefault()));
   }
}
