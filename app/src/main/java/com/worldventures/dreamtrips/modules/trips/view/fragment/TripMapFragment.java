package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.ui.fragment.FragmentHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.map.reactive.MapObservableFactory;
import com.worldventures.dreamtrips.modules.trips.model.TripClusterItem;
import com.worldventures.dreamtrips.modules.trips.model.TripMapDetailsAnchor;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapPresenter;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripMapListBundle;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;
import com.worldventures.dreamtrips.modules.trips.view.util.ContainerDetailsMapParamsBuilder;
import com.worldventures.dreamtrips.modules.trips.view.util.TripClusterRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;
import icepick.Icepick;
import icepick.State;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.worldventures.dreamtrips.modules.trips.view.util.ContainerDetailsMapParamsBuilder.TRIANGLE_HEIGHT_DP;

@Layout(R.layout.fragment_trips_map)
@MenuResource(R.menu.menu_map)
public class TripMapFragment extends RxBaseFragment<TripMapPresenter> implements TripMapPresenter.View {

   private static final String KEY_MAP = "map";
   private static final int SMALL_PADDING = 20;

   protected ToucheableMapView mapView;
   @InjectView(R.id.container_info) protected FrameLayout containerInfo;
   @InjectView(R.id.container_info_wrapper) protected ViewGroup containerInfoWrapper;
   @InjectView(R.id.container_no_google) protected FrameLayout noGoogleContainer;
   @Optional @InjectView(R.id.left_pointer) View leftPointer;
   @Optional @InjectView(R.id.right_pointer) View rightPointer;
   @Optional @InjectView(R.id.bottom_pointer) View bottomPointer;
   @Optional @InjectView(R.id.left_space) View leftSpace;
   @Optional @InjectView(R.id.right_space) View rightSpace;

   protected GoogleMap googleMap;
   private Bundle mapBundle;

   @Inject BackStackDelegate backStackDelegate;
   @State boolean searchOpened;

   private LatLng selectedLocation;

   private Subscription mapChangesSubscription;
   private Subscription markersClickSubscription;

   private ClusterManager<TripClusterItem> clusterManager;
   private TripClusterRenderer tripClusterRenderer;

   @Override
   protected TripMapPresenter createPresenter(Bundle savedInstanceState) {
      return new TripMapPresenter();
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Icepick.restoreInstanceState(this, savedInstanceState);
      if (savedInstanceState != null) {
         mapBundle = savedInstanceState.getBundle(KEY_MAP);
      }
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      Icepick.saveInstanceState(this, outState);
      if (mapView != null) {
         Bundle mapBundle = new Bundle();
         outState.putBundle(KEY_MAP, mapBundle);
         mapView.onSaveInstanceState(mapBundle);
      }
      super.onSaveInstanceState(outState);
   }

