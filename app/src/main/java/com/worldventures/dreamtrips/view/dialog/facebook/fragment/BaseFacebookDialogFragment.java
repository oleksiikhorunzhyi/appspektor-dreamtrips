package com.worldventures.dreamtrips.view.dialog.facebook.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.view.Window;

import com.worldventures.dreamtrips.view.activity.Injector;
import com.worldventures.dreamtrips.view.dialog.ImagePickCallback;

public class BaseFacebookDialogFragment extends DialogFragment {
    private static final String FB_TAG = "FB_TAG";
    protected Injector injector;
    protected ImagePickCallback imagePickCallback;
    private FragmentManager fm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }


    public void show(FragmentManager fm, Injector injector, ImagePickCallback callback) {

        this.fm = fm;
        this.injector = injector;
        imagePickCallback = callback;
        show(fm, FB_TAG);
    }
}
