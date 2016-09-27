package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.worldventures.dreamtrips.api.dtl.merchants.RatingHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableRatingParams;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.ShareEventProvider;
import com.worldventures.dreamtrips.modules.dtl.analytics.TransactionRatingEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.TransactionSuccessEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlTransactionSucceedPresenter extends JobPresenter<DtlTransactionSucceedPresenter.View> {

   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject LocationDelegate locationDelegate;
   //
   @State int stars;
   //
   private final Merchant merchant;

   public DtlTransactionSucceedPresenter(Merchant merchant) {
      this.merchant = merchant;
   }

   public void rate(int stars) {
      this.stars = stars;
   }

   public void share() {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(transaction -> view.showShareDialog((int) transaction.getDtlTransactionResult()
                  .getEarnedPoints(), merchant), apiErrorPresenter::handleError);
   }

   public void done() {
      if (stars == 0) return;
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .flatMap(transaction -> transactionInteractor.rateActionPipe()
                  .createObservableResult(new RatingHttpAction(merchant.id(), ImmutableRatingParams.builder()
                        .rating(stars)
                        .transactionId(transaction.getDtlTransactionResult().getId())
                        .build())))
            .compose(bindViewIoToMainComposer())
            .subscribe(action -> {
            }, apiErrorPresenter::handleError);
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new TransactionRatingEvent(merchant.asMerchantAttributes(), stars)));
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      apiErrorPresenter.setView(view);
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(transaction -> {
               view.setCongratulations(transaction.getDtlTransactionResult());
               locationDelegate.requestLocationUpdate()
                     .compose(bindViewIoToMainComposer())
                     .onErrorReturn(throwable -> new Location(""))
                     .subscribe(location -> {
                        analyticsInteractor.dtlAnalyticsCommandPipe()
                              .send(DtlAnalyticsCommand.create(new TransactionSuccessEvent(merchant.asMerchantAttributes(), transaction, location)));
                     }, e -> {});
            }, apiErrorPresenter::handleError);
      bindApiPipe();
   }

   private void bindApiPipe() {
      transactionInteractor.rateActionPipe()
            .observe()
            .subscribe(new ActionStateSubscriber<RatingHttpAction>().onFail(apiErrorPresenter::handleActionError));
   }

   public void trackSharing(@ShareType String type) {
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(ShareEventProvider.provideTransactionSuccessShareEvent(merchant.asMerchantAttributes(), type)));
   }

   public interface View extends ApiErrorView, RxView {
      void showShareDialog(int amount, Merchant merchant);

      void setCongratulations(DtlTransactionResult result);
   }
}
