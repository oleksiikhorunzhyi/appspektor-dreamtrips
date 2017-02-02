package com.worldventures.dreamtrips.wallet.ui.settings.lostcard;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.map.SmartCardLocaleInfoWindow;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.LostCardPin;

import butterknife.InjectView;
import rx.Observable;

public class LostCardScreen extends WalletLinearLayout<LostCardPresenter.Screen, LostCardPresenter, LostCardPath>
      implements LostCardPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.tracking_enable_switcher) SwitchCompat trackingEnableSwitcher;
   @InjectView(R.id.ll_disable_tracking_info_view) View disabledTrackingView;
   @InjectView(R.id.last_connection_time_container) View lastConnectionTimeContainer;
   @InjectView(R.id.noGoogleContainer) View noGoogleContainer;
   @InjectView(R.id.map_container) View mapContainer;
   @InjectView(R.id.tv_empty_lost_card_msg) TextView tvDisableLostCardMsg;
   @InjectView(R.id.tv_empty_last_location_msg) TextView tvEmptyLastLocationMsg;
   @InjectView(R.id.last_connected_label) TextView tvLastConnectionLabel;
   @InjectView(R.id.map_view) ToucheableMapView mapView;

   private GoogleMap googleMap;

   private Observable<Boolean> enableTrackingObservable;

   public LostCardScreen(Context context) {
      super(context);
   }

   public LostCardScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public LostCardPresenter createPresenter() {
      return new LostCardPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      if (isInEditMode()) return;
      enableTrackingObservable = RxCompoundButton.checkedChanges(trackingEnableSwitcher);

      tvDisableLostCardMsg.setText(Html.fromHtml(getString(R.string.wallet_lost_card_empty_view)));
      checkGoogleServicesAndInitMap();
   }

   private void checkGoogleServicesAndInitMap() {
      noGoogleContainer.setVisibility(View.GONE);
      if (!trackingEnableSwitcher.isChecked()) return;
      if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext()) != ConnectionResult.SUCCESS) {
         toggleVisibleMap(false);
         toggleVisibleMsgEmptyLastLocation(false);
         toggleVisibleDisabledOfTrackingView(false);
         noGoogleContainer.setVisibility(View.VISIBLE);
      } else {
         MapsInitializer.initialize(getContext());
         initMap();
      }
   }

   protected void onNavigationClick() {
      presenter.goBack();
   }

   @Override
   public Observable<Boolean> observeTrackingEnable() {
      return enableTrackingObservable;
   }

   @Override
   public void toggleVisibleDisabledOfTrackingView(boolean visible) {
      disabledTrackingView.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   public void toggleVisibleMsgEmptyLastLocation(boolean visible) {
      tvEmptyLastLocationMsg.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   public void toggleVisibleLastConnectionTime(boolean visible) {
      lastConnectionTimeContainer.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   public void toggleVisibleMap(boolean visible) {
      mapContainer.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   public void setLastConnectionLabel(String lastConnection) {
      tvLastConnectionLabel.setText(lastConnection);
   }

   @Override
   public void toggleSwitcher(boolean checked) {
      trackingEnableSwitcher.setChecked(checked);
   }

   @Override
   public void addPin(LostCardPin lostCardPin) {
      if (googleMap != null && lostCardPin != null) {
         googleMap.setInfoWindowAdapter(new SmartCardLocaleInfoWindow(getContext(), lostCardPin));

         Marker marker = googleMap.addMarker(
               new MarkerOptions()
                     .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dining_pin_icon))
                     .position(lostCardPin.position())
                     .snippet(lostCardPin.place())
         );

         marker.showInfoWindow();
      }
   }

   @Override
   public void onTrackingChecked(boolean checked) {
      checkGoogleServicesAndInitMap();
   }

   @Override
   public void showRationaleForLocation() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_location_permission_message)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> presenter.requestPermissions(trackingEnableSwitcher.isChecked()))
            .build()
            .show();
   }

   @Override
   public void showDeniedForLocation() {
      Snackbar.make(this, R.string.wallet_lost_card_no_permission, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   private void initMap() {
      mapView.getMapAsync(map -> {
         googleMap = map;
         googleMap.setMyLocationEnabled(true);
         UiSettings uiSettings = googleMap.getUiSettings();
         uiSettings.setAllGesturesEnabled(true);
         uiSettings.setZoomControlsEnabled(true);
         googleMap.setOnMarkerClickListener(onMarkerClickListener);

         onMapLoaded();
      });
   }

   private GoogleMap.OnMarkerClickListener onMarkerClickListener = marker -> {
      googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 5));
      marker.showInfoWindow();
      return false;
   };

   protected void onMapLoaded() {
      if (trackingEnableSwitcher.isChecked()) presenter.loadLastSmartCardLocation();
   }
}
