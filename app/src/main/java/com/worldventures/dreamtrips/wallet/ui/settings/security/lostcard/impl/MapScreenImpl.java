package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.impl;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerChangeType;
import com.bluelinelabs.conductor.rxlifecycle.RxRestoreViewOnCreateController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.MapScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.MapPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.adapter.LostCardInfoWindowAdapter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.model.LostCardPin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static android.graphics.Bitmap.createBitmap;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MapScreenImpl extends RxRestoreViewOnCreateController implements MapScreen, OnMapReadyCallback  {

   @InjectView(R.id.last_connection_time_container) View lastConnectionTimeContainer;
   @InjectView(R.id.map_view) ToucheableMapView mapView;
   @InjectView(R.id.empty_location_view) View emptyLocationsView;
   @InjectView(R.id.last_connected_label) TextView tvLastConnectionLabel;
   @InjectView(R.id.noGoogleContainer) View noGoogleContainer;

   @Inject MapPresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private final SimpleDateFormat lastConnectedDateFormat = new SimpleDateFormat("EEEE, MMMM dd, h:mma", Locale.US);

   private GoogleMap googleMap;

   @NonNull
   @Override
   protected View onCreateView(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup, @Nullable Bundle bundle) {
      final View view = layoutInflater.inflate(R.layout.screen_wallet_settings_lostcard_map, viewGroup, false);
      //noinspection all
      final ObjectGraph objectGraph = (ObjectGraph) view.getContext().getSystemService(InjectingActivity.OBJECT_GRAPH_SERVICE_NAME);
      objectGraph.inject(this);
      ButterKnife.inject(this, view);
      if (MapsInitializer.initialize(view.getContext()) != ConnectionResult.SUCCESS) {
         noGoogleContainer.setVisibility(View.VISIBLE);
      } else {
         mapView.onCreate(bundle);
      }
      return view;
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      getPresenter().attachView(this);
      mapView.onResume();
   }

   @Override
   protected void onDetach(@NonNull View view) {
      super.onDetach(view);
      getPresenter().detachView(true);
      mapView.onPause();
   }

   @Override
   protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
      mapView.onSaveInstanceState(outState);
      super.onSaveViewState(view, outState);
   }

   @Override
   protected void onDestroyView(@NonNull View view) {
      super.onDestroyView(view);
      mapView.onDestroy();
      ButterKnife.reset(this);
   }

   @Override
   protected void onChangeEnded(@NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
      super.onChangeEnded(changeHandler, changeType);
      if (changeType == ControllerChangeType.PUSH_ENTER || changeType == ControllerChangeType.POP_ENTER) {
         mapView.getMapAsync(this);
      }
   }

   @Override
   public boolean handleBack() {
      return false;
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
      Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_wallet_vector_dining_pin);
      Bitmap bitmap = createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);
      return bitmap;
   }

   @Override
   public void onMapReady(GoogleMap googleMap) {
      this.googleMap = googleMap;

      final UiSettings uiSettings = googleMap.getUiSettings();
      uiSettings.setAllGesturesEnabled(true);
      uiSettings.setZoomControlsEnabled(true);

      googleMap.setMyLocationEnabled(true);
      getPresenter().onMapPrepared();
   }

   @Override
   public void setLastConnectionDate(Date date) {
      tvLastConnectionLabel.setText(lastConnectedDateFormat.format(date));
   }

   @Override
   public OperationView<FetchAddressWithPlacesCommand> provideOperationView() {
      //noinspection unchecked cast
      return new ComposableOperationView<>(null, null, ErrorViewFactory.<FetchAddressWithPlacesCommand>builder()
            .addProvider(new HttpErrorViewProvider<>(getActivity(), httpErrorHandlingUtil,
                  getPresenter()::retryFetchAddressWithPlaces,
                  fetchAddressWithPlacesCommand -> {
                  }))
            .build());
   }

   @Override
   public void setVisibleMsgEmptyLastLocation(boolean visible) {
      emptyLocationsView.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   public void setVisibleLastConnectionTime(boolean visible) {
      lastConnectionTimeContainer.setVisibility(visible ? VISIBLE : GONE);
   }

   private MapPresenter getPresenter() {
      return presenter;
   }
}
