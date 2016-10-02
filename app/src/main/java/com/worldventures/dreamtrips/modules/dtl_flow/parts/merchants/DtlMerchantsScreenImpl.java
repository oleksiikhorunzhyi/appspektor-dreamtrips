package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.techery.spares.adapter.expandable.ExpandableLayoutManager;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.core.selectable.SelectionManager;
import com.worldventures.dreamtrips.core.selectable.SingleSelectionManager;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantExpandableCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.adapter.ThinMerchantsAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.MerchantCellDelegate;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DialogFactory;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbarHelper;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.ExpandableDtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;
import com.worldventures.dreamtrips.modules.feed.view.custom.StateRecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class DtlMerchantsScreenImpl extends DtlLayout<DtlMerchantsScreen, DtlMerchantsPresenter, DtlMerchantsPath>
      implements DtlMerchantsScreen, MerchantCellDelegate {

   @Optional @InjectView(R.id.expandableDtlToolbar) ExpandableDtlToolbar dtlToolbar;
   @InjectView(R.id.lv_items) EmptyRecyclerView recyclerView;
   @InjectView(R.id.swipe_container) SwipeRefreshLayout refreshLayout;
   @InjectView(R.id.emptyView) View emptyView;

   ThinMerchantsAdapter adapter;
   SelectionManager selectionManager;
   SweetAlertDialog errorDialog;

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      recyclerView.setLayoutManager(new ExpandableLayoutManager(getActivity()));
   }

   @Override
   protected void onPostAttachToWindowView() {
      super.onPostAttachToWindowView();
      initDtlToolbar();

      adapter = new ThinMerchantsAdapter(getActivity(), injector);
      adapter.registerCell(ImmutableThinMerchant.class, DtlMerchantExpandableCell.class);
      adapter.registerDelegate(ImmutableThinMerchant.class, this);

      selectionManager = new SingleSelectionManager(recyclerView);
      selectionManager.setEnabled(isTabletLandscape());

      recyclerView.setAdapter(adapter);
      recyclerView.setEmptyView(emptyView);

      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
      refreshLayout.setEnabled(false);
   }

   private void initDtlToolbar() {
      if (dtlToolbar == null) return;
      RxDtlToolbar.actionViewClicks(dtlToolbar)
            .throttleFirst(250L, TimeUnit.MILLISECONDS)
            .compose(RxLifecycle.bindView(this))
            .subscribe(aVoid -> ((FlowActivity) getActivity()).openLeftDrawer());
      RxDtlToolbar.navigationClicks(dtlToolbar)
            .throttleFirst(250L, TimeUnit.MILLISECONDS)
            .compose(RxLifecycle.bindView(this))
            .subscribe(aVoid -> getPresenter().mapClicked());
      RxDtlToolbar.merchantSearchApplied(dtlToolbar)
            .filter(s -> !dtlToolbar.isCollapsed())
            .compose(RxLifecycle.bindView(this))
            .subscribe(getPresenter()::applySearch);
      RxDtlToolbar.locationInputFocusChanges(dtlToolbar)
            .skip(1)
            .compose(RxLifecycle.bindView(this))
            .filter(Boolean::booleanValue) // only true -> only focus gains
            .subscribe(aBoolean -> getPresenter().locationChangeRequested());
      RxDtlToolbar.filterButtonClicks(dtlToolbar)
            .compose(RxLifecycle.bindView(this))
            .subscribe(aVoid -> ((FlowActivity) getActivity()).openRightDrawer());
      RxDtlToolbar.offersOnlyToggleChanges(dtlToolbar)
            .compose(RxLifecycle.bindView(this))
            .subscribe(aBoolean -> getPresenter().offersOnlySwitched(aBoolean));
   }

   @Override
   public void setFilterButtonState(boolean isDefault) {
      if (dtlToolbar == null) return;
      dtlToolbar.setFilterEnabled(!isDefault);
   }

   @Override
   public void showEmptyMerchantView(boolean show) {
      adapter.setItems(Collections.emptyList());
   }

   @Override
   public void setExpandedOffers(List<String> expandedOffers) {
      adapter.setExpandedMerchantIds(expandedOffers);
   }

   @Override
   public List<String> getExpandedOffers() {
      return adapter.getExpandedMerchantIds();
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
   public void onToggleExpanded(boolean expanded, ImmutableThinMerchant item) {
      adapter.toogle(expanded, item);
      getPresenter().onToggleExpand(expanded, item);
   }

   @Override
   public void onCellClicked(ImmutableThinMerchant merchant) {
      getPresenter().merchantClicked(merchant);
   }

   @Override
   public void onOfferClick(ThinMerchant merchant, Offer offer) {
      getPresenter().onOfferClick(merchant, offer);
   }

   @Override
   public void setItems(List<ThinMerchant> merchants) {
      hideProgress();
      adapter.setItems(merchants);
   }

   @Override
   public void showProgress() {
      refreshLayout.setRefreshing(true);
      emptyView.setVisibility(GONE);
   }

   @Override
   public void hideProgress() {
      refreshLayout.setRefreshing(false);
      emptyView.setVisibility(VISIBLE);
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

   @Override
   public void toggleOffersOnly(boolean enabled) {
      if (dtlToolbar == null) return;
      dtlToolbar.toggleOffersOnly(enabled);
   }

   @Override
   public void toggleSelection(ThinMerchant merchant) {
      int index = adapter.getItems().indexOf(merchant);
      if (index != -1) selectionManager.toggleSelection(index);
   }

   @Override
   public boolean isToolbarCollapsed() {
      return dtlToolbar == null || dtlToolbar.isCollapsed();
   }

   @Override
   protected void onDetachedFromWindow() {
      selectionManager.release();
      recyclerView.setAdapter(null);
      super.onDetachedFromWindow();
   }

   @Override
   public void onApiCallFailed() {
      super.onApiCallFailed();
      hideProgress();
   }

   @Override
   public boolean onApiError(ErrorResponse errorResponse) {
      SweetAlertDialog alertDialog = DialogFactory.createErrorDialog(getActivity(), errorResponse.getFirstMessage());
      alertDialog.setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
      alertDialog.show();
      return true;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Boilerplate stuff
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public DtlMerchantsPresenter createPresenter() {
      return new DtlMerchantsPresenterImpl(getContext(), injector);
   }

   public DtlMerchantsScreenImpl(Context context) {
      super(context);
   }

   public DtlMerchantsScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }
}
