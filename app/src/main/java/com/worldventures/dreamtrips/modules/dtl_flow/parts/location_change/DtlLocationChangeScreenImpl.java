package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.jakewharton.rxbinding.view.RxView;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationChangeCell;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.ExpandableDtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import rx.Observable;
import timber.log.Timber;

public class DtlLocationChangeScreenImpl extends DtlLayout<DtlLocationChangeScreen, DtlLocationChangePresenter, DtlLocationChangePath> implements DtlLocationChangeScreen, ActivityResultDelegate.ActivityResultListener, CellDelegate<DtlExternalLocation> {

   @Inject @ForActivity Provider<Injector> injectorProvider;
   @Inject ActivityResultDelegate activityResultDelegate;
   //
   @InjectView(R.id.expandableDtlToolbar) ExpandableDtlToolbar dtlToolbar;
   @InjectView(R.id.autoDetectNearMe) Button autoDetectNearMe;
   @InjectView(R.id.emptyMerchantsCaption) View emptyMerchantsCaption;
   @InjectView(R.id.emptyMerchantsOrCaption) View emptyMerchantsOrCaption;
   @InjectView(R.id.selectFromNearbyCitiesCaption) View selectFromNearbyCitiesCaption;
   @InjectView(R.id.locationsList) RecyclerView recyclerView;
   @InjectView(R.id.progress) View progressView;
   //
   BaseDelegateAdapter adapter;

   @Override
   protected void onPostAttachToWindowView() {
      super.onPostAttachToWindowView();
      //
      activityResultDelegate.addListener(this);
      //
      setupRecyclerView();
      bindNearMeButton();
   }

   @Override
   protected void onDetachedFromWindow() {
      activityResultDelegate.removeListener(this);
      super.onDetachedFromWindow();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Private screen methods
   ///////////////////////////////////////////////////////////////////////////

   private void setupRecyclerView() {
      recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.dtl_location_change_list_divider), true));
      //
      adapter = new BaseDelegateAdapter<DtlExternalLocation>(getActivity(), injectorProvider.get());
      adapter.registerCell(DtlExternalLocation.class, DtlLocationChangeCell.class);
      adapter.registerDelegate(DtlExternalLocation.class, this);
      //
      recyclerView.setAdapter(adapter);
   }

   private void bindNearMeButton() {
      RxView.clicks(autoDetectNearMe)
            .compose(RxLifecycle.bindView(this))
            .throttleFirst(3L, TimeUnit.SECONDS)
            .subscribe(aVoid -> getPresenter().loadNearMeRequested());
   }

   ///////////////////////////////////////////////////////////////////////////
   // Overridden and screen implementation methods
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public Observable<Void> provideMapClickObservable() {
      return RxDtlToolbar.navigationClicks(dtlToolbar).throttleFirst(250L, TimeUnit.MILLISECONDS);
   }

   @Override
   public Observable<Boolean> provideMerchantInputFocusLossObservable() {
      return RxDtlToolbar.merchantSearchInputFocusChanges(dtlToolbar)
            .skip(1)
            .filter(Boolean::booleanValue); // only true -> only focus gains;
   }

   @Override
   public Observable<Void> provideDtlToolbarCollapsesObservable() {
      return RxDtlToolbar.collapses(dtlToolbar);
   }

   @Override
   public Observable<String> provideLocationSearchObservable() {
      return RxDtlToolbar.locationSearchTextChanges(dtlToolbar).skip(1);
   }

   @Override
   public void updateToolbarTitle(@Nullable DtlLocation dtlLocation, @Nullable String appliedSearchQuery) {
      if (dtlLocation == null) return;
      switch (dtlLocation.getLocationSourceType()) {
         case NEAR_ME:
         case EXTERNAL:
            dtlToolbar.setCaptions(appliedSearchQuery, dtlLocation.getLongName());
            break;
         case FROM_MAP:
            String locationTitle = TextUtils.isEmpty(dtlLocation.getLongName()) ? getResources().getString(R.string.dtl_nearby_caption_empty) : getResources()
                  .getString(R.string.dtl_nearby_caption_format, dtlLocation.getLongName());
            dtlToolbar.setCaptions(appliedSearchQuery, locationTitle);
            break;
      }
   }

   @Override
   public void hideNearMeButton() {
      autoDetectNearMe.setVisibility(GONE);
   }

   @Override
   public void showProgress() {
      progressView.setVisibility(VISIBLE);
      selectFromNearbyCitiesCaption.setVisibility(GONE);
      switchVisibilityNoMerchants(false);
      switchVisibilityOrCaption(false);
   }

   @Override
   public void hideProgress() {
      progressView.setVisibility(GONE);
   }

   @Override
   public void switchVisibilityNoMerchants(boolean visible) {
      emptyMerchantsCaption.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   public void switchVisibilityOrCaption(boolean visible) {
      emptyMerchantsOrCaption.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   public void onCellClicked(DtlExternalLocation location) {
      hideSoftInput();
      getPresenter().locationSelected(location);
   }

   @Override
   public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == DtlActivity.GPS_LOCATION_RESOLUTION_REQUEST) {
         switch (resultCode) {
            case Activity.RESULT_OK:
               // All required changes were successfully made
               getPresenter().onLocationResolutionGranted();
               break;
            case Activity.RESULT_CANCELED:
               // The user was asked to change settings, but chose not to
               getPresenter().onLocationResolutionDenied();
               break;
         }
         return true;
      }
      return false;
   }

   @Override
   public void onApiCallFailed() {
      hideProgress();
   }

   @Override
   public void locationResolutionRequired(Status status) {
      try {
         status.startResolutionForResult(getActivity(), DtlActivity.GPS_LOCATION_RESOLUTION_REQUEST);
      } catch (IntentSender.SendIntentException th) {
         Timber.e(th, "Error opening settings activity.");
      }
   }

   @Override
   public void setItems(List<DtlExternalLocation> locations, boolean showLocationHeader) {
      hideProgress();
      //
      selectFromNearbyCitiesCaption.setVisibility(showLocationHeader ? VISIBLE : GONE);
      adapter.clearAndUpdateItems(locations);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Boilerplate stuff
   ///////////////////////////////////////////////////////////////////////////

   public DtlLocationChangeScreenImpl(Context context) {
      super(context);
   }

   public DtlLocationChangeScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public DtlLocationChangePresenter createPresenter() {
      return new DtlLocationChangePresenterImpl(getContext(), injector);
   }
}
