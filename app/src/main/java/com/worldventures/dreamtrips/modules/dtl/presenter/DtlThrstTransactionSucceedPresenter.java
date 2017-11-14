package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.core.model.ShareType;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.ShareEventProvider;
import com.worldventures.dreamtrips.modules.dtl.analytics.TransactionRatingEvent;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstPaymentCompletedBundle;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEarnPointsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;

import javax.inject.Inject;

import icepick.State;

public class DtlThrstTransactionSucceedPresenter extends JobPresenter<DtlThrstTransactionSucceedPresenter.View> {

   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject LocationDelegate locationDelegate;
   @Inject SessionHolder appSessionHolder;
   @Inject DtlApiErrorViewAdapter apiErrorViewAdapter;

   @State int stars;

   private final Merchant merchant;
   private User user;
   private String earnedPoints;
   private String totalPoints;

   public DtlThrstTransactionSucceedPresenter(Merchant merchant, String earnPoints, String totalPoints) {
      this.merchant = merchant;
      this.earnedPoints = earnPoints;
      this.totalPoints = totalPoints;
   }

   public void share() {
      view.showShareDialog(earnedPoints, merchant);
   }

   public void init() {
      view.setTotalEarnedPoints(earnedPoints);
      view.setTotalPoints(totalPoints);
   }

   public void continueAction() {
      this.user = appSessionHolder.get().get().user();
      if (!ReviewStorage.exists(context, String.valueOf(user.getId()), merchant.id())) {
         view.sendToReview(merchant);
      }
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new TransactionRatingEvent(merchant.asMerchantAttributes(), stars)));
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
   }

   public void trackSharing(@ShareType String type) {
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(
                  ShareEventProvider.provideTransactionSuccessShareEvent(merchant.asMerchantAttributes(), type)));
   }

   public interface View extends DtlApiErrorViewAdapter.ApiErrorView, RxView {
      void showShareDialog(String amount, Merchant merchant);

      void sendToReview(Merchant merchant);

      void setTotalPoints(String points);

      void setTotalEarnedPoints(String earnedPoints);
   }
}
