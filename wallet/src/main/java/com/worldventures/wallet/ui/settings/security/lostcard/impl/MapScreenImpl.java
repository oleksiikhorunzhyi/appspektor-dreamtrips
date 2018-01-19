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

import com.bluelinelabs.conductor.RestoreViewOnCreateController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.ui.view.custom.ToucheableMapView;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.R;
import com.worldventures.wallet.databinding.WalletIncludeMapPopupInfoBinding;
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.wallet.ui.settings.security.lostcard.MapPresenter;
import com.worldventures.wallet.ui.settings.security.lostcard.MapScreen;
import com.worldventures.wallet.ui.settings.security.lostcard.model.LostCardPin;
import com.worldventures.wallet.ui.settings.security.lostcard.model.PopupLastLocationViewModel;
import com.worldventures.wallet.util.WalletLocationsUtil;

import java.util.Date;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;
import rx.subjects.PublishSubject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MapScreenImpl extends RestoreViewOnCreateController implements MapScreen, OnMapReadyCallback {

   private static final String STATE_KEY_POPUP_MODEL = "MapScreenImpl#STATE_KEY_POPUP_MODEL";
   private static final String STATE_KEY_MAP_POSITION = "MapScreenImpl#STATE_KEY_MAP_POSITION";

   @Inject MapPresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private final PublishSubject<Void> detachStopper = PublishSubject.create();

   private ToucheableMapView mapView;
   private View emptyLocationsView;
   private View noGoogleContainer;

   @Nullable private GoogleMap googleMap;
   private WalletIncludeMapPopupInfoBinding popupInfoViewBinding;

   private PopupLastLocationViewModel lastLocationViewModel = new PopupLastLocationViewModel();
   private LatLng lastPosition;

   @NonNull
   @Override
   protected View onCreateView(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup, @Nullable Bundle bundle) {
      final View view = layoutInflater.inflate(R.layout.subscreen_wallet_settings_lostcard_map, viewGroup, false);
      //noinspection all
      final ObjectGraph objectGraph = ((ObjectGraph) view.getContext()
            .getSystemService(Injector.OBJECT_GRAPH_SERVICE_NAME)).plus(new MapScreenModule());
      objectGraph.inject(this);
      mapView = view.findViewById(R.id.map_view);
      emptyLocationsView = view.findViewById(R.id.empty_location_view);
      noGoogleContainer = view.findViewById(R.id.noGoogleContainer);
      final View popupInfoContainer = view.findViewById(R.id.ll_popup_info);
      popupInfoViewBinding = DataBindingUtil.bind(popupInfoContainer);
      if (MapsInitializer.initialize(view.getContext()) != ConnectionResult.SUCCESS) {
         noGoogleContainer.setVisibility(VISIBLE);
      } else {
         mapView.onCreate(bundle);
      }
      mapView.getMapAsync(this);
      return view;
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      getPresenter().attachView(this);
      mapView.onResume();

      popupInfoViewBinding.setViewModel(lastLocationViewModel);

      mapView.setMapTouchListener2(motionEvent -> {
         if (!lastLocationViewModel.hasLastLocation()) {
            return;
         }
         switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
               lastLocationViewModel.setVisible(false);
               break;
            case MotionEvent.ACTION_UP:
               lastLocationViewModel.setVisible(true);
               break;
            default:
               break;
         }
      });
   }

   @Override
   protected void onDetach(@NonNull View view) {
      super.onDetach(view);
      detachStopper.onNext(null);
      getPresenter().detachView(true);
      mapView.onPause();
   }

   @Override
   protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
      mapView.onSaveInstanceState(outState);
      outState.putParcelable(STATE_KEY_POPUP_MODEL, lastLocationViewModel);
      outState.putParcelable(STATE_KEY_MAP_POSITION, lastPosition);
      super.onSaveViewState(view, outState);
   }

   @Override
   protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
      super.onRestoreViewState(view, savedViewState);
      lastLocationViewModel = savedViewState.getParcelable(STATE_KEY_POPUP_MODEL);
      lastPosition = savedViewState.getParcelable(STATE_KEY_MAP_POSITION);
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
      lastLocationViewModel.setPlaces(pinData.getPlaces());
      lastLocationViewModel.setAddress(pinData.getAddress());

      popupInfoViewBinding.setDirectionClick(view -> {
         openExternalMap(pinData.getPosition());
         getPresenter().trackDirectionsClick();
      });
   }

   private void openExternalMap(WalletCoordinates position) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"
            + position.getLat() + "," + position.getLng() + "?z=17&q="
            + position.getLat() + "," + position.getLng())));
   }

   @Override
   public void setCoordinates(@Nullable WalletCoordinates position) {
      if (position != null) {
         final LatLng latLng = WalletLocationsUtil.INSTANCE.toLatLng(position);
         lastPosition = latLng;
         clearMapAndAttachMarker(latLng, true);
      }
      emptyLocationsView.setVisibility(position == null ? VISIBLE : GONE);
   }

   private void clearMapAndAttachMarker(LatLng position, boolean withAnimation) {
      googleMap.clear();
      googleMap.addMarker(
            new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.wallet_image_pin_smart_card))
                  .position(position)
      );
      position = new LatLng(position.latitude + 0.00045, position.longitude);
      if (withAnimation) {
         googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
      } else {
         googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));
      }
   }

   @Override
   public void onMapReady(GoogleMap googleMap) {
      this.googleMap = googleMap;

      final UiSettings uiSettings = googleMap.getUiSettings();
      uiSettings.setAllGesturesEnabled(true);
      uiSettings.setZoomControlsEnabled(true);

      googleMap.setMyLocationEnabled(true);

      if (lastPosition == null) {
         getPresenter().fetchLastKnownLocation();
      } else {
         clearMapAndAttachMarker(lastPosition, false);
      }
   }

   @Override
   public void setLastConnectionDate(Date date) {
      lastLocationViewModel.setLastConnectedDate(date);
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
   public <T> Observable.Transformer<T, T> bindUntilDetach() {
      return input -> input.takeUntil(detachStopper);
   }

   private MapPresenter getPresenter() {
      return presenter;
   }
}
