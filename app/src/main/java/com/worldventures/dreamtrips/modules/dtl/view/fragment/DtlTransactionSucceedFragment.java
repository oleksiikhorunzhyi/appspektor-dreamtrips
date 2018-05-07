package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.worldventures.core.service.DeviceInfoProvider;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.service.DialogNavigatorInteractor;
import com.worldventures.dreamtrips.core.navigation.service.command.CloseDialogCommand;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.review.DtlReviewPath;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.social.ui.share.bundle.ShareBundle;
import com.worldventures.dreamtrips.social.ui.share.view.ShareFragment;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;

@Layout(R.layout.fragment_transaction_succeed)
@ComponentPresenter.ComponentTitle(R.string.dtl_success_title)
public class DtlTransactionSucceedFragment extends RxBaseFragmentWithArgs<DtlTransactionSucceedPresenter, MerchantBundle> implements DtlTransactionSucceedPresenter.View {

   @InjectView(R.id.total) TextView total;
   @InjectView(R.id.earned) TextView earned;
   @Inject DialogNavigatorInteractor dialogNavigatorInteractor;
   @Inject DeviceInfoProvider deviceInfoProvider;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
   }

   @Override
   public void setCongratulations(DtlTransactionResult result) {
      total.setText(String.valueOf((int) result.getTotal()));
      earned.setText(String.format("+%dpt", Double.valueOf(result.getEarnedPoints()).intValue()));
   }

   @Override
   public void sendToReview(Merchant merchant) {
      if (!deviceInfoProvider.isTablet()) {
         Flow.get(getContext()).set(new DtlReviewPath(merchant, true));
      }
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
         router.moveTo(ShareFragment.class, NavigationConfigBuilder.forActivity().data(shareBundle).build());
      }).show();
   }

   @Override
   protected DtlTransactionSucceedPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlTransactionSucceedPresenter(getArgs().getMerchant());
   }
}
