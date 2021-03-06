package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.service.DialogNavigatorInteractor;
import com.worldventures.dreamtrips.core.navigation.service.command.CloseDialogCommand;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstPaymentCompletedBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstTransactionSucceedPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.review.DtlReviewPath;
import com.worldventures.dreamtrips.social.ui.share.bundle.ShareBundle;
import com.worldventures.dreamtrips.social.ui.share.view.ShareFragment;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;

@Layout(R.layout.fragment_transaction_succeed)
public class DtlThrstTransactionSucceedFragment extends RxBaseFragmentWithArgs<DtlThrstTransactionSucceedPresenter, ThrstPaymentCompletedBundle> implements DtlThrstTransactionSucceedPresenter.View {

   @InjectView(R.id.earned) TextView earned;
   @InjectView(R.id.total) TextView total;
   @InjectView(R.id.done) TextView done;
   @Inject DialogNavigatorInteractor dialogNavigatorInteractor;

   @Override
   public void onResume() {
      super.onResume();
      if (isTabletLandscape()) {
         done.setText(R.string.done_pilot_button);
      } else {
         done.setText(R.string.continue_option);
      }
   }

   @Override
   public void sendToReview(Merchant merchant) {
      Flow.get(getContext()).set(new DtlReviewPath(merchant, true));
   }

   @Override
   public void setTotalPoints(String points) {
      total.setText(points);
   }

   @Override
   public void setTotalEarnedPoints(String earnedPoints) {
      earned.setText(String.format("+%spt", earnedPoints));
   }

   @OnClick(R.id.share)
   void onShareClicked() {
      getPresenter().share();
   }

   @OnClick(R.id.done)
   void onDoneClicked() {
      if (!isTabletLandscape()) {
         getPresenter().continueAction();
      }
      dialogNavigatorInteractor.closeDialogActionPipe().send(new CloseDialogCommand());
   }

   @Override
   public void showShareDialog(String amount, Merchant merchant) {
      new ShareDialog(getContext(), type -> {
         getPresenter().trackSharing(type);
         ShareBundle shareBundle = MerchantHelper.buildShareBundle(getContext(), merchant, type);
         router.moveTo(ShareFragment.class, NavigationConfigBuilder.forActivity().data(shareBundle).build());
      }).show();
   }

   @Override
   protected DtlThrstTransactionSucceedPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlThrstTransactionSucceedPresenter(getArgs().getMerchant(), getArgs().getEarnedPoints(),
            getArgs().getTotalPoints());
   }
}
