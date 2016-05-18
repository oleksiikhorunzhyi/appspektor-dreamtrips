package com.messenger.ui.dialog;


import android.content.Context;
import android.support.v7.app.AlertDialog;

import rx.functions.Action0;

public class LeaveChatDialog {

    private final AlertDialog dialog;

    private Action0 positiveListener;
    private Action0 negativeListener;

    public LeaveChatDialog(Context context, String message) {
        dialog = new AlertDialog.Builder(context)
                .setNegativeButton(android.R.string.cancel, (dialog, which1) -> {
                    if (negativeListener != null)
                        negativeListener.call();
                })
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (positiveListener != null)
                        positiveListener.call();
                })
                .setMessage(message)
                .create();
    }

    public LeaveChatDialog setNegativeListener(Action0 negativeListener) {
        this.negativeListener = negativeListener;
        return this;
    }

    public LeaveChatDialog setPositiveListener(Action0 positiveListener) {
        this.positiveListener = positiveListener;
        return this;
    }

    public void show() {
        dialog.show();
    }
}
