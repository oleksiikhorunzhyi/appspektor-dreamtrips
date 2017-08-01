package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.impl;


import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.LostCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.LostCardScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.adapter.LostCardInfoWindowAdapter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.model.LostCardPin;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletSwitcher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static android.graphics.Bitmap.createBitmap;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LostCardScreenImpl extends WalletBaseController<LostCardScreen, LostCardPresenter> implements LostCardScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.tracking_enable_switcher) WalletSwitcher trackingEnableSwitcher;
   @InjectView(R.id.ll_disable_tracking_info_view) View disabledTrackingView;
   @InjectView(R.id.last_connection_time_container) View lastConnectionTimeContainer;
   @InjectView(R.id.map_container) View mapContainer;
   @InjectView(R.id.tv_empty_lost_card_msg) TextView tvDisableLostCardMsg;
   @InjectView(R.id.empty_location_view) View emptyLocationsView;
   @InjectView(R.id.last_connected_label) TextView tvLastConnectionLabel;
   @InjectView(R.id.map_view) ToucheableMapView mapView;

   @Inject LostCardPresenter presenter;

   private final SimpleDateFormat lastConnectedDateFormat = new SimpleDateFormat("EEEE, MMMM dd, h:mma", Locale.US);

   private Observable<Boolean> enableTrackingObservable;
   private GoogleMap googleMap;

   public LostCardScreenImpl() {
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      enableTrackingObservable = RxCompoundButton.checkedChanges(trackingEnableSwitcher).skip(1);
      mapView.onCreate(null);
      initMap();
      tvDisableLostCardMsg.setText(ProjectTextUtils.fromHtml(getString(R.string.wallet_lost_card_empty_view)));
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      mapView.onResume();
   }

   @Override
   protected void onDetach(@NonNull View view) {
      super.onDetach(view);
      mapView.onPause();
   }

   @Override
   protected void onDestroyView(View view) {
      super.onDestroyView(view);
      mapView.onDestroy();
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   @Override
   public Observable<Boolean> observeTrackingEnable() {
      return enableTrackingObservable;
   }

   @Override
   public OperationView<FetchAddressWithPlacesCommand> provideOperationView() {
      //noinspection unchecked cast
      return new ComposableOperationView<>(null, null, ErrorViewFactory.<FetchAddressWithPlacesCommand>builder()
            .addProvider(new HttpErrorViewProvider<>(getContext(), getPresenter().httpErrorHandlingUtil(),
                  getPresenter()::retryFetchAddressWithPlaces,
                  fetchAddressWithPlacesCommand -> {
                  }))
            .build());
   }

   @Override
   public void setVisibleDisabledTrackingView(boolean visible) {
      disabledTrackingView.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   public void setVisibleMsgEmptyLastLocation(boolean visible) {
      emptyLocationsView.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   public void setVisibleLastConnectionTime(boolean visible) {
      lastConnectionTimeContainer.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   public void setVisibilityMap(boolean visible) {
      if (mapContainer.getVisibility() == GONE) {
         ObjectAnimator.ofFloat(mapView, View.ALPHA, 0f, 1f)
               .setDuration(1500)
               .start();
      }
      mapContainer.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   public void setLastConnectionDate(Date date) {
      tvLastConnectionLabel.setText(lastConnectedDateFormat.format(date));
   }

   @Override
   public void setTrackingSwitchStatus(boolean checked) {
      trackingEnableSwitcher.setCheckedWithoutNotify(checked);
   }

   @Override
   public void addPin(@NonNull LostCardPin pinData) {
      final Marker marker = clearMapAndAttachMarker(pinData.position());
      final LostCardInfoWindowAdapter infoWindowAdapter = new LostCardInfoWindowAdapter((ViewGroup) getView(), pinData);
      googleMap.setInfoWindowAdapter(infoWindowAdapter);
      googleMap.setOnInfoWindowClickListener(m -> {
         infoWindowAdapter.openExternalMap();
         getPresenter().trackDirectionsClick();
      });
      marker.showInfoWindow();
   }

   @Override
   public void addPin(LatLng position) {
      clearMapAndAttachMarker(position);
   }

   private Marker clearMapAndAttachMarker(LatLng position) {
      googleMap.clear();
      final Marker marker = googleMap.addMarker(
            new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable()))
                  .position(position)
      );
      googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
      return marker;
   }

   private Bitmap getBitmapFromVectorDrawable() {
      Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_dining_pin_icon);
      Bitmap bitmap = createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);
      return bitmap;
   }

   @Override
   public void showDisableConfirmationDialog() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_disable_tracking_msg)
            .positiveText(R.string.wallet_disable_tracking)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> getPresenter().disableTracking())
            .onNegative((dialog, which) -> getPresenter().disableTrackingCanceled())
            .cancelListener(dialog -> getPresenter().disableTrackingCanceled())
            .build()
            .show();
   }

   @Override
   public void showRationaleForLocation() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_location_permission_message)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> getPresenter().onPermissionRationaleClick())
            .build()
            .show();
   }

   @Override
   public void showDeniedForLocation() {
      Snackbar.make(getView(), R.string.wallet_lost_card_no_permission, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(getView());
   }

   private void initMap() {
      mapView.getMapAsync(googleMap -> {
         this.googleMap = googleMap;

         final UiSettings uiSettings = googleMap.getUiSettings();
         uiSettings.setAllGesturesEnabled(true);
         uiSettings.setZoomControlsEnabled(true);

         googleMap.setMyLocationEnabled(true);
         onMapPrepared();
      });
   }

   protected void onMapPrepared() {
      getPresenter().onMapPrepared();
   }

   @Override
   public LostCardPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_lost_card, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return true;
   }
}
