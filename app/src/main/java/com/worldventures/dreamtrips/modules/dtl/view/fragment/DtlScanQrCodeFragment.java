package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanQrCodePresenter;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import timber.log.Timber;

@Layout(R.layout.fragment_scan_qr)
public class DtlScanQrCodeFragment extends BaseFragmentWithArgs<DtlScanQrCodePresenter, DtlPlace>
        implements DtlScanQrCodePresenter.View, ZBarScannerView.ResultHandler {

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    @InjectView(R.id.scanner_view)
    ZBarScannerView scanner;

    @Inject
    @Named(RouteCreatorModule.DTL_TRANSACTION)
    RouteCreator<DtlTransaction> routeCreator;

    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.category)
    TextView category;
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
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            scanner.startCamera();
        } else {
            requestCameraPermission();
        }
        scanner.setResultHandler(this); // Register ourselves as a handler for scan results.
    }

    @Override
    public void setPlace(DtlPlace place) {
        name.setText(place.getName());
        category.setText(helper.getFirstCategoryName(place));
        address.setText(String.format("%s\n%s", place.getAddress1(), place.getAddress2()));
        if (!place.getMediaList().isEmpty()) {
            placeImage.setImageURI(Uri.parse(place.getMediaList().get(0).getMediaFileName()));
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        Snackbar.make(scanner, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, view -> ActivityCompat.requestPermissions(getActivity(), permissions,
                        RC_HANDLE_CAMERA_PERM))
                .show();
    }

    @Override
    public void onPause() {
        super.onPause();
        scanner.stopCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // we have permission, so create the cameraSource
            scanner.startCamera();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, (dialog, id) -> getActivity().finish())
                .show();
    }

    @Override
    public void handleResult(final Result rawResult) {
        String contents = rawResult.getContents();
        getPresenter().codeScanned(contents);
        Timber.d(contents);
    }

    @Override
    public void openTransactionSuccess(DtlPlace dtlPlace, DtlTransaction dtlTransaction) {
        getActivity().finish();
        NavigationBuilder.create()
                .with(activityRouter)
                .data(dtlPlace)
                .move(routeCreator.createRoute(dtlTransaction));
    }

    private SweetAlertDialog pDialog;

    @Override
    public void hideProgress() {
        if (pDialog != null) pDialog.dismissWithAnimation();
        scanner.startCamera();
    }

    @Override
    public void showProgress() {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.theme_main));
        pDialog.setTitleText(getString(R.string.wait));
        pDialog.setCancelable(false);
        pDialog.show();
    }
}
