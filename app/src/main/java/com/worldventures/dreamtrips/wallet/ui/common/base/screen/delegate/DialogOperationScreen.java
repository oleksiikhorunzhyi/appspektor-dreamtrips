package com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import java.lang.ref.WeakReference;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.functions.Action1;

import static cn.pedant.SweetAlert.SweetAlertDialog.SUCCESS_TYPE;

public class DialogOperationScreen implements OperationScreen<SweetAlertDialog> {
    private static final long SUCCESS_TIMEOUT = 1500L;

    private WeakReference<View> viewRef;

    private WeakHandler timeoutHandler = new WeakHandler();

    private SweetAlertDialog successDialog;
    private SweetAlertDialog errorDialog;
    private SweetAlertDialog progressDialog;

    private boolean isSuccessWithDelay;

    public DialogOperationScreen(@NonNull View view, boolean isSuccessWithDelay) {
        this.viewRef = new WeakReference<>(view);
        this.isSuccessWithDelay = isSuccessWithDelay;

        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                hideDialogs();
                v.removeOnAttachStateChangeListener(this);
            }
        });
    }

    public DialogOperationScreen(@NonNull View view) {
        this(view, true);
    }

    @Override
    public void showProgress(String msg) {
        View view = checkAndGetView();
        progressDialog = buildProgressDialog(view);
        progressDialog.setContentText(msg);

        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void notifyError(String msg, Action1<SweetAlertDialog> action) {
        errorDialog = buildErrorDialog(checkAndGetView());
        errorDialog.setContentText(msg);
        errorDialog.show();
    }

    @Override
    public void showSuccess(String msg, Action1<SweetAlertDialog> action) {
        successDialog = buildSuccessDialog(checkAndGetView());
        successDialog.setContentText(msg);
        successDialog.setConfirmClickListener(sweetAlertDialog -> {
            timeoutHandler.removeCallbacksAndMessages(null);
            sweetAlertDialog.dismissWithAnimation();

            if (action != null) {
                action.call(sweetAlertDialog);
            }
        });
        successDialog.show();

        if (isSuccessWithDelay) {
            timeoutHandler.postDelayed(() -> {
                if (successDialog.isShowing()) {
                    successDialog.dismissWithAnimation();
                    if (action != null) action.call(successDialog);
                }
            }, SUCCESS_TIMEOUT);
        }
    }

    @Override
    public Context context() {
        return checkAndGetView().getContext();
    }

    private SweetAlertDialog buildProgressDialog(@NonNull View view) {
        SweetAlertDialog progressDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("");
        progressDialog.setCancelable(false);

        return progressDialog;
    }

    private SweetAlertDialog buildSuccessDialog(@NonNull View view) {
        SweetAlertDialog successDialog = new SweetAlertDialog(view.getContext(), SUCCESS_TYPE)
                .setTitleText("");
        successDialog.setCancelable(false);

        return successDialog;
    }

    private SweetAlertDialog buildErrorDialog(@NonNull View view) {
        return new SweetAlertDialog(view.getContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("");
    }

    private void hideDialogs() {
        if (progressDialog != null) progressDialog.dismiss();
        if (successDialog != null) successDialog.dismiss();
        if (errorDialog != null) errorDialog.dismiss();
    }

    private View checkAndGetView() {
        View v = viewRef.get();
        if (v == null) {
            throw new IllegalStateException("View == null");
        }

        return v;
    }
}