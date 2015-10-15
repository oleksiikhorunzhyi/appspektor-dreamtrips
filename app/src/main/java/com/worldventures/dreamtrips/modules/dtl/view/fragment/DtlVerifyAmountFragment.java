package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlVerifyAmountPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_verify_amount)
public class DtlVerifyAmountFragment extends BaseFragmentWithArgs<DtlVerifyAmountPresenter, DtlPlace>
        implements DtlVerifyAmountPresenter.View {

    @InjectView(R.id.dt_points)
    TextView dtPoints;
    @InjectView(R.id.spent_amount)
    TextView spentAmount;
    @InjectView(R.id.receipt)
    SimpleDraweeView receipt;

    @Override
    protected DtlVerifyAmountPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlVerifyAmountPresenter(getArgs());
    }

    @OnClick(R.id.rescan)
    void onRescan() {
        getPresenter().rescan();
    }

    @OnClick(R.id.scan_merchant_code)
    void onScanQr() {
        getActivity().finish();
        NavigationBuilder.create()
                .with(activityRouter)
                .data(getArgs())
                .move(Route.DTL_SCAN_QR);
    }

    @Override
    public void openScanReceipt() {
        getActivity().finish();
        NavigationBuilder.create()
                .with(activityRouter)
                .data(getArgs())
                .move(Route.DTL_SCAN_RECEIPT);
    }

    @Override
    public void attachDtPoints(int count) {
        dtPoints.setText(getString(R.string.dtl_dt_points, count));
    }

    @Override
    public void attachTransaction(DtlTransaction dtlTransaction) {
        spentAmount.setText(getString(R.string.dtl_spent_amount, dtlTransaction.getAmount()));
        receipt.setImageURI(Uri.parse(dtlTransaction.getReceiptPhoto()));
    }
}
