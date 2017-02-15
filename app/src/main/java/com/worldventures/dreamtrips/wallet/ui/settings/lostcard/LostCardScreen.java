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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jakewharton.rxbinding.view.RxView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;
import com.worldventures.dreamtrips.wallet.service.location.LocationSettingsService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.adapter.LostCardInfoWindowAdapter;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.LostCardPin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static android.graphics.Bitmap.createBitmap;

public class LostCardScreen extends WalletLinearLayout<LostCardPresenter.Screen, LostCardPresenter, LostCardPath>
      implements LostCardPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.tracking_enable_switcher) SwitchCompat trackingEnableSwitcher;
   @InjectView(R.id.ll_disable_tracking_info_view) View disabledTrackingView;
   @InjectView(R.id.last_connection_time_container) View lastConnectionTimeContainer;
   //   @InjectView(R.id.noGoogleContainer) View noGoogleContainer;
   @InjectView(R.id.map_container) View mapContainer;
   @InjectView(R.id.tv_empty_lost_card_msg) TextView tvDisableLostCardMsg;
   @InjectView(R.id.tv_tracking_enabled_empty_location) TextView tvTrackingEnabledEmptyLocations;
   @InjectView(R.id.last_connected_label) TextView tvLastConnectionLabel;
   @InjectView(R.id.map_view) ToucheableMapView mapView;

   private final SimpleDateFormat lastConnectedDateFormat = new SimpleDateFormat("EEEE, MMMM dd, h:mma", Locale.US);

   private Observable<Boolean> enableTrackingObservable;

   private GoogleMap googleMap;

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
      final Context context = getContext();
      //noinspection all
      final LocationSettingsService locationSettingsService = (LocationSettingsService) context.getSystemService(LocationSettingsService.SERVICE_NAME);
      return new LostCardPresenter(context, locationSettingsService, getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      if (isInEditMode()) return;
      enableTrackingObservable = RxView.clicks(trackingEnableSwitcher).map(aVoid -> trackingEnableSwitcher.isChecked());

      mapView.onCreate(null);
      initMap();
      //// TODO: 2/14/17 fromHtml is deprecated from 24 API
      //noinspection all
      tvDisableLostCardMsg.setText(Html.fromHtml(getString(R.string.wallet_lost_card_empty_view)));
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      if (isInEditMode()) return;
      mapView.onResume();
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      if (isInEditMode()) return;
      mapView.onPause();
      mapView.onDestroy();
   }

   protected void onNavigationClick() {
      presenter.goBack();
   }

   @Override
   public Observable<Boolean> observeTrackingEnable() {
      return enableTrackingObservable;
   }

   @Override
   public OperationView<FetchAddressWithPlacesCommand> provideOperationView() {
      //noinspection unchecked cast
      return new ComposableOperationView<>(null, null, ErrorViewFactory.<FetchAddressWithPlacesCommand>builder()
            .addProvider(new HttpErrorViewProvider<>(getContext(),
                  presenter::retryFetchAddressWithPlaces,
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
      tvTrackingEnabledEmptyLocations.setVisibility(visible ? VISIBLE : GONE);
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
      trackingEnableSwitcher.setChecked(checked);
   }

   @Override
   public void addPin(@NonNull LostCardPin pinData) {
      final Marker marker = clearMapAndAttachMarker(pinData.position());
      final LostCardInfoWindowAdapter infoWindowAdapter = new LostCardInfoWindowAdapter(this, pinData);
      googleMap.setInfoWindowAdapter(infoWindowAdapter);
      googleMap.setOnInfoWindowClickListener(m -> infoWindowAdapter.openExternalMap());
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
            .onPositive((dialog, which) -> presenter.disableTracking())
            .onNegative((dialog, which) -> presenter.disableTrackingCanceled())
            .build()
            .show();
   }

   @Override
   public void showRationaleForLocation() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_location_permission_message)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> presenter.onPermissionRationaleClick())
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
      presenter.onMapPrepared();
   }
}
