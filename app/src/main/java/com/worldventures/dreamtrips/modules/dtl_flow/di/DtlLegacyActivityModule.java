package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPointsEstimationPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanQrCodePresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanReceiptPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstFlowPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstScanReceiptPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstThankYouScreenPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstTransactionSucceedPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlVerifyAmountPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DtlPointsEstimationFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanQrCodeFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanReceiptFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlThrstFlowFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlThrstScanReceiptFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlThrstTransactionSucceedFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlTransactionSucceedFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlVerifyAmountFragment;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot.DtlThankYouScreenFragment;

import dagger.Module;

@Module(
      injects = {
            DtlScanReceiptFragment.class,
            DtlScanReceiptPresenter.class,
            DtlThrstScanReceiptFragment.class,
            DtlThrstScanReceiptPresenter.class,
            DtlPointsEstimationFragment.class,
            DtlPointsEstimationPresenter.class,
            DtlScanQrCodeFragment.class,
            DtlScanQrCodePresenter.class,
            DtlTransactionSucceedFragment.class,
            DtlTransactionSucceedPresenter.class,
            DtlThrstTransactionSucceedFragment.class,
            DtlThrstTransactionSucceedPresenter.class,
            DtlVerifyAmountFragment.class,
            DtlVerifyAmountPresenter.class,
            DtlThrstFlowFragment.class,
            DtlThrstFlowPresenter.class,
            DtlThankYouScreenFragment.class,
            DtlThrstThankYouScreenPresenter.class
      },
      complete = false, library = true)
public class DtlLegacyActivityModule {

}
