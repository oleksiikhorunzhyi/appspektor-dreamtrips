package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.worldventures.core.model.ShareType;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.api.dtl.merchants.AddRatingHttpAction;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.InformView;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.ShareEventProvider;
import com.worldventures.dreamtrips.modules.dtl.analytics.TransactionRatingEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.TransactionSuccessEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.util.ProxyApiErrorView;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class DtlTransactionSucceedPresenter extends Presenter<DtlTransactionSucceedPresenter.View> {

   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject LocationDelegate locationDelegate;
   @Inject DtlApiErrorViewAdapter apiErrorViewAdapter;

   private final Merchant merchant;
   private User user;

   public DtlTransactionSucceedPresenter(Merchant merchant) {
      this.merchant = merchant;
   }

   public void share() {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .compose(bindViewToMainComposer())
            .subscribe(transaction -> view.showShareDialog((int) transaction.getDtlTransactionResult()
                  .getEarnedPoints(), merchant), apiErrorViewAdapter::handleError);
   }

   public void done() {
      this.user = appSessionHolder.get().get().user();
      if (!ReviewStorage.exists(context, String.valueOf(user.getId()), merchant.id())) {
         view.sendToReview(merchant);
      }
      transactionInteractor.transactionActionPipe().send(DtlTransactionAction.clean(merchant));
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new TransactionRatingEvent(merchant.asMerchantAttributes())));
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      apiErrorViewAdapter.setView(new ProxyApiErrorView(view, () -> {
      }));
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .compose(bindViewToMainComposer())
            .subscribe(transaction -> {
               view.setCongratulations(transaction.getDtlTransactionResult());
               locationDelegate.requestLocationUpdate()
                     .compose(bindViewToMainComposer())
                     .onErrorReturn(throwable -> new Location(""))
                     .subscribe(location -> {
                        analyticsInteractor.analyticsCommandPipe()
                              .send(DtlAnalyticsCommand.create(new TransactionSuccessEvent(
                                    merchant.asMerchantAttributes(), transaction, location)));
                     }, e -> {
                     });
            }, apiErrorViewAdapter::handleError);
      bindApiPipe();
   }

   private void bindApiPipe() {
      transactionInteractor.rateActionPipe()
            .observe()
            .subscribe(new ActionStateSubscriber<AddRatingHttpAction>().onFail(apiErrorViewAdapter::handleError));
   }

   public void trackSharing(@ShareType String type) {
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(
                  ShareEventProvider.provideTransactionSuccessShareEvent(merchant.asMerchantAttributes(), type)));
   }

   @Override
   public void dropView() {
      super.dropView();
      transactionInteractor.transactionActionPipe().send(DtlTransactionAction.clean(merchant));
   }

   public interface View extends InformView, RxView {
      void showShareDialog(int amount, Merchant merchant);

      void setCongratulations(DtlTransactionResult result);

      void sendToReview(Merchant merchant);
   }
}
