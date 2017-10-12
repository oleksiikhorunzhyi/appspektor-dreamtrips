package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.StringRes;

import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.EstimatePointsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableEstimationParams;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.InformView;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.PointsEstimatorCalculateEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.util.ProxyApiErrorView;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class DtlPointsEstimationPresenter extends JobPresenter<DtlPointsEstimationPresenter.View> {

   public static final String BILL_TOTAL = "billTotal";
   private static final String NUMBER_REGEX = "[+-]?\\d*(\\.\\d+)?";

   protected final Merchant merchant;

   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject DtlApiErrorViewAdapter apiErrorViewAdapter;

   public DtlPointsEstimationPresenter(Merchant merchant) {
      this.merchant = merchant;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      apiErrorViewAdapter.setView(new ProxyApiErrorView(view, () -> view.hideProgress()));
      view.showCurrency(merchant.asMerchantAttributes().defaultCurrency());
      bindApiJob();
   }

   private void bindApiJob() {
      transactionInteractor.estimatePointsActionPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<EstimatePointsHttpAction>().onStart(action -> view.showProgress())
                  .onFail((action, exception) -> {
                     if (action.errorResponse() != null) {
                        String reason = action.errorResponse().reasonFor(BILL_TOTAL);
                        if (reason != null) view.showError(reason);
                     }
                     apiErrorViewAdapter.handleError(action, exception);
                  })
                  .onSuccess(action -> view.showEstimatedPoints(action.estimatedPoints().points().intValue())));
   }

   public void onCalculateClicked(String userInput) {
      if (!validateInput(userInput)) return;

      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new PointsEstimatorCalculateEvent(merchant.asMerchantAttributes())));

      transactionInteractor.estimatePointsActionPipe()
            .send(new EstimatePointsHttpAction(merchant.id(), ImmutableEstimationParams.builder()
                  .billTotal(Double.valueOf(userInput))
                  .checkinTime(DateTimeUtils.currentUtcString())
                  .currencyCode(merchant.asMerchantAttributes().defaultCurrency().code())
                  .build()));
   }

   protected boolean validateInput(String pointsInput) {
      if (pointsInput.isEmpty() || !pointsInput.matches(NUMBER_REGEX)) {
         view.showError(R.string.dtl_field_validation_empty_input_error);
         return false;
      }
      if (Double.valueOf(pointsInput) < 0D) {
         view.showError(R.string.dtl_points_estimation_negative_input_error);
         return false;
      }
      return true;
   }

   public interface View extends RxView, InformView {

      void showProgress();

      void hideProgress();

      void showError(@StringRes int errorRes);

      void showError(String error);

      void showEstimatedPoints(int value);

      void showCurrency(Currency currency);
   }
}
