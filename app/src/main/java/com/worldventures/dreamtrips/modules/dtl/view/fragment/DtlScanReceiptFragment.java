package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanReceiptPresenter;
import com.worldventures.dreamtrips.modules.dtl.validator.AmountValidator;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import butterknife.OnClick;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.fragment_scan_receipt)
@MenuResource(R.menu.menu_scan_receipt)
public class DtlScanReceiptFragment extends BaseFragmentWithArgs<DtlScanReceiptPresenter, DtlPlace>
        implements DtlScanReceiptPresenter.View {

    @InjectView(R.id.verify)
    Button verify;
    @InjectView(R.id.scan_receipt)
    Button scanReceipt;
    @InjectView(R.id.rescan_receipt)
    Button rescanReceipt;
    @InjectView(R.id.receipt)
    SimpleDraweeView receipt;
    @InjectView(R.id.shadow)
    View shadow;
    @InjectView(R.id.fab_progress)
    FabButton fabProgress;
    @InjectView(R.id.fabbutton_circle)
    CircleImageView circleView;
    @InjectView(R.id.amount_input)
    DTEditText amountInput;

    @Inject
    @Named(RouteCreatorModule.DTL_TRANSACTION)
    RouteCreator<DtlTransaction> routeCreator;

    private TextWatcherAdapter textWatcherAdapter = new TextWatcherAdapter() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getPresenter().onAmountChanged(s.toString());
        }
    };

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        amountInput.addValidator(new AmountValidator(getString(R.string.dtl_amount_invalid)));
    }

    @Override
    public void onResume() {
        super.onResume();
        amountInput.addTextChangedListener(textWatcherAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        amountInput.removeTextChangedListener(textWatcherAdapter);
    }

    @Override
    protected DtlScanReceiptPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlScanReceiptPresenter(getArgs());
    }

    @OnClick(R.id.verify)
    void onVerify() {
        if (amountInput.validate()) getPresenter().verify();
    }

    @OnClick(R.id.scan_receipt)
    void onImage() {
        getPresenter().scanReceipt();
    }

    @OnClick(R.id.rescan_receipt)
    void onRescan() {
        getPresenter().rescanReceipt();
    }

    @Override
    public void openScanQr(DtlPlace dtlPlace, DtlTransaction dtlTransaction) {
        NavigationBuilder.create()
                .with(fragmentCompass)
                .data(dtlPlace)
                .move(routeCreator.createRoute(dtlTransaction));
    }

    @OnClick(R.id.fab_progress)
    void onProgressClick() {
        getPresenter().onProgressClicked();
    }

    @Override
    public void hideScanButton() {
        scanReceipt.setVisibility(View.GONE);
    }

    @Override
    public void attachReceipt(Uri uri) {
        rescanReceipt.setVisibility(View.VISIBLE);
        receipt.setImageURI(uri);
    }

    @Override
    public void showProgress() {
        shadow.setVisibility(View.VISIBLE);
        fabProgress.setVisibility(View.VISIBLE);
        fabProgress.setIcon(R.drawable.ic_upload_cloud, R.drawable.ic_upload_cloud);
        fabProgress.setIndeterminate(true);
        fabProgress.showProgress(true);
        int color = getResources().getColor(R.color.bucket_blue);
        circleView.setColor(color);
    }

    @Override
    public void hideProgress() {
        fabProgress.setVisibility(View.GONE);
        shadow.setVisibility(View.GONE);
    }

    @Override
    public void uploadError() {
        fabProgress.showProgress(false);
        fabProgress.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
        int color = getResources().getColor(R.color.bucket_red);
        circleView.setColor(color);
    }

    @Override
    public void enableVerification() {
        verify.setEnabled(true);
    }

    @Override
    public void disableVerification() {
        verify.setEnabled(false);
    }
}
