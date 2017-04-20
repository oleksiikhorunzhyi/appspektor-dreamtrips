package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.view.cell.MerchantsErrorCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.ProgressCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.adapter.ThinMerchantsAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.MerchantCellDelegate;
import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.MerchantsAdapterDelegate;
import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.ScrollingManager;
import com.worldventures.dreamtrips.modules.dtl.view.cell.pagination.PaginationManager;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DialogFactory;
import com.worldventures.dreamtrips.modules.dtl.view.util.ClearableSelectionManager;
import com.worldventures.dreamtrips.modules.dtl.view.util.LayoutManagerScrollPersister;
import com.worldventures.dreamtrips.modules.dtl.view.util.MerchantTypeUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbarHelper;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.ExpandableDtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;

import java.util.ArrayList;
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
   @InjectView(R.id.captionNoMerchants) TextView noMerchantsCaption;

   @InjectView(R.id.btn_filter_merchant_food) View filterFood;
   @InjectView(R.id.btn_filter_merchant_entertainment) View filterEntertainment;
   @InjectView(R.id.btn_filter_merchant_spa) View filterSpa;

   @InjectView(R.id.id_view_food) View backgroundFood;
   @InjectView(R.id.id_view_entertainment) View backgroundEntertainment;
   @InjectView(R.id.id_view_spas) View backgroundSpa;

   @Inject MerchantsAdapterDelegate delegate;

   ScrollingManager scrollingManager;
   ClearableSelectionManager selectionManager;
   SweetAlertDialog errorDialog;
   PaginationManager paginationManager;
   LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
   LayoutManagerScrollPersister scrollStatePersister = new LayoutManagerScrollPersister();

   private int idResource = R.string.dtlt_search_hint;
   public static List<String> merchantType = new ArrayList<>();

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

      selectionManager = new ClearableSelectionManager(recyclerView);
      selectionManager.setEnabled(isTabletLandscape());

      recyclerView.setAdapter(selectionManager.provideWrappedAdapter(adapter));

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
   }

   @Override
   public void onRefreshSuccess() {
      this.refreshProgress(false);
      this.hideRefreshMerchantsError();
      this.showEmpty(false);
      this.updateLoadingState(false);
   }

   @Override
   public void onRefreshProgress() {
      this.refreshProgress(true);
      this.hideRefreshMerchantsError();
      this.showEmpty(false);
      this.loadNextMerchantsError(false);
      this.updateLoadingState(true);
   }

   @Override
   public void onRefreshError(String error) {
      this.loadNextMerchantsError(false);
      this.refreshProgress(false);
      this.showhMerchantsError();
      this.showEmpty(false);
      this.updateLoadingState(false);
   }

   @Override
   public void onLoadNextSuccess() {
      this.loadNextProgress(false);
      this.loadNextMerchantsError(false);
      this.showEmpty(false);
      this.updateLoadingState(false);
   }

   @Override
   public void onLoadNextProgress() {
      this.loadNextProgress(true);
      this.loadNextMerchantsError(false);
      this.showEmpty(false);
      this.updateLoadingState(true);
   }

   @Override
   public void onLoadNextError() {
      this.loadNextProgress(false);
      this.showhMerchantsError();
      this.showEmpty(false);
      this.updateLoadingState(true);
   }

   @OnClick(R.id.btn_filter_merchant_food)
   @Override
   public void onClickFood() {
      if (!filterFood.isSelected()) {
         MerchantTypeUtil.toggleState(filterFood, filterEntertainment, filterSpa, FilterData.RESTAURANT);
         loadMerchantsAndAmenities(MerchantTypeUtil.getMerchantTypeList(FilterData.RESTAURANT), MerchantTypeUtil.getStringResource(FilterData.RESTAURANT));

         setStatusMerchantType(FilterData.RESTAURANT);
      }
   }

   @OnClick(R.id.btn_filter_merchant_entertainment)
   @Override
   public void onClickEntertainment() {
      if (!filterEntertainment.isSelected()) {
         MerchantTypeUtil.toggleState(filterFood, filterEntertainment, filterSpa, FilterData.ENTERTAINMENT);
         loadMerchantsAndAmenities(MerchantTypeUtil.getMerchantTypeList(FilterData.ENTERTAINMENT), MerchantTypeUtil.getStringResource(FilterData.ENTERTAINMENT));

         setStatusMerchantType(FilterData.ENTERTAINMENT);
      }
   }

   @OnClick(R.id.btn_filter_merchant_spa)
   @Override
   public void onClickSpa() {
      if (!filterSpa.isSelected()) {
         MerchantTypeUtil.toggleState(filterFood, filterEntertainment, filterSpa, FilterData.SPAS);
         loadMerchantsAndAmenities(MerchantTypeUtil.getMerchantTypeList(FilterData.SPAS), MerchantTypeUtil.getStringResource(FilterData.SPAS));

         setStatusMerchantType(FilterData.SPAS);
      }
   }

   public static List<String> getFilterType(){
      return merchantType;
   }

   private static void setStatusMerchantType(String type){
      merchantType.clear();
      merchantType = MerchantTypeUtil.getMerchantTypeList(type);
   }

    @Override
    public void updateMerchantType(List<String> type) {

       if (type != null ) {
          if (type.size() > 1) {
             if (type.get(0).equals(FilterData.RESTAURANT) && type.get(1).equals(FilterData.BAR)) {
                filterFood.setSelected(true);
                idResource = R.string.dtlt_search_hint;
             }
          } else {
             if (type.get(0).equals(FilterData.ENTERTAINMENT)) {
                filterEntertainment.setSelected(true);
                idResource = R.string.filter_merchant_entertainment;
             } else if (type.get(0).equals(FilterData.SPAS)) {
                filterSpa.setSelected(true);
                idResource = R.string.filter_merchant_spa;
             }
          }
       }
       updateFiltersView(idResource);
    }

   @Override
   public int getMerchantType() {
      return idResource;
   }

   private void showhMerchantsError() {
      if (!delegate.isItemsPresent()) errorView.setVisibility(VISIBLE);
      else loadNextMerchantsError(true);
   }

   private void hideRefreshMerchantsError() {
      errorView.setVisibility(GONE);
   }

   private void updateLoadingState(boolean isLoading) {
      paginationManager.updateLoadingStatus(isLoading);
   }

   private void loadNextMerchantsError(boolean show) {
      if(show) delegate.addItem(MerchantsErrorCell.INSTANCE);
      else delegate.removeItem(MerchantsErrorCell.INSTANCE);
   }

   private void refreshProgress(boolean isShow) {
      refreshLayout.setRefreshing(isShow);
   }

   private void loadNextProgress(boolean isLoading) {
      if (isLoading) delegate.addItem(ProgressCell.INSTANCE);
      else delegate.removeItem(ProgressCell.INSTANCE);
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
   public void setRefreshedItems(List<ThinMerchant> merchants) {
      delegate.setItems(merchants);
      scrollStatePersister.restoreInstanceStateIfNeeded(getLastRestoredInstanceState(), layoutManager);
   }

   @Override
   public void connectToggleUpdate() {
      if(dtlToolbar == null) return;

      RxDtlToolbar.offersOnlyToggleChanges(dtlToolbar)
            .compose(RxLifecycle.bindView(this))
            .subscribe(aBoolean -> getPresenter().offersOnlySwitched(aBoolean));
   }

   @Override
   public void showEmpty(boolean isShow) {
      emptyView.setVisibility(isShow ? VISIBLE : GONE);
   }

   @Override
   public void showNoMerchantsCaption(boolean isFilterDefault, boolean isOffersOnly) {
      @StringRes int captionId;
      if (!isFilterDefault) {
         captionId = R.string.merchants_no_results;
      } else {
         captionId =
               isOffersOnly ? R.string.merchants_no_results_offers_only : R.string.dtl_location_no_merchants_caption;
      }
      noMerchantsCaption.setText(captionId);
   }

   @Override
   public void clearMerchants() {
      delegate.clear();
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
   public void sendToRatingReview(ThinMerchant merchant) {
      getPresenter().sendToRatingReview(merchant);
   }

   @Override
   public void userHasPendingReview() {
      errorDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE);
      errorDialog.setTitleText(getActivity().getString(R.string.app_name));
      errorDialog.setContentText(getContext().getString(R.string.text_awaiting_approval_review));
      errorDialog.setConfirmText(getActivity().getString(R.string.apptentive_ok));
      errorDialog.showCancelButton(true);
      errorDialog.setConfirmClickListener(listener -> listener.dismissWithAnimation());
      errorDialog.show();
   }

   @Override
   public void toggleSelection(ThinMerchant merchant) {
      int index = delegate.getItems().indexOf(merchant);
      if (index != -1) selectionManager.toggleSelection(index);
      scrollingManager.scrollToPosition(index);
   }

   @Override
   public void clearSelection() {
      selectionManager.clearSelections();
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
   public void showLoadMerchantError(String error) {
      errorDialog = DialogFactory.createRetryDialog(getActivity(), error);
      errorDialog.setConfirmClickListener(listener -> {
         listener.dismissWithAnimation();
         getPresenter().onRetryMerchantClick();
      });
      errorDialog.setOnDismissListener(dialog -> getPresenter().onRetryDialogDismiss());
      errorDialog.show();
   }

   @Override
   protected void onDetachedFromWindow() {
      selectionManager.release();
      hideErrorIfNeed();
      super.onDetachedFromWindow();
   }

   private void hideErrorIfNeed() {
      if (errorDialog != null && errorDialog.isShowing()) errorDialog.dismiss();
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

   private void updateFiltersView(int stringResource) {

      ViewUtils.setCompatDrawable(backgroundFood, MerchantTypeUtil.filterMerchantDrawable(filterFood));

      ViewUtils.setCompatDrawable(backgroundEntertainment, MerchantTypeUtil.filterMerchantDrawable(filterEntertainment));

      ViewUtils.setCompatDrawable(backgroundSpa, MerchantTypeUtil.filterMerchantDrawable(filterSpa));

      if (stringResource != 0 && dtlToolbar != null) {
         dtlToolbar.setSearchCaption(getContext().getResources().getString(stringResource));
      }
   }

   public void loadMerchantsAndAmenities(List<String> merchantType , int stringResource) {
      getPresenter().onLoadMerchantsType(merchantType);
      updateFiltersView(stringResource);
      getPresenter().loadAmenities(merchantType);
   }
}
