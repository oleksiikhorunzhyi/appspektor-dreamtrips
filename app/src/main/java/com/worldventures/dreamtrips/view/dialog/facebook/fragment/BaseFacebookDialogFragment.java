package com.worldventures.dreamtrips.view.dialog.facebook.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.dialog.ImagePickCallback;

public abstract class BaseFacebookDialogFragment extends DialogFragment {
    protected Injector injector;
    protected ImagePickCallback imagePickCallback;
    protected FragmentManager fm;

    public abstract String getDialogTag();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_DreamTripsTheme);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void show(FragmentManager fm, Injector injector, ImagePickCallback callback) {

        this.fm = fm;
        this.injector = injector;
        imagePickCallback = callback;
        show(fm, getDialogTag());
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