   @Override
   public void afterCreateView(View rootView) {
      mapView = (ToucheableMapView) rootView.findViewById(R.id.map);
      if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) != ConnectionResult.SUCCESS) {
         mapView.setVisibility(View.GONE);
         noGoogleContainer.setVisibility(View.VISIBLE);
      } else {
         MapsInitializer.initialize(rootView.getContext());
         mapView.onCreate(mapBundle);
      }
      initMap();
   }

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      if (savedInstanceState != null) {
         getPresenter().removeInfoIfNeeded();
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      mapView.onResume();
      backStackDelegate.setListener(this::onBackPressed);
   }

   @Override
   public void onPause() {
      super.onPause();
      mapView.onPause();
      backStackDelegate.setListener(null);
   }

   private boolean onBackPressed() {
      if (getChildFragmentManager().findFragmentById(R.id.container_info) instanceof TripMapListFragment) {
         removeTripsPopupInfo();
         return true;
      }
      return false;
   }

   private void moveToListView() {
      if (getChildFragmentManager().findFragmentById(R.id.container_info) instanceof TripMapListFragment) {
         removeTripsPopupInfo();
      }
      containerInfo.post(() -> router.back());
   }

   @Override
   public void onDestroyView() {
      FragmentHelper.resetChildFragmentManagerField(this);
      //
      if (mapView != null) {
         mapView.removeAllViews();
      }
      if (googleMap != null) {
         googleMap.clear();
         googleMap.setOnMarkerClickListener(null);
      }
      if (mapChangesSubscription != null && !mapChangesSubscription.isUnsubscribed()) {
         mapChangesSubscription.unsubscribe();
      }
      if (markersClickSubscription != null && !markersClickSubscription.isUnsubscribed()) {
         markersClickSubscription.unsubscribe();
      }
      //local clustering
      if (clusterManager != null) {
         clusterManager.setOnClusterItemClickListener(null);
         clusterManager.setOnClusterClickListener(null);
      }
      //
      super.onDestroyView();
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
      if (mapView != null) {
         mapView.onDestroy();
         mapView = null;
      }
      googleMap = null;
   }

   @Override
   public void onLowMemory() {
      super.onLowMemory();
      mapView.onLowMemory();
   }

   private void initMap() {
      mapView.getMapAsync(map -> {
         googleMap = map;
         googleMap.setMyLocationEnabled(true);
         mapView.setMapTouchListener(this::onMapTouched);
         //local clustering
         clusterManager = new ClusterManager<>(getActivity(), googleMap);
         tripClusterRenderer = new TripClusterRenderer(getContext(), googleMap, clusterManager);
         clusterManager.setRenderer(tripClusterRenderer);
         clusterManager.setOnClusterClickListener(cluster -> {
            if (googleMap.getCameraPosition().zoom >= 17.0f) {
               selectedLocation = cluster.getPosition();
               getPresenter().onMarkerClicked(tripClusterRenderer.getMarker(Queryable.from(cluster.getItems())
                     .first()));
            } else {
               LatLngBounds.Builder builder = LatLngBounds.builder();
               for (TripClusterItem item : cluster.getItems()) {
                  builder.include(item.getPosition());
               }
               final LatLngBounds bounds = builder.build();
               googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
            return true;
         });
         clusterManager.setOnClusterItemClickListener(tripClusterItem -> {
            getPresenter().onMarkerClicked(tripClusterRenderer.getMarker(tripClusterItem));
            return true;
         });
         //
         onMapLoaded();
      });
   }

   @Override
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);
      MenuItem searchItem = menu.findItem(R.id.action_search);
      if (searchOpened) searchItem.expandActionView();
      MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
         @Override
         public boolean onMenuItemActionExpand(MenuItem item) {
            searchOpened = true;
            return true;
         }

         @Override
         public boolean onMenuItemActionCollapse(MenuItem item) {
            searchOpened = false;
            return true;
         }
      });
      SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
      searchView.setQuery(getPresenter().getQuery(), false);
      searchView.setOnCloseListener(() -> {
         TripMapFragment.this.getPresenter().applySearch(null);
         return false;
      });
      searchView.setOnQueryTextListener(onQueryTextListener);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      getPresenter().onCameraChanged();
      switch (item.getItemId()) {
         case R.id.action_filter:
            ((MainActivity) getActivity()).openRightDrawer();
            break;
         case R.id.action_list:
            moveToListView();
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Map stuff
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void addMarker(MarkerOptions options) {
      getPresenter().addMarker(googleMap.addMarker(options));
   }

   @Override
   public void moveTo(List<TripModel> trips) {
      router.moveTo(Route.MAP_INFO, NavigationConfigBuilder.forFragment()
            .containerId(R.id.container_info)
            .fragmentManager(getChildFragmentManager())
            .backStackEnabled(false)
            .data(new TripMapListBundle(trips))
            .build());
   }

   protected int calculateOffset(int size) {
      Rect rect = new Rect();
      mapView.getLocalVisibleRect(rect);
      int maxHeight = (int) (rect.bottom - getResources().getDimension(R.dimen.map_trip_detail_spacing));
      int result = (int) ((rect.bottom - rect.top) / 2 - (Math.min(maxHeight, size * getResources().getDimension(R.dimen.map_trip_detail_cell_height))));
      return result - SMALL_PADDING;
   }

   private void animateToMarker(LatLng latLng, int offset) {
      Projection projection = googleMap.getProjection();
      Point screenLocation = projection.toScreenLocation(latLng);
      screenLocation.set(screenLocation.x, screenLocation.y - offset);
      LatLng offsetTarget = projection.fromScreenLocation(screenLocation);
      googleMap.animateCamera(CameraUpdateFactory.newLatLng(offsetTarget));
   }

   @Override
   public void removeTripsPopupInfo() {
      router.moveTo(Route.MAP_INFO, NavigationConfigBuilder.forRemoval()
            .containerId(R.id.container_info)
            .fragmentManager(getChildFragmentManager())
            .build());
      selectedLocation = null;
      hideInfoContainer();
   }

   @Override
   public void zoomToBounds(LatLngBounds latLngBounds) {
      googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
   }

   @Override
   public void setSelectedLocation(LatLng latLng) {
      selectedLocation = latLng;
   }

   @Override
   public GoogleMap getMap() {
      return googleMap;
   }

   @Override
   public void updateContainerParams(int tripCount) {
      Rect rect = new Rect();
      mapView.getLocalVisibleRect(rect);
      Pair<FrameLayout.LayoutParams, TripMapDetailsAnchor> pair = new ContainerDetailsMapParamsBuilder().mapRect(rect)
            .markerPoint(googleMap.getProjection().toScreenLocation(selectedLocation))
            .context(getContext())
            .tripsCount(tripCount)
            .tabletLandscape(isTabletLandscape())
            .build();
      containerInfoWrapper.setLayoutParams(pair.first);
      updatePointerPosition(pair.second);
   }

   @Override
   public void scrollCameraToPin(int size) {
      if (!isTabletLandscape()) {
         int height = calculateOffset(size);
         animateToMarker(selectedLocation, height);
      }
   }

   @Override
   public void showInfoContainer() {
      containerInfoWrapper.setVisibility(View.VISIBLE);
   }

   public void hideInfoContainer() {
      containerInfoWrapper.setVisibility(View.GONE);
   }

   protected void onMapLoaded() {
      getPresenter().onMapLoaded();
      mapChangesSubscription = subscribeToCameraChanges();
      markersClickSubscription = subscribeToMarkersClicks();
   }

   private Subscription subscribeToCameraChanges() {
      return MapObservableFactory.createCameraChangeObservable(googleMap)
            .throttleLast(100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(cameraPosition -> {
               //                    getPresenter().reloadMapObjects();
               //local clustering
               clusterManager.onCameraChange(cameraPosition);
               //
            }, error -> {
               Timber.e(error.getMessage());
            });
   }

   private Subscription subscribeToMarkersClicks() {
      return MapObservableFactory.createMarkerClickObservable(googleMap).subscribe(marker -> {
         //                    getPresenter().onMarkerClicked(marker);
         //local clustering
         clusterManager.onMarkerClick(marker);
         //
      }, error -> {
         Timber.e(error, error.getMessage());
      });
   }

   private void updatePointerPosition(TripMapDetailsAnchor anchor) {
      if (anchor != null && isTabletLandscape()) {
         FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) containerInfo.getLayoutParams();
         int margin = (int) ViewUtils.pxFromDp(getContext(), TRIANGLE_HEIGHT_DP);
         switch (anchor.getPointerPosition()) {
            case BOTTOM:
               bottomPointer.setVisibility(View.VISIBLE);
               leftPointer.setVisibility(View.GONE);
               rightPointer.setVisibility(View.GONE);
               params.bottomMargin = margin;
               params.leftMargin = 0;
               params.rightMargin = 0;
               break;
            case LEFT:
               leftPointer.setVisibility(View.VISIBLE);
               bottomPointer.setVisibility(View.GONE);
               rightPointer.setVisibility(View.GONE);
               leftSpace.getLayoutParams().height = anchor.getMargin();
               leftSpace.requestLayout();
               params.leftMargin = margin;
               params.bottomMargin = 0;
               params.rightMargin = 0;
               break;
            case RIGHT:
               rightPointer.setVisibility(View.VISIBLE);
               leftPointer.setVisibility(View.GONE);
               bottomPointer.setVisibility(View.GONE);
               rightSpace.getLayoutParams().height = anchor.getMargin();
               rightSpace.requestLayout();
               params.rightMargin = margin;
               params.bottomMargin = 0;
               params.leftMargin = 0;
               break;
         }
      }
   }

   protected void onMapTouched() {
      getPresenter().onCameraChanged();
   }

   private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String s) {
         return false;
      }

      @Override
      public boolean onQueryTextChange(String s) {
         if (getPresenter() != null) {
            getPresenter().applySearch(s);
         }
         return true;
      }
   };

   //local clustering
   @Override
   public void addItems(List<TripClusterItem> tripClusterItems) {
      clusterManager.addItems(tripClusterItems);
      clusterManager.cluster();
   }

   @Override
   public void clearItems() {
      clusterManager.clearItems();
   }

   @Override
   public List<Marker> getMarkers() {
      List<Marker> markers = new ArrayList<>();
      markers.addAll(clusterManager.getClusterMarkerCollection().getMarkers());
      markers.addAll(clusterManager.getMarkerCollection().getMarkers());
      return markers;
   }
   //
}