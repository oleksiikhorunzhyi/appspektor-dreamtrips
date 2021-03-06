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
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.innahema.collections.query.queriables.Queryable;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DialogFactory;
import com.worldventures.dreamtrips.modules.dtl.view.util.MerchantTypeUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.DtlTransactionListPath;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbarHelper;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.ExpandableDtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;
import com.worldventures.dreamtrips.modules.map.model.DtlClusterItem;
import com.worldventures.dreamtrips.modules.map.renderer.ClusterRenderer;
import com.worldventures.dreamtrips.modules.map.view.MapViewUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;
import flow.Flow;
import flow.History;
import flow.path.Path;
import flow.path.PathContext;

public class DtlMapScreenImpl extends DtlLayout<DtlMapScreen, DtlMapPresenter, DtlMapPath> implements DtlMapScreen {

   private static final int CAMERA_DURATION = 1000;
   private static final float CAMERA_PADDING = 50f;

   public static final String MAP_TAG = "MAP_TAG";

   @InjectView(R.id.mapTouchView) View mapTouchView;
   @InjectView(R.id.infoContainer) FrameLayout infoContainer;
   @InjectView(R.id.noGoogleContainer) FrameLayout noGoogleContainer;
   @InjectView(R.id.redoMerchants) View redoMerchants;
   @InjectView(R.id.loadMoreMerchants) View loadMoreMerchants;

   @InjectView(R.id.btn_filter_merchant_food) View filterFood;
   @InjectView(R.id.btn_filter_merchant_entertainment) View filterEntertainment;
   @InjectView(R.id.btn_filter_merchant_spa) View filterSpa;

   @Optional @InjectView(R.id.expandableDtlToolbar) ExpandableDtlToolbar dtlToolbar;

