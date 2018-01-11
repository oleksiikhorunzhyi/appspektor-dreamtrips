package com.worldventures.dreamtrips.modules.dtl_flow.parts.detail_review;

import android.content.Context;
import android.view.MenuItem;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableSdkFlaggingReviewParams;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Errors;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.FlaggingReviewAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.UrlTokenAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.NetworkUtils;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlDetailReviewPresenterImpl extends DtlPresenterImpl<DtlDetailReviewScreen, ViewState.EMPTY> implements DtlDetailReviewPresenter {

   @Inject
   PresentationInteractor presentationInteractor;
   @Inject
   MerchantsInteractor merchantInteractor;

   private final String merchantName;
   private final ReviewObject reviewObject;
   private static final String BRAND_ID = "1";

   public DtlDetailReviewPresenterImpl(Context context, Injector injector, String merchantName, ReviewObject reviewObject) {
      super(context);
      injector.inject(this);
      this.merchantName = merchantName;
      this.reviewObject = reviewObject;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
   }

   @Override
   public void onBackPressed() {
   }

   @Override
   public boolean onToolbarMenuItemClick(MenuItem item) {
      if (item.getItemId() == R.id.action_add_flag) {
         onItemClick();
      }
      return super.onToolbarMenuItemClick(item);
   }

   @Override
   public int getMenuFlag() {
      return R.menu.menu_detail_review;
   }

   @Override
   public void sendFlag() {
      FlaggingReviewAction flaggingReviewAction = FlaggingReviewAction.create(getView().getMerchantId(),
            ImmutableSdkFlaggingReviewParams.builder()
                  .authorIpAddress(getIpAddress())
                  .contentType(1)
                  .feedbackType(1)
                  .build());
      merchantInteractor.flaggingReviewHttpPipe()
            .createObservable(flaggingReviewAction)
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FlaggingReviewAction>()
               .onSuccess(flaggingReviewAction1 -> {
                  List<Errors> errors = flaggingReviewAction.getResult().errors();
                  if (errors == null || errors.isEmpty()) {
                     getView().showFlaggingSuccess();
                  } else {
                     getView().showFlaggingError();
                  }
               })
               .onFail((flaggingReviewAction1, throwable) -> getView().showFlaggingError()));
   }

   private void onMerchantsLoaded(UrlTokenAction action) {
      getView().onRefreshSuccess();
   }

   private void onMerchantsLoading(UrlTokenAction action, Integer progress) {
      getView().onRefreshProgress();
   }

   private void onMerchantsLoadingError(UrlTokenAction action, Throwable throwable) {
      getView().onRefreshError(action.toString());
   }

   @Override
   public void onItemClick() {
      sendFlag();
   }

   @Override
   public void validateComingFrom() {
      if (getView().isFromListReview()) {
         navigateToListReview("");
      } else {
         navigateToDetail("");
      }
   }

   @Override
   public void navigateToListReview(String message) {
      try {
         //TODO add merchant
         Path path = new DtlReviewsPath(FlowUtil.currentMaster(getContext()), null, message);
         History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
         historyBuilder.pop();
         historyBuilder.push(path);
         Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   @Override
   public void navigateToDetail(String message) {
      try {
         Path path = new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), null, null, message);
         History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
         historyBuilder.pop();
         historyBuilder.push(path);
         Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.BACKWARD);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public String getIpAddress() {
      return NetworkUtils.getIpAddress(true);
   }
}
