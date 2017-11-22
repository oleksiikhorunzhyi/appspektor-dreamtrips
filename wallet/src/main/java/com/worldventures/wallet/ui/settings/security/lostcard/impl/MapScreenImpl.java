package com.worldventures.wallet.ui.settings.security.lostcard.impl;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.rxlifecycle.ControllerEvent;
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
import com.trello.rxlifecycle.LifecycleTransformer;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.ui.view.custom.ToucheableMapView;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.R;
import com.worldventures.wallet.databinding.WalletIncludeMapPopupInfoBinding;
import com.worldventures.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.wallet.domain.entity.lostcard.WalletPlace;
import com.worldventures.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.wallet.ui.settings.security.lostcard.MapPresenter;
import com.worldventures.wallet.ui.settings.security.lostcard.MapScreen;
import com.worldventures.wallet.ui.settings.security.lostcard.model.LostCardPin;
import com.worldventures.wallet.ui.settings.security.lostcard.model.PopupLastLocationViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.String.format;

public class MapScreenImpl extends RxRestoreViewOnCreateController implements MapScreen, OnMapReadyCallback {

   @Inject MapPresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private final SimpleDateFormat lastConnectedDateFormat = new SimpleDateFormat("EEEE, MMMM dd, h:mma", Locale.US);

   private ToucheableMapView mapView;
   private View emptyLocationsView;
   private View noGoogleContainer;
   private GoogleMap googleMap;
   private WalletIncludeMapPopupInfoBinding popupInfoViewBinding;
   private PopupLastLocationViewModel lastLocationViewModel = new PopupLastLocationViewModel();

   @NonNull
   @Override
   protected View onCreateView(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup, @Nullable Bundle bundle) {
      final View view = layoutInflater.inflate(R.layout.subscreen_wallet_settings_lostcard_map, viewGroup, false);
      //noinspection all
      final ObjectGraph objectGraph = (ObjectGraph) view.getContext()
            .getSystemService(Injector.OBJECT_GRAPH_SERVICE_NAME);
      objectGraph.inject(this);
      mapView = view.findViewById(R.id.map_view);
      if (MapsInitializer.initialize(view.getContext()) != ConnectionResult.SUCCESS) {
         noGoogleContainer.setVisibility(VISIBLE);
      } else {
         mapView.onCreate(bundle);
      }
      mapView.getMapAsync(this);
      emptyLocationsView = view.findViewById(R.id.empty_location_view);
      noGoogleContainer = view.findViewById(R.id.noGoogleContainer);
      final View popupInfoContainer = view.findViewById(R.id.ll_popup_info);
      popupInfoViewBinding = DataBindingUtil.bind(popupInfoContainer);
      return view;
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      getPresenter().attachView(this);
      mapView.onResume();

      mapView.setMapTouchListener2(motionEvent -> {
         if (!lastLocationViewModel.hasLastLocation()) {
            return;
         }
         switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
               popupInfoViewBinding.setVisible(false);
               break;
            case MotionEvent.ACTION_UP:
               popupInfoViewBinding.setVisible(true);
               break;
            default:
               break;
         }
      });
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
   }

   @Override
   public boolean handleBack() {
      return false;
   }

   @Override
   public void addPin(@NonNull LostCardPin pinData) {
      clearMapAndAttachMarker(pinData.getPosition());
      lastLocationViewModel.setPlace(obtainPlace(pinData.getPlaces()));
      lastLocationViewModel.setAddress(obtainAddress(pinData.getAddress()));
      setVisibleMsgEmptyLastLocation(false);

      popupInfoViewBinding.setLastLocation(lastLocationViewModel);
      popupInfoViewBinding.setDirectionClick(view -> {
         openExternalMap(pinData.getPosition());
         getPresenter().trackDirectionsClick();
      });
   }

   private String obtainPlace(List<WalletPlace> places) {
      return places != null && places.size() == 1 ? places.get(0).getName() : "";
   }

   private String obtainAddress(WalletAddress address) {
      return address != null
            ? format("%s, %s\n%s", address.getCountryName(), address.getAdminArea(), address.getAddressLine())
            : "";
   }

   private void openExternalMap(LatLng position) {
      Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"
            + position.latitude + "," + position.longitude + "?z=17&q="
            + position.latitude + "," + position.longitude));
      startActivity(map);
   }

   @Override
   public void addPin(LatLng position) {
      clearMapAndAttachMarker(position);
   }

   private Marker clearMapAndAttachMarker(LatLng position) {
      googleMap.clear();
      final Marker marker = googleMap.addMarker(
            new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.wallet_image_pin_smart_card))
                  .position(position)
      );
      position = new LatLng(position.latitude + 0.00045, position.longitude);
      googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
      return marker;
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
      lastLocationViewModel.setLastConnectedDate(lastConnectedDateFormat.format(date));
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
   public <T> LifecycleTransformer<T> bindUntilDetach() {
      return bindUntilEvent(ControllerEvent.DETACH);
   }

   @Override
   public void setVisibleMsgEmptyLastLocation(boolean visible) {
      emptyLocationsView.setVisibility(visible ? VISIBLE : GONE);
      popupInfoViewBinding.setVisible(!visible);
   }

   private MapPresenter getPresenter() {
      return presenter;
   }
}
