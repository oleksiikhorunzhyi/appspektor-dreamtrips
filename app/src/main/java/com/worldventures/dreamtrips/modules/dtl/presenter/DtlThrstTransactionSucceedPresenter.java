package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.core.model.ShareType;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.ShareEventProvider;
import com.worldventures.dreamtrips.modules.dtl.analytics.TransactionRatingEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;

import javax.inject.Inject;

public class DtlThrstTransactionSucceedPresenter extends Presenter<DtlThrstTransactionSucceedPresenter.View> {

   @Inject DtlTransactionInteractor dtlTransactionInteractor;

   private final Merchant merchant;
   private String earnedPoints;
   private String totalPoints;

   public DtlThrstTransactionSucceedPresenter(Merchant merchant, String earnPoints, String totalPoints) {
      this.merchant = merchant;
      this.earnedPoints = earnPoints;
      this.totalPoints = totalPoints;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      view.setTotalEarnedPoints(earnedPoints);
      view.setTotalPoints(totalPoints);
   }

   public void share() {
      view.showShareDialog(earnedPoints, merchant);
   }

   public void continueAction() {
      User user = appSessionHolder.get().get().user();
      if (!ReviewStorage.exists(context, String.valueOf(user.getId()), merchant.id())) {
         view.sendToReview(merchant);
      }
      dtlTransactionInteractor.transactionActionPipe().send(DtlTransactionAction.clean(merchant));
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new TransactionRatingEvent(merchant.asMerchantAttributes())));
   }

   public void trackSharing(@ShareType String type) {
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(
                  ShareEventProvider.provideTransactionSuccessShareEvent(merchant.asMerchantAttributes(), type)));
   }

   @Override
   public void dropView() {
      super.dropView();
      dtlTransactionInteractor.transactionActionPipe().send(DtlTransactionAction.clean(merchant));
   }

   public interface View extends Presenter.View  {
      void showShareDialog(String amount, Merchant merchant);

      void sendToReview(Merchant merchant);

      void setTotalPoints(String points);

      void setTotalEarnedPoints(String earnedPoints);
   }
}
