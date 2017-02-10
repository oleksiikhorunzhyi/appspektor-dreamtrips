package com.worldventures.dreamtrips.wallet.ui.settings.lostcard;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.map.SmartCardLocaleInfoWindow;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.LostCardPin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

   private final SimpleDateFormat lastConnectedDateFormat = new SimpleDateFormat("EEEE, MMMM dd, h:mma", Locale.US);

   private Observable<Boolean> enableTrackingObservable;

   private MaterialDialog dialogErrorLocationService = null;

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

   @NonNull
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

      mapView.onCreate(null);

      tvDisableLostCardMsg.setText(Html.fromHtml(getString(R.string.wallet_lost_card_empty_view)));
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      if(isInEditMode()) return;
      mapView.onResume();
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      if(isInEditMode()) return;
      mapView.onPause();
      mapView.onDestroy();
   }

   private void checkGoogleServicesAndInitMap() {
      noGoogleContainer.setVisibility(View.GONE);
      if (!trackingEnableSwitcher.isChecked()) return;
      if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext()) != ConnectionResult.SUCCESS) {
         setVisibilityMap(false);
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
   public void setVisibilityMap(boolean visible) {
      if (mapContainer.getVisibility() == GONE) {
         ObjectAnimator.ofFloat(mapView, "alpha", 0f, 1f)
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
   public void toggleLostCardSwitcher(boolean checked) {
      trackingEnableSwitcher.setChecked(checked);
   }

   @Override
   public void addPin(@NonNull LostCardPin lostCardPin) {
      mapView.getMapAsync(googleMap -> {
         SmartCardLocaleInfoWindow popupPinWindow = new SmartCardLocaleInfoWindow(getContext(), lostCardPin);
         googleMap.setInfoWindowAdapter(popupPinWindow);
         googleMap.clear();

         Marker marker = googleMap.addMarker(
               new MarkerOptions()
                     .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable()))
                     .position(walletCoordinatesToLatLng(lostCardPin.position()))
                     .snippet(/*lostCardPin.place()*/ "")
         );

         googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
         marker.showInfoWindow();

         googleMap.setOnInfoWindowClickListener(m -> popupPinWindow.openExternalMap());

         googleMap.setOnMarkerClickListener(m -> {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
            marker.showInfoWindow();
            return false;
         });
      });
   }

   private LatLng walletCoordinatesToLatLng(WalletCoordinates position) {
      return new LatLng(
            position.lat(),
            position.lng()
      );
   }

   private Bitmap getBitmapFromVectorDrawable() {
      Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_dining_pin_icon);
      Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);
      return bitmap;
   }

   @Override
   public void onTrackingChecked(boolean checked) {
      checkGoogleServicesAndInitMap();
   }

   private void showDialogDisableTrackingConfirmation() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_disable_tracking_msg)
            .positiveText(R.string.wallet_disable_tracking)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> presenter.disableTracking())
            .onNegative((dialog, which) -> toggleLostCardSwitcher(true))
            .build()
            .show();
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
      toggleLostCardSwitcher(false);
   }

   @Override
   public void showOpenLocationServiceSettingsDialog() {
      if (dialogErrorLocationService == null) {
         dialogErrorLocationService = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_location_service_settings_title)
               .content(R.string.wallet_location_service_settings_message)
               .positiveText(R.string.ok)
               .negativeText(R.string.cancel)
               .onPositive((dialog, which) -> presenter.requestLocationSettings())
               .build();
      }
      if (!dialogErrorLocationService.isShowing()) {
         dialogErrorLocationService.show();
      }
   }

   @Override
   public boolean showDisableConfirmationDialogIfNeed(boolean enableTracking) {
      if (mapContainer.getVisibility() == VISIBLE && !enableTracking) {
         showDialogDisableTrackingConfirmation();
         return true;
      }
      return false;
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   private void initMap() {
      mapView.getMapAsync(googleMap -> {
         googleMap.setMyLocationEnabled(true);
         UiSettings uiSettings = googleMap.getUiSettings();
         uiSettings.setAllGesturesEnabled(true);
         uiSettings.setZoomControlsEnabled(true);

         onMapLoaded();
      });
   }

   protected void onMapLoaded() {
      if (trackingEnableSwitcher.isChecked()) presenter.loadLastSmartCardLocation();
   }
}
