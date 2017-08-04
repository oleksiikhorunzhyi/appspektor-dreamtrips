package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.service.DialogNavigatorInteractor;
import com.worldventures.dreamtrips.core.navigation.service.command.CloseDialogCommand;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstPaymentCompletedBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstTransactionSucceedPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPath;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;

@Layout(R.layout.fragment_transaction_succeed)
public class DtlThrstTransactionSucceedFragment extends RxBaseFragmentWithArgs<DtlThrstTransactionSucceedPresenter, ThrstPaymentCompletedBundle> implements DtlThrstTransactionSucceedPresenter.View {

   @InjectView(R.id.earned) TextView earned;
   @InjectView(R.id.total) TextView total;
   @Inject DialogNavigatorInteractor dialogNavigatorInteractor;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      ThrstPaymentCompletedBundle thrstPaymentCompletedBundle = getArgs();
      earned.setText(String.format("+%spt", thrstPaymentCompletedBundle.getEarnedPoints()));
      total.setText(thrstPaymentCompletedBundle.getTotalPoints());
   }

   @Override
   public boolean onApiError(ErrorResponse errorResponse) {
      return true;
   }

   @Override
   public void onApiCallFailed() {
   }

   @Override
   public void sendToReview(Merchant merchant) {
      Flow.get(getContext()).set(new DtlCommentReviewPath(merchant, false, true));
   }

   @OnClick(R.id.share)
   void onShareClicked() {
      getPresenter().share();
   }

   @OnClick(R.id.done)
   void onDoneClicked() {
      getPresenter().done();
      dialogNavigatorInteractor.closeDialogActionPipe().send(new CloseDialogCommand());
   }

   @Override
   public void showShareDialog(int amount, Merchant merchant) {
      new ShareDialog(getContext(), type -> {
         getPresenter().trackSharing(type);
         ShareBundle shareBundle = MerchantHelper.buildShareBundle(getContext(), merchant, type);
         router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity().data(shareBundle).build());
      }).show();
   }

   @Override
   protected DtlThrstTransactionSucceedPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlThrstTransactionSucceedPresenter(getArgs().getMerchant());
   }
}
