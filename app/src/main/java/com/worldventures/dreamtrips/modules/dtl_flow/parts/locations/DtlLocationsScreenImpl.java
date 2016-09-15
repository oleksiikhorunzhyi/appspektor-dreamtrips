package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search.DtlLocationsSearchPath;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import flow.Flow;
import timber.log.Timber;

public class DtlLocationsScreenImpl extends DtlLayout<DtlLocationsScreen, DtlLocationsPresenter, DtlLocationsPath> implements DtlLocationsScreen, ActivityResultDelegate.ActivityResultListener, CellDelegate<DtlExternalLocation> {

   @Inject @ForActivity Provider<Injector> injectorProvider;
   @Inject ActivityResultDelegate activityResultDelegate;
   //
   @InjectView(R.id.locationsList) RecyclerView recyclerView;
   @InjectView(R.id.progress) View progressView;
   @InjectView(R.id.emptyMerchantsCaption) View emptyMerchantsCaption;
   @InjectView(R.id.autoDetectNearMe) Button autoDetectNearMe;
   @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
   //
   BaseDelegateAdapter adapter;

   @Override
   protected void onPostAttachToWindowView() {
      initToolbar();
      //
      if (getPath().isShowNoMerchantsCaption()) emptyMerchantsCaption.setVisibility(View.VISIBLE);
      //
      recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));
      //
      adapter = new BaseDelegateAdapter<DtlExternalLocation>(getActivity(), injectorProvider.get());
      adapter.registerCell(DtlExternalLocation.class, DtlLocationCell.class);
      adapter.registerDelegate(DtlExternalLocation.class, this);
      //
      recyclerView.setAdapter(adapter);
      //
      bindNearMeButton();
      //
      activityResultDelegate.addListener(this);
   }

   private void initToolbar() {
      toolbar.setTitle(R.string.dtl_locations_title);
      toolbar.inflateMenu(R.menu.menu_locations);
      if (getPath().isAllowUserGoBack()) {
         toolbar.setNavigationIcon(R.drawable.back_icon);
         toolbar.setNavigationOnClickListener(v -> Flow.get(this).goBack());
      } else if (!isTabletLandscape()) {
         toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
         toolbar.setNavigationOnClickListener(view -> ((FlowActivity) getActivity()).openLeftDrawer());
      }
      toolbar.setOnMenuItemClickListener(item -> {
         if (item.getItemId() == R.id.action_search) {
            Flow.get(getContext()).set(new DtlLocationsSearchPath());
            return true;
         }
         return false;
      });
   }

   @Override
   public void locationResolutionRequired(Status status) {
      try {
         status.startResolutionForResult(getActivity(), DtlActivity.GPS_LOCATION_RESOLUTION_REQUEST);
      } catch (IntentSender.SendIntentException th) {
         Timber.e(th, "Error opening settings activity.");
      }
   }

   private void bindNearMeButton() {
      RxView.clicks(autoDetectNearMe)
            .compose(RxLifecycle.bindView(this))
            .throttleFirst(3L, TimeUnit.SECONDS)
            .subscribe(aVoid -> getPresenter().loadNearMeRequested());
   }

   @Override
   public void onCellClicked(DtlExternalLocation location) {
      hideSoftInput();
      getPresenter().onLocationSelected(location);
   }

   @Override
   public void setItems(List<DtlExternalLocation> dtlExternalLocations) {
      hideProgress();
      //
      adapter.clear();
      adapter.addItems(dtlExternalLocations);
   }

   @Override
   public void hideNearMeButton() {
      autoDetectNearMe.setVisibility(View.GONE);
   }

   @Override
   public void showProgress() {
      progressView.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideProgress() {
      progressView.setVisibility(View.GONE);
   }

   @Override
   public boolean onApiError(ErrorResponse errorResponse) {
      return false;
   }

   @Override
   public void onApiCallFailed() {
      progressView.setVisibility(View.GONE);
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
   protected void onDetachedFromWindow() {
      activityResultDelegate.removeListener(this);
      super.onDetachedFromWindow();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Boilerplate stuff
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public DtlLocationsPresenter createPresenter() {
      return new DtlLocationsPresenterImpl(getContext(), injector);
   }

   public DtlLocationsScreenImpl(Context context) {
      super(context);
   }

   public DtlLocationsScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }
}
