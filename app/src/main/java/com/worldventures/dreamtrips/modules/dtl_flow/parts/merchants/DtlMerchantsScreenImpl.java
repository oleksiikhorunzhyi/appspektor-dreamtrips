package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.messenger.util.ScrollStatePersister;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.core.selectable.SelectionManager;
import com.worldventures.dreamtrips.core.selectable.SingleSelectionManager;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.view.cell.MerchantsErrorCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.ProgressCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.adapter.ThinMerchantsAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.MerchantCellDelegate;
import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.MerchantsAdapterDelegate;
import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.ScrollingManager;
import com.worldventures.dreamtrips.modules.dtl.view.cell.pagination.PaginationManager;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DialogFactory;
import com.worldventures.dreamtrips.modules.dtl.view.util.LayoutManagerScrollPersister;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbarHelper;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.ExpandableDtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class DtlMerchantsScreenImpl extends DtlLayout<DtlMerchantsScreen, DtlMerchantsPresenter, DtlMerchantsPath>
      implements DtlMerchantsScreen, MerchantCellDelegate {

   @Optional @InjectView(R.id.expandableDtlToolbar) ExpandableDtlToolbar dtlToolbar;
   @InjectView(R.id.lv_items) EmptyRecyclerView recyclerView;
   @InjectView(R.id.swipe_container) SwipeRefreshLayout refreshLayout;
   @InjectView(R.id.emptyView) View emptyView;
   @InjectView(R.id.errorView) View errorView;

   @Inject MerchantsAdapterDelegate delegate;

   ScrollingManager scrollingManager;
   SelectionManager selectionManager;
   SweetAlertDialog errorDialog;
   PaginationManager paginationManager;
   LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
   LayoutManagerScrollPersister scrollStatePersister = new LayoutManagerScrollPersister();

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      recyclerView.setLayoutManager(layoutManager);
   }

   @Override
   public Parcelable onSaveInstanceState() {
      return scrollStatePersister.saveScrollState(super.onSaveInstanceState(), layoutManager);
   }

   @Override
   protected void onPostAttachToWindowView() {
      super.onPostAttachToWindowView();
      initDtlToolbar();

      ThinMerchantsAdapter adapter = new ThinMerchantsAdapter(getActivity(), injector);
      delegate.setup(adapter);
      delegate.registerDelegate(ImmutableThinMerchant.class, this);
      delegate.registerDelegate(MerchantsErrorCell.Model.class, model -> onRetryClick());

      paginationManager = new PaginationManager();
      paginationManager.setup(recyclerView);
      paginationManager.setPaginationListener(() -> getPresenter().loadNext());

      scrollingManager = new ScrollingManager();
      scrollingManager.setup(recyclerView);

      selectionManager = new SingleSelectionManager(recyclerView);
      selectionManager.setEnabled(isTabletLandscape());

      recyclerView.setAdapter(adapter);

      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
      refreshLayout.setOnRefreshListener(() -> getPresenter().refresh());
      refreshLayout.setEnabled(true);
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
      if (dtlToolbar != null) dtlToolbar.setFilterEnabled(!isDefault);
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
   public boolean isToolbarCollapsed() {
      return dtlToolbar == null || dtlToolbar.isCollapsed();
   }

   @Override
   public void setRefreshedItems(List<ThinMerchant> merchants) {
      delegate.setItems(merchants);
      scrollStatePersister.restoreInstanceStateIfNeeded(getLastRestoredInstanceState(), layoutManager);
   }

   @Override
   public void refreshProgress(boolean isShow) {
      refreshLayout.setRefreshing(isShow);
   }

   @Override
   public void loadNextProgress(boolean isLoading) {
      if (isLoading) delegate.addItem(ProgressCell.INSTANCE);
      else delegate.removeItem(ProgressCell.INSTANCE);
   }

   @Override
   public void showEmpty(boolean isShow) {
      emptyView.setVisibility(isShow ? VISIBLE : GONE);
   }

   @Override
   public void updateLoadingState(boolean isLoading) {
      paginationManager.updateLoadingStatus(isLoading);
   }

   @Override
   public void refreshMerchantsError(boolean isShow) {
      errorView.setVisibility(isShow ? VISIBLE : GONE);
   }

   @Override
   public void clearMerchants() {
      delegate.clear();
   }

   @Override
   public void loadNextMerchantsError(boolean show) {
      if(show) delegate.addItem(MerchantsErrorCell.INSTANCE);
      else delegate.removeItem(MerchantsErrorCell.INSTANCE);
   }

   @OnClick(R.id.retry)
   protected void onRetryClick() {
      getPresenter().onRetryMerchantsClick();
   }

   @Override
   public void onToggleExpanded(boolean expanded, ImmutableThinMerchant item) {
      delegate.toggleItem(expanded, item);
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
   public void toggleOffersOnly(boolean enabled) {
      if (dtlToolbar == null) return;
      dtlToolbar.toggleOffersOnly(enabled);
   }

   @Override
   public void toggleSelection(ThinMerchant merchant) {
      int index = delegate.getItems().indexOf(merchant);
      if (index != -1) selectionManager.toggleSelection(index);
      scrollingManager.scrollToPosition(index);
   }

   @Override
   protected void onDetachedFromWindow() {
      selectionManager.release();
      super.onDetachedFromWindow();
   }

   @Override
   public void applyViewState(DtlMerchantsState state) {
      if (state == null) return;
      delegate.setExpandedMerchants(state.getExpandedMerchantIds());
   }

   @Override
   public DtlMerchantsState provideViewState() {
      return new DtlMerchantsState(delegate.getExpandedMerchants(), recyclerView.getLayoutManager().onSaveInstanceState());
   }

   @Override
   public void showError(String error) {
      errorDialog = DialogFactory.createRetryDialog(getActivity(), error);
      errorDialog.setConfirmClickListener(listener -> {
         listener.dismissWithAnimation();
         getPresenter().onRetryMerchantClick();
      });
      errorDialog.show();
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
