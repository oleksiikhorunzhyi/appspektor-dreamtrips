package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.innahema.collections.query.queriables.Queryable;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantType;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbarHelper;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.ExpandableDtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;
import com.worldventures.dreamtrips.modules.map.model.DtlClusterItem;
import com.worldventures.dreamtrips.modules.map.renderer.DtClusterRenderer;
import com.worldventures.dreamtrips.modules.map.view.MapViewUtils;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import de.greenrobot.event.EventBus;
import flow.path.Path;
import flow.path.PathContext;
import icepick.State;
import rx.Observable;

public class DtlMapScreenImpl extends DtlLayout<DtlMapScreen, DtlMapPresenter, DtlMapPath> implements DtlMapScreen {

   protected static final int CAMERA_DURATION = 1000;

   public static final String MAP_TAG = "MAP_TAG";

   @InjectView(R.id.mapTouchView) View mapTouchView;
   @InjectView(R.id.infoContainer) FrameLayout infoContainer;
   @InjectView(R.id.noGoogleContainer) FrameLayout noGoogleContainer;
   @Optional @InjectView(R.id.expandableDtlToolbar) ExpandableDtlToolbar dtlToolbar;
   @InjectView(R.id.redo_merchants) View loadMerchantsRoot;
   //
   LatLng selectedLocation;
   @State String lastQuery;
   //
   private ClusterManager<DtlClusterItem> clusterManager;
   private Marker locationPin;
   private GoogleMap googleMap;

   public DtlMapScreenImpl(Context context) {
      super(context);
   }

   public DtlMapScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public DtlMapPresenter createPresenter() {
      return new DtlMapPresenterImpl(getContext(), injector);
   }

   @Override
   protected void onPostAttachToWindowView() {
      checkMapAvailable();
      initToolbar();
   }

   @Override
   protected void onDetachedFromWindow() {
      destroyMap();
      super.onDetachedFromWindow();
   }

   protected void initToolbar() {
      if (dtlToolbar == null) return;
      RxDtlToolbar.actionViewClicks(dtlToolbar)
            .throttleFirst(250L, TimeUnit.MILLISECONDS)
            .compose(RxLifecycle.bindView(this))
            .subscribe(aVoid -> ((FlowActivity) getActivity()).openLeftDrawer());
      RxDtlToolbar.merchantSearchTextChanges(dtlToolbar)
            .skip(1)
            .debounce(250L, TimeUnit.MILLISECONDS)
            .filter(s -> !dtlToolbar.isCollapsed())
            .compose(RxLifecycle.bindView(this))
            .subscribe(getPresenter()::applySearch);
      RxDtlToolbar.locationInputFocusChanges(dtlToolbar)
            .skip(1)
            .compose(RxLifecycle.bindView(this))
            .filter(Boolean::booleanValue) // only true -> only focus gains
            .subscribe(aBoolean -> getPresenter().locationChangeRequested());
      RxDtlToolbar.navigationClicks(dtlToolbar)
            .throttleFirst(200L, TimeUnit.MILLISECONDS)
            .compose(RxLifecycle.bindView(this))
            .subscribe(aVoid -> getPresenter().onListClicked());
      RxDtlToolbar.filterButtonClicks(dtlToolbar)
            .compose(RxLifecycle.bindView(this))
            .subscribe(aVoid -> ((FlowActivity) getActivity()).openRightDrawer());
   }

   @Override
   public void setFilterButtonState(boolean enabled) {
      if (dtlToolbar == null) return;
      dtlToolbar.setFilterEnabled(enabled);
   }

   private void checkMapAvailable() {
      if (MapsInitializer.initialize(getActivity()) != ConnectionResult.SUCCESS) {
         destroyMap();
         noGoogleContainer.setVisibility(View.VISIBLE);
      }
   }

   @Override
   public void prepareMap() {
      releaseMapFragment();
      //
      MapFragment mapFragment = MapFragment.newInstance();
      getActivity().getFragmentManager()
            .beginTransaction()
            .add(R.id.mapFragmentContainer, mapFragment, MAP_TAG)
            .commit();
      //
      mapFragment.getMapAsync(map -> {
         googleMap = map;
         googleMap.clear();
         googleMap.setMyLocationEnabled(true);
         //
         MapViewUtils.setLocationButtonGravity(mapFragment.getView(), 16, RelativeLayout.ALIGN_PARENT_END, RelativeLayout.ALIGN_PARENT_BOTTOM);
         onMapLoaded();
      });
      mapTouchView.setOnTouchListener((v, event) -> {
         if (event.getAction() == MotionEvent.ACTION_DOWN) {
            hideInfoIfShown();
         }
         return false;
      });
   }

   private void destroyMap() {
      if (googleMap != null) {
         googleMap = null;
      }
      releaseMapFragment();
   }

   private void releaseMapFragment() {
      android.app.Fragment fragment = getActivity().getFragmentManager().findFragmentByTag(MAP_TAG);
      if (fragment == null) return;
      //
      getActivity().getFragmentManager().beginTransaction().remove(fragment).commit();
   }

   @Override
   public Observable<Boolean> getToggleObservable() {
      if (dtlToolbar == null) return Observable.empty();
      return RxDtlToolbar.diningFilterChanges(dtlToolbar).compose(RxLifecycle.bindView(this));
   }

   @OnClick(R.id.redo_merchants_button)
   public void onMechantsRedoClick() {
      getPresenter().onLoadMerchantsClick(googleMap.getCameraPosition().target);
   }

