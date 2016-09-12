package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.event.CloseDialogEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.properratingbar.ProperRatingBar;

@Layout(R.layout.fragment_transaction_succeed)
public class DtlTransactionSucceedFragment extends RxBaseFragmentWithArgs<DtlTransactionSucceedPresenter, MerchantBundle> implements DtlTransactionSucceedPresenter.View {

   @InjectView(R.id.total) TextView total;
   @InjectView(R.id.earned) TextView earned;

   @InjectView(R.id.rating_bar) ProperRatingBar properRatingBar;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      properRatingBar.setListener(ratingBar -> getPresenter().rate(ratingBar.getRating()));
   }

   @Override
   public boolean onApiError(ErrorResponse errorResponse) {
      return true;
   }

   @Override
   public void onApiCallFailed() {
   }

   @Override
   public void setCongratulations(DtlTransactionResult result) {
      total.setText(String.valueOf((int) result.getTotal()));
      earned.setText(String.format("+%dpt", Double.valueOf(result.getEarnedPoints()).intValue()));
   }

   @OnClick(R.id.share)
   void onShareClicked() {
      getPresenter().share();
   }

   @OnClick(R.id.done)
   void onDoneClicked() {
      getPresenter().done();
      //TODO think about dismissing dialog from fragment
      eventBus.post(new CloseDialogEvent());
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
   protected DtlTransactionSucceedPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlTransactionSucceedPresenter(getArgs().getMerchant());
   }
}
