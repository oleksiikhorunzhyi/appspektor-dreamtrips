package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.zxing.Result;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.api.error.FieldError;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanQrCodePresenter;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import permissions.dispatcher.DeniedPermission;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import permissions.dispatcher.ShowsRationale;
import timber.log.Timber;

@Layout(R.layout.fragment_scan_qr)
@RuntimePermissions
public class DtlScanQrCodeFragment extends BaseFragmentWithArgs<DtlScanQrCodePresenter, DtlPlace>
        implements DtlScanQrCodePresenter.View, ZXingScannerView.ResultHandler {

    @InjectView(R.id.scanner_view)
    ZXingScannerView scanner;

    @Inject
    @Named(RouteCreatorModule.DTL_TRANSACTION)
    RouteCreator<DtlTransaction> routeCreator;

    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.address)
    TextView address;
    @InjectView(R.id.place_image)
    SimpleDraweeView placeImage;

    DtlPlaceHelper helper;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        helper = new DtlPlaceHelper(activity);
    }

    @Override
    protected DtlScanQrCodePresenter createPresenter(Bundle savedInstanceState) {
        return new DtlScanQrCodePresenter(getArgs());
    }

    @Override
    public void onResume() {
        super.onResume();
        DtlScanQrCodeFragmentPermissionsDispatcher.startCameraWithCheck(this);
        scanner.setResultHandler(this);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void startCamera() {
        scanner.startCamera();
    }

    @ShowsRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera() {
        Snackbar.make(getView(), R.string.permission_camera_rationale, Snackbar.LENGTH_SHORT).show();
    }

    @DeniedPermission(Manifest.permission.CAMERA)
    void showDeniedForCamera() {
        Snackbar.make(getView(), R.string.no_camera_permission, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setPlace(DtlPlace place) {
        name.setText(place.getDisplayName());
        if (!TextUtils.isEmpty(place.getAddress1())) {
            address.setText(place.getAddress1());
        }
        if (!place.getImages().isEmpty()) {
            placeImage.setImageURI(Uri.parse(place.getImages().get(0).getImagePath()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        scanner.stopCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        DtlScanQrCodeFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void handleResult(final Result rawResult) {
        String contents = rawResult.getText();
        getPresenter().codeScanned(contents);
        Timber.d(contents);
    }

    @Override
    public void openTransactionSuccess(DtlPlace dtlPlace, DtlTransaction dtlTransaction) {
        getActivity().finish();
        eventBus.postSticky(new DtlTransactionSucceedEvent(dtlTransaction));
    }

    private SweetAlertDialog pDialog;

    @Override
    public void hideProgress() {
        if (pDialog != null) pDialog.dismissWithAnimation();
    }

    @Override
    public void showProgress(@StringRes int titleText) {
        if (pDialog == null || !pDialog.isShowing()) {
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.theme_main));
            pDialog.setTitleText(getString(titleText));
            pDialog.setCancelable(false);
            pDialog.show();
        } else
            pDialog.setTitle(getString(titleText));
    }

    @Override
    public void photoUploadError() {
        SweetAlertDialog alertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.alert))
                .setContentText(getString(R.string.dtl_photo_upload_error))
                .setConfirmText(getString(R.string.ok))
                .setConfirmClickListener(sweetAlertDialog -> moveTo(Route.DTL_SCAN_RECEIPT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        hideProgress();
        //
        FieldError fieldError = Queryable.from(errorResponse.getErrors()).firstOrDefault();
        //
        if (fieldError != null) {
            SweetAlertDialog alertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.alert))
                    .setContentText(fieldError.getFirstMessage())
                    .setConfirmText(getString(R.string.ok))
                    .setConfirmClickListener(sweetAlertDialog -> {
                        switch (fieldError.field) {
                            case DtlTransaction.AMOUNT:
                            case DtlTransaction.RECEIPT:
                                moveTo(Route.DTL_SCAN_RECEIPT);
                                break;
                            case DtlTransaction.TOKEN:
                            case DtlTransaction.LOCATION:
                                scanner.startCamera();
                                sweetAlertDialog.dismissWithAnimation();
                                break;
                            default:
                                getActivity().finish();
                                break;
                        }
                    });
            alertDialog.setCancelable(false);
            alertDialog.show();
            return true;
        }

        return false;
    }

    private void moveTo(Route route) {
        getActivity().finish();
        router.moveTo(route, NavigationConfigBuilder.forActivity()
                .data(getArgs())
                .build());
    }
}