   @Override
   public void showProgress(boolean show) {
      int textResId = show ? R.string.loading : R.string.dtl_load_merchants_here_button_caption;
      int visibility = show ? View.VISIBLE : View.GONE;
      //
      Button loadMerchantsBtn = ButterKnife.<Button>findById(loadMerchantsRoot, R.id.redo_merchants_button);
      ButterKnife.findById(loadMerchantsRoot, R.id.redo_merchants_progress).setVisibility(visibility);
      //
      loadMerchantsBtn.setText(textResId);
      loadMerchantsBtn.setEnabled(!show);
   }

   @Override
   public boolean isToolbarCollapsed() {
      if (dtlToolbar == null) return true;
      return dtlToolbar.isCollapsed();
   }

   @Override
   public void addLocationMarker(LatLng location) {
      if (locationPin != null) locationPin.remove();
      locationPin = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_pin))
            .position(location));
   }

   @Override
   public void addPin(String id, LatLng latLng, MerchantType type) {
      clusterManager.addItem(new DtlClusterItem(id, latLng, type));
   }

   @Override
   public void clearMap() {
      clusterManager.clearItems();
   }

   @Override
   public void prepareInfoWindow(int height) {
      int ownHeight;
      if (dtlToolbar == null) {
         ownHeight = getHeight();
      } else {
         ownHeight = getHeight() - dtlToolbar.getBottom();
      }
      int centerY = ownHeight / 2;
      int resultY = height + getResources().getDimensionPixelSize(R.dimen.size_huge);
      int offset = resultY - centerY;
      animateTo(selectedLocation, offset);
   }

   @Override
   public void centerIn(Location location) {
      googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.asLatLng(), MapViewUtils.DEFAULT_ZOOM));
   }

   @Override
   public void renderPins() {
      clusterManager.cluster();
   }

   @Override
   public void toggleDiningFilterSwitch(boolean enabled) {
      if (dtlToolbar == null) return;
      dtlToolbar.toggleDiningFilterSwitch(enabled);
   }

   @Override
   public GoogleMap getMap() {
      return googleMap;
   }

   @Override
   public void cameraPositionChange(CameraPosition cameraPosition) {
      clusterManager.onCameraChange(cameraPosition);
      selectedLocation = cameraPosition.target;
   }

   @Override
   public void markerClick(Marker marker) {
      clusterManager.onMarkerClick(marker);
   }

   @Override
   public void showPinInfo(DtlMerchant merchant) {
      infoContainer.removeAllViews();
      PathContext newContext = PathContext.create((PathContext) getContext(), new DtlMapInfoPath(FlowUtil.currentMaster(this), merchant), Path
            .contextFactory());
      DtlMapInfoScreenImpl infoView = (DtlMapInfoScreenImpl) LayoutInflater.from(getContext())
            .cloneInContext(newContext)
            .inflate(FlowUtil.layoutFrom(DtlMapInfoPath.class), infoContainer, false);
      infoView.setInjector(injector);
      infoContainer.addView(infoView);
   }

   @Override
   public void openFilter() {
      ((FlowActivity) getActivity()).openRightDrawer();
   }

   @Override
   public void showButtonLoadMerchants(boolean show) {
      loadMerchantsRoot.setVisibility(show ? View.VISIBLE : View.GONE);
   }

   @Override
   public void zoom(float zoom) {
      googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
   }

   @Override
   public void updateToolbarLocationTitle(@Nullable DtlLocation dtlLocation) {
      if (dtlToolbar == null) return;
      dtlToolbar.setLocationCaption(DtlToolbarHelper.provideLocationCaption(getResources(), dtlLocation));
   }

   @Override
   public void updateToolbarSearchCaption(@Nullable String searchCaption) {
      if (dtlToolbar == null) return;
      dtlToolbar.setSearchCaption(searchCaption);
   }

   @Override
   public void tryHideMyLocationButton(boolean hide) {
      googleMap.setMyLocationEnabled(!hide);
   }

   @Override
   public void animateTo(LatLng coordinates, int offset) {
      Projection projection = googleMap.getProjection();
      Point screenLocation = projection.toScreenLocation(coordinates);
      screenLocation.y -= offset;
      LatLng offsetTarget = projection.fromScreenLocation(screenLocation);
      googleMap.animateCamera(CameraUpdateFactory.newLatLng(offsetTarget), CAMERA_DURATION, new GoogleMap.CancelableCallback() {
         @Override
         public void onFinish() {
            onMarkerFocused();
         }

         @Override
         public void onCancel() {
         }
      });
   }

   private void onMapLoaded() {
      clusterManager = new ClusterManager<>(getContext(), googleMap);
      clusterManager.setRenderer(new DtClusterRenderer(getContext().getApplicationContext(), googleMap, clusterManager));
      //
      clusterManager.setOnClusterItemClickListener(dtlClusterItem -> {
         selectedLocation = dtlClusterItem.getPosition();
         getPresenter().onMarkerClick(dtlClusterItem.getId());
         return true;
      });
      //
      clusterManager.setOnClusterClickListener(cluster -> {
         if (googleMap.getCameraPosition().zoom >= 17.0f) {
            getPresenter().onMarkerClick(Queryable.from(cluster.getItems()).first().getId());
         } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), googleMap.getCameraPosition().zoom + 1.0f), MapViewUtils.MAP_ANIMATION_DURATION, null);
         }
         return true;
      });
      //
      getPresenter().onMapLoaded();
   }

   private void hideInfoIfShown() {
      infoContainer.removeAllViews();
   }

   protected void onMarkerFocused() {
      EventBus.getDefault().post(new DtlShowMapInfoEvent());
   }
}
