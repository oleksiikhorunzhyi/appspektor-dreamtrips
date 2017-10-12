package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.google.android.gms.common.api.Status;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.jakewharton.rxbinding.view.RxView;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlLocation;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationSearchCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationSearchHeaderCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlNearbyHeaderCell;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbarHelper;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import timber.log.Timber;

public class MasterToolbarScreenImpl extends DtlLayout<MasterToolbarScreen, MasterToolbarPresenter, MasterToolbarPath>
      implements MasterToolbarScreen, ActivityResultDelegate.ActivityResultListener {

   @Inject ActivityResultDelegate activityResultDelegate;

   @InjectView(R.id.dtlToolbar) DtlToolbar toolbar;

   private View searchContentView, autoDetectNearMe, progress;
   private RecyclerView recyclerView;
   private BaseDelegateAdapter adapter;
   private PopupWindow popupWindow;

   @Override
   protected void onPostAttachToWindowView() {
      super.onPostAttachToWindowView();
      injector.inject(this);
      initDtlToolbar();

      prepareViews();
      setupPopup();
      setupRecyclerView();

      activityResultDelegate.addListener(this);
   }

   @Override
   protected void onDetachedFromWindow() {
      activityResultDelegate.removeListener(this);
      onPopupVisibilityChange(false);
      super.onDetachedFromWindow();
   }

   protected void initDtlToolbar() {
      RxDtlToolbar.merchantSearchApplied(toolbar)
            .compose(RxLifecycleAndroid.bindView(this))
            .subscribe(getPresenter()::applySearch);
      RxDtlToolbar.filterButtonClicks(toolbar)
            .compose(RxLifecycleAndroid.bindView(this))
            .subscribe(aVoid -> ((FlowActivity) getActivity()).openRightDrawer());
      RxDtlToolbar.offersOnlyToggleChanges(toolbar)
            .compose(RxLifecycleAndroid.bindView(this))
            .subscribe(getPresenter()::offersOnlySwitched);
   }

   @Override
   public void connectToggleUpdate() {
      RxDtlToolbar.offersOnlyToggleChanges(toolbar)
            .compose(RxLifecycleAndroid.bindView(this))
            .subscribe(aBoolean -> getPresenter().offersOnlySwitched(aBoolean));
   }

   @Override
   public void updateToolbarLocationTitle(@Nullable DtlLocation dtlLocation) {
      toolbar.setLocationCaption(DtlToolbarHelper.provideLocationCaption(getResources(), dtlLocation));
   }

   @Override
   public void updateToolbarSearchCaption(@Nullable String searchCaption) {
      toolbar.setSearchCaption(searchCaption);
   }

   @Override
   public void toggleOffersOnly(boolean enabled) {
      toolbar.toggleOffersOnly(enabled);
   }

   @Override
   public void setFilterButtonState(boolean isDefault) {
      toolbar.setFilterEnabled(!isDefault);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Private screen methods
   ///////////////////////////////////////////////////////////////////////////

   private void prepareViews() {
      searchContentView = LayoutInflater.from(getContext()).inflate(R.layout.view_dtl_location_search, null);
      searchContentView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

      popupWindow = new PopupWindow(searchContentView, searchContentView.getMeasuredWidth(), WindowManager.LayoutParams.WRAP_CONTENT);
      popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
      popupWindow.setBackgroundDrawable(new ColorDrawable());
      popupWindow.setOutsideTouchable(true);
      popupWindow.setOnDismissListener(() -> SoftInputUtil.hideSoftInputMethod(this));

      this.progress = ButterKnife.findById(searchContentView, R.id.progress);
      this.recyclerView = ButterKnife.<RecyclerView>findById(searchContentView, R.id.locationsList);
      this.autoDetectNearMe = ButterKnife.findById(searchContentView, R.id.autoDetectNearMe);

      RxView.clicks(autoDetectNearMe)
            .compose(RxLifecycleAndroid.bindView(this))
            .throttleFirst(3L, TimeUnit.SECONDS)
            .subscribe(aVoid -> onNearMeClicked());
   }

   protected void setupPopup() {
      Observable<Boolean> focus = RxView.focusChanges(toolbar.getLocationSearchInput());
      Observable<Boolean> clicks = RxView.clicks(toolbar.getLocationSearchInput())
            .flatMap(aVoid -> Observable.just(Boolean.TRUE));

      Observable.merge(clicks, focus).compose(RxLifecycleAndroid.bindView(this)).subscribe(this::onPopupVisibilityChange);
   }

   protected void onPopupVisibilityChange(boolean visible) {
      if (visible) {
         adapter.clear();
         popupWindow.showAsDropDown(toolbar.getLocationSearchInput());
         getPresenter().onShowToolbar();
         autoDetectNearMe.setVisibility(getPresenter().needShowAutodetectButton() ? VISIBLE : GONE);
      } else {
         popupWindow.dismiss();
      }
   }

   private void setupRecyclerView() {
      adapter = new BaseDelegateAdapter<DtlLocation>(getActivity(), injector);
      adapter.registerCell(ImmutableDtlLocation.class, DtlLocationSearchCell.class);
      adapter.registerCell(DtlLocationSearchHeaderCell.HeaderModel.class, DtlLocationSearchHeaderCell.class);
      adapter.registerCell(DtlNearbyHeaderCell.NearbyHeaderModel.class, DtlNearbyHeaderCell.class);

      adapter.registerDelegate(ImmutableDtlLocation.class, location -> onLocationClicked((DtlLocation) location));

      recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      recyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.dtl_location_change_list_divider), false));

      recyclerView.setAdapter(adapter);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Overridden and screen implementation methods
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public Observable<String> provideLocationSearchObservable() {
      return RxDtlToolbar.locationSearchTextChanges(toolbar).skip(1);
   }

   private List<Object> prepareHeader(boolean showNearmyHeader) {
      List<Object> locations = new CopyOnWriteArrayList<>();
      if (showNearmyHeader) {
         locations.add(DtlNearbyHeaderCell.NearbyHeaderModel.INSTANCE);
      }
      return locations;
   }

   @Override
   public void toggleSearchPopupVisibility(boolean show) {
      onPopupVisibilityChange(show);
   }

   @Override
   public boolean isSearchPopupShowing() {
      return popupWindow.isShowing();
   }

   @Override
   public void showProgress() {
      ViewUtils.setViewVisibility(VISIBLE, progress);
      ViewUtils.setViewVisibility(GONE, recyclerView);
   }

   @Override
   public void hideProgress() {
      ViewUtils.setViewVisibility(View.GONE, progress);
      ViewUtils.setViewVisibility(View.VISIBLE, recyclerView);
   }

   public void onLocationClicked(DtlLocation location) {
      hideSoftInput();
      getPresenter().locationSelected(location);
   }

   private void onNearMeClicked() {
      hideSoftInput();
      getPresenter().loadNearMeRequested();
   }

   @Override
   public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == DtlActivity.GPS_LOCATION_RESOLUTION_REQUEST) {
         switch (resultCode) {
            case Activity.RESULT_OK:
               // All required changes were successfully made
               getPresenter().onLocationResolutionGranted();
               break;
            case Activity.RESULT_CANCELED:
               // The user was asked to change settings, but chose not to
               getPresenter().onLocationResolutionDenied();
               break;
            default:
               break;
         }
         return true;
      }
      return false;
   }



   @Override
   public void locationResolutionRequired(Status status) {
      try {
         status.startResolutionForResult(getActivity(), DtlActivity.GPS_LOCATION_RESOLUTION_REQUEST);
      } catch (IntentSender.SendIntentException th) {
         Timber.e(th, "Error opening settings activity.");
      }
   }

   @Override
   public void setItems(List<DtlLocation> items, boolean showNearbyHeader) {
      hideProgress();

      List<Object> locations = prepareHeader(showNearbyHeader);
      locations.addAll(items);
      adapter.clearAndUpdateItems(locations);
      autoDetectNearMe.setVisibility(getPresenter().needShowAutodetectButton() ? VISIBLE : GONE);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Boilerplate stuff
   ///////////////////////////////////////////////////////////////////////////

   public MasterToolbarScreenImpl(Context context) {
      super(context);
   }

   public MasterToolbarScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public MasterToolbarPresenter createPresenter() {
      return new MasterToolbarPresenterImpl(getContext(), injector);
   }
}
