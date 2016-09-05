package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlEnrollWizard;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlVerifyAmountPresenter;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

@SuppressLint("DefaultLocale")
@Layout(R.layout.fragment_verify_amount)
public class DtlVerifyAmountFragment extends RxBaseFragmentWithArgs<DtlVerifyAmountPresenter, MerchantIdBundle> implements DtlVerifyAmountPresenter.View {

   @InjectView(R.id.dt_points) TextView dtPoints;
   @InjectView(R.id.spent_amount) TextView spentAmount;
   @InjectView(R.id.receipt) SimpleDraweeView receipt;
   @InjectView(R.id.info) TextView info;
   //
   @Inject @Named(RouteCreatorModule.DTL_TRANSACTION) RouteCreator<DtlTransaction> routeCreator;
   //
   private DtlEnrollWizard dtlEnrollWizard;

   @Override
   protected DtlVerifyAmountPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlVerifyAmountPresenter(getArgs().getMerchantId());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      dtlEnrollWizard = new DtlEnrollWizard(router, routeCreator);
   }

   @Override
   public void onResume() {
      super.onResume();
      ButterKnife.<Toolbar>findById(getActivity(), R.id.toolbar_actionbar).setTitle(R.string.dtl_verify_amount);
   }

   @OnClick(R.id.rescan)
   void onRescan() {
      getPresenter().rescan();
   }

   @OnClick(R.id.scan_merchant_code)
   void onScanQr() {
      getPresenter().scanQr();
   }

   @Override
   public void openScanQr(DtlTransaction dtlTransaction) {
      dtlEnrollWizard.proceed(getFragmentManager(), dtlTransaction, getArgs());
   }

   @Override
   public void openScanReceipt(DtlTransaction dtlTransaction) {
      dtlEnrollWizard.clearAndProceed(getFragmentManager(), dtlTransaction, getArgs());
   }

   @Override
   public void attachTransaction(DtlTransaction dtlTransaction, Currency currency) {
      spentAmount.setText(String.format("%s %.2f %s", currency.prefix(), dtlTransaction.getBillTotal(), currency
            .suffix()));
      receipt.setController(GraphicUtils.provideFrescoResizingController(Uri.parse(dtlTransaction.getUploadTask()
            .getFilePath()), receipt.getController()));
   }

   @OnClick(R.id.infoToggle)
   void infoToggle() {
      info.setVisibility(info.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
   }

   @Override
   public void attachDtPoints(int count) {
      dtPoints.setText(String.format("+%dpt", count));
   }
}
