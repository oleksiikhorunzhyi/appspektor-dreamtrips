package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.ShareEventProvider;
import com.worldventures.dreamtrips.modules.dtl.analytics.TransactionRatingEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;

import javax.inject.Inject;

import icepick.State;

public class DtlThrstTransactionSucceedPresenter extends JobPresenter<DtlThrstTransactionSucceedPresenter.View> {

   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject LocationDelegate locationDelegate;
   @Inject SessionHolder<UserSession> appSessionHolder;

   @State int stars;

   private final Merchant merchant;
   private User user;

   public DtlThrstTransactionSucceedPresenter(Merchant merchant) {
      this.merchant = merchant;
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
      this.user = appSessionHolder.get().get().getUser();
      if (!ReviewStorage.exists(context, String.valueOf(user.getId()), merchant.id())) {
         view.sendToReview(merchant);
      }
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new TransactionRatingEvent(merchant.asMerchantAttributes(), stars)));
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
   }

   public void trackSharing(@ShareType String type) {
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(
                  ShareEventProvider.provideTransactionSuccessShareEvent(merchant.asMerchantAttributes(), type)));
   }

   public interface View extends ApiErrorView, RxView {
      void showShareDialog(int amount, Merchant merchant);

      void sendToReview(Merchant merchant);
   }
}