   private ClusterManager<DtlClusterItem> clusterManager;
   private Marker locationPin;
   private GoogleMap googleMap;
   private int markerHeight;
   private SweetAlertDialog errorDialog;

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
      prepareMarkerSize();
   }

   @Override
   protected void onDetachedFromWindow() {
      hideErrorIfNeed();
      destroyMap();
      super.onDetachedFromWindow();
   }

   protected void initToolbar() {
      if (dtlToolbar == null) {
         return;
      }
      RxDtlToolbar.actionViewClicks(dtlToolbar)
            .throttleFirst(250L, TimeUnit.MILLISECONDS)
            .compose(RxLifecycleAndroid.bindView(this))
            .subscribe(aVoid -> ((FlowActivity) getActivity()).openLeftDrawer());
      RxDtlToolbar.merchantSearchApplied(dtlToolbar)
            .filter(s -> !dtlToolbar.isCollapsed())
            .compose(RxLifecycleAndroid.bindView(this))
            .subscribe(getPresenter()::applySearch);
      RxDtlToolbar.locationInputFocusChanges(dtlToolbar)
            .skip(1)
            .compose(RxLifecycleAndroid.bindView(this))
            .filter(Boolean::booleanValue) // only true -> only focus gains
            .subscribe(aBoolean -> getPresenter().locationChangeRequested(dtlToolbar.getSearchQuery()));
      RxDtlToolbar.navigationClicks(dtlToolbar)
            .throttleFirst(200L, TimeUnit.MILLISECONDS)
            .compose(RxLifecycleAndroid.bindView(this))
            .subscribe(aVoid -> getPresenter().onListClicked());
      RxDtlToolbar.filterButtonClicks(dtlToolbar)
            .compose(RxLifecycleAndroid.bindView(this))
            .subscribe(aVoid -> ((FlowActivity) getActivity()).openRightDrawer());
      dtlToolbar.setTransactionsButtonListener(getPresenter()::onTransactionsClick);
   }

   @Override
   public void setFilterButtonState(boolean isDefault) {
      if (dtlToolbar == null) {
         return;
      }
      dtlToolbar.setFilterEnabled(!isDefault);
   }

   @Override
   public void updateMerchantType(List<String> type) {
      int idResource = R.string.dtlt_search_hint;
      if (type != null && type.size() > 0) {
         if (type.size() == 1) {
            if (type.get(0).equals(FilterData.ENTERTAINMENT)) {
               filterEntertainment.setSelected(true);
               idResource = R.string.filter_merchant_entertainment;
            } else if (type.get(0).equals(FilterData.SPAS)) {
               filterSpa.setSelected(true);
               idResource = R.string.filter_merchant_spas;
            }
         } else {
            if (type.get(0).equals(FilterData.RESTAURANT) && type.get(1).equals(FilterData.BAR)) {
               filterFood.setSelected(true);
               idResource = R.string.dtlt_search_hint;
            }
         }
      }
      updateFiltersView(idResource);
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

      MapFragment mapFragment = MapFragment.newInstance();
      getActivity().getFragmentManager()
            .beginTransaction()
            .add(R.id.mapFragmentContainer, mapFragment, MAP_TAG)
            .commit();

      mapFragment.getMapAsync(map -> {
         googleMap = map;
         googleMap.clear();
         googleMap.setMyLocationEnabled(true);
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
      if (fragment == null) {
         return;
      }

      getActivity().getFragmentManager().beginTransaction().remove(fragment).commit();
   }

   @OnClick(R.id.loadMoreMerchants)
   public void onLoadMoreClick() {
      getPresenter().onLoadMoreClicked();
   }

   @OnClick(R.id.redoMerchants)
   public void onRedoMerchantsClick() {
      getPresenter().onLoadMerchantsClick(googleMap.getCameraPosition().target);
   }

   @OnClick(R.id.btn_filter_merchant_food)
   public void onFilterFoodClick() {
      if (!filterFood.isSelected()) {
         MerchantTypeUtil.toggleState(filterFood, filterEntertainment, filterSpa, FilterData.RESTAURANT);
         loadMerchantsAndAmenities(MerchantTypeUtil.getMerchantTypeList(FilterData.RESTAURANT), MerchantTypeUtil.getStringResource(FilterData.RESTAURANT));
      }
   }

   @OnClick(R.id.btn_filter_merchant_entertainment)
   public void onFilterEntertainmentClick() {
      if (!filterEntertainment.isSelected()) {
         MerchantTypeUtil.toggleState(filterFood, filterEntertainment, filterSpa, FilterData.ENTERTAINMENT);
         loadMerchantsAndAmenities(MerchantTypeUtil.getMerchantTypeList(FilterData.ENTERTAINMENT), MerchantTypeUtil.getStringResource(FilterData.ENTERTAINMENT));
      }
   }

   @OnClick(R.id.btn_filter_merchant_spa)
   public void onFilterSpaClick() {
      if (!filterSpa.isSelected()) {
         MerchantTypeUtil.toggleState(filterFood, filterEntertainment, filterSpa, FilterData.SPAS);
         loadMerchantsAndAmenities(MerchantTypeUtil.getMerchantTypeList(FilterData.SPAS), MerchantTypeUtil.getStringResource(FilterData.SPAS));
      }
   }

   @Override
   public void showProgress(boolean show) {
      if (show) {
         showBlockingProgress();
      } else {
         hideBlockingProgress();
      }
   }

   @Override
   public void addLocationMarker(LatLng location) {
      if (locationPin != null) {
         locationPin.remove();
      }
      locationPin = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_pin))
            .position(location));
   }

   @Override
   public void showItems(List<ThinMerchant> merchants) {
      clearMap();

      clusterManager.addItems(Queryable.from(merchants).map(DtlClusterItem::new).toList());
      clusterManager.cluster();
   }

   @Override
   public void clearMap() {
      clusterManager.clearItems();
      googleMap.clear();
   }

   @Override
   public void prepareInfoWindow(@Nullable LatLng location, int height) {
      if (location == null) {
         return;
      }
      int center = (dtlToolbar == null ? getHeight() / 2 : (getHeight() - dtlToolbar.getBottom()) / 2) - height;
      int offset = markerHeight - center;
      animateTo(location, offset);
   }

   @Override
   public void centerIn(LatLng location) {
      googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, MapViewUtils.DEFAULT_ZOOM));
   }

   @Override
   public void connectToggleUpdate() {
      if (dtlToolbar == null) {
         return;
      }

      RxDtlToolbar.offersOnlyToggleChanges(dtlToolbar)
            .compose(RxLifecycleAndroid.bindView(this))
            .subscribe(getPresenter()::offersOnlySwitched);
   }

   @Override
   public void toggleOffersOnly(boolean enabled) {
      if (dtlToolbar == null) {
         return;
      }
      dtlToolbar.toggleOffersOnly(enabled);
   }

   @Override
   public GoogleMap getMap() {
      return googleMap;
   }

   @Override
   public void cameraPositionChange(CameraPosition cameraPosition) {
      clusterManager.onCameraChange(cameraPosition);
   }

   @Override
   public void markerClick(Marker marker) {
      clusterManager.onMarkerClick(marker);
   }

   @Override
   public void showPinInfo(ThinMerchant merchant) {
      infoContainer.removeAllViews();
      PathContext newContext = PathContext.create((PathContext) getContext(), new DtlMapInfoPath(FlowUtil.currentMaster(this), merchant),
            Path.contextFactory());
      DtlMapInfoScreenImpl infoView = (DtlMapInfoScreenImpl) LayoutInflater.from(getContext())
            .cloneInContext(newContext)
            .inflate(FlowUtil.layoutFrom(DtlMapInfoPath.class), infoContainer, false);
      infoView.setInjector(injector);
      infoContainer.addView(infoView);
   }

   @Override
   public void showError(String error) {
      errorDialog = DialogFactory.createRetryDialog(getActivity(), error);
      errorDialog.setConfirmClickListener(listener -> {
         listener.dismissWithAnimation();
         getPresenter().retryLoadMerchant();
      });
      errorDialog.show();
   }

   private void hideErrorIfNeed() {
      if (errorDialog != null && errorDialog.isShowing()) {
         errorDialog.dismiss();
      }
   }

   @Override
   public void showButtonRedoMerchants(boolean isShow) {
      ViewUtils.setViewVisibility(redoMerchants, isShow ? View.VISIBLE : View.GONE);
   }

   @Override
   public void showLoadMoreButton(boolean isShow) {
      ViewUtils.setViewVisibility(loadMoreMerchants, (isShow ? View.VISIBLE : View.GONE));
   }

   @Override
   public void zoom(float zoom) {
      googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
   }

   @Override
   public void zoomBounds(LatLngBounds bounds) {
      getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) ViewUtils.pxFromDp(getContext(), CAMERA_PADDING)));
   }

   @Override
   public void updateToolbarLocationTitle(@Nullable DtlLocation dtlLocation) {
      if (dtlToolbar == null) {
         return;
      }
      dtlToolbar.setLocationCaption(DtlToolbarHelper.provideLocationCaption(getResources(), dtlLocation));
   }

   @Override
   public void updateToolbarSearchCaption(List<String> types, @Nullable String searchQuery) {
      if (dtlToolbar == null) {
         return;
      }
      dtlToolbar.setSearchHint(MerchantTypeUtil.getSearchHintForMerchantTypes(getContext(), types));
      dtlToolbar.setSearchQuery(searchQuery);
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
            getPresenter().onMarkerFocused();
         }

         @Override
         public void onCancel() {
         }
      });
   }

   private void prepareMarkerSize() {
      ImageView marker = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.pin_map, null);
      marker.setImageResource(R.drawable.offer_pin_icon);
      ClusterRenderer.measureIcon(marker);
      this.markerHeight = marker.getMeasuredHeight();
   }

   private void onMapLoaded() {
      clusterManager = new ClusterManager<>(getContext(), googleMap);
      clusterManager.setRenderer(new ClusterRenderer(getContext().getApplicationContext(), googleMap, clusterManager));

      clusterManager.setOnClusterItemClickListener(dtlClusterItem -> {
         getPresenter().onMarkerClicked(dtlClusterItem.getMerchant());
         return true;
      });

      clusterManager.setOnClusterClickListener(cluster -> {
         if (googleMap.getCameraPosition().zoom >= 17.0f) {
            getPresenter().onMarkerClicked(Queryable.from(cluster.getItems()).first().getMerchant());
         } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), googleMap.getCameraPosition().zoom + 1.0f), MapViewUtils.MAP_ANIMATION_DURATION, null);
         }
         return true;
      });

      getPresenter().onMapLoaded();
   }

   private void hideInfoIfShown() {
      if (infoContainer.getChildCount() > 0) {
         infoContainer.removeAllViews();
         getPresenter().onMarkerPopupDismiss();
      }
   }

   private void updateFiltersView(int stringResource) {

      ViewUtils.setCompatDrawable(filterFood, MerchantTypeUtil.filterMapDrawable(filterFood));

      ViewUtils.setCompatDrawable(filterEntertainment, MerchantTypeUtil.filterMapDrawable(filterEntertainment));

      ViewUtils.setCompatDrawable(filterSpa, MerchantTypeUtil.filterMapDrawable(filterSpa));

      ViewUtils.setTextColor((Button) filterFood, MerchantTypeUtil.filterMerchantColor(filterFood));

      ViewUtils.setTextColor((Button) filterEntertainment, MerchantTypeUtil.filterMerchantColor(filterEntertainment));

      ViewUtils.setTextColor((Button) filterSpa, MerchantTypeUtil.filterMerchantColor(filterSpa));

      if (stringResource != 0 && dtlToolbar != null) {
         dtlToolbar.setSearchHint(getContext().getResources().getString(stringResource));
      }
   }

   private void loadMerchantsAndAmenities(List<String> merchantType, int stringResource) {
      updateFiltersView(stringResource);
      getPresenter().setMerchantType(merchantType);
      getPresenter().loadAmenities(merchantType);
   }

   @Override
   public void showNoTransactionMessage() {
      SweetAlertDialog errorDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE);
      errorDialog.setTitleText(getActivity().getString(R.string.app_name));
      errorDialog.setContentText(getContext().getString(R.string.dtl_no_transaction_message));
      errorDialog.setConfirmText(getActivity().getString(R.string.apptentive_ok));
      errorDialog.showCancelButton(true);
      errorDialog.setConfirmClickListener(listener -> listener.dismissWithAnimation());
      errorDialog.show();
   }

   @Override
   public void goToTransactionPage() {
      Path path = new DtlTransactionListPath(FlowUtil.currentMaster(getContext()));
      History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
      historyBuilder.push(path);
      Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
   }
}
