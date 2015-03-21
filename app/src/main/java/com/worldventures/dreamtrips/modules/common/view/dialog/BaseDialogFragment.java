package com.worldventures.dreamtrips.modules.common.view.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.fragment.InjectingDialogFragment;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;

import butterknife.ButterKnife;

/**
 * Created by 1 on 29.01.15.
 */
public abstract class BaseDialogFragment<PM extends BasePresenter> extends InjectingDialogFragment implements BasePresenter.View {

    private PM presentationModel;

    public PM getPresentationModel() {
        return presentationModel;
    }

    @Override
    public void informUser(String stringId) {

    }

    @Override
    public void alert(String s) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.presentationModel = createPresentationModel(savedInstanceState);

        if (this.presentationModel != null) {
            inject(this.presentationModel);
        }

        this.presentationModel.init();

        Layout layout = this.getClass().getAnnotation(Layout.class);

        if (layout == null) {
            throw new IllegalArgumentException("ConfigurableFragment should have Layout annotation");
        }

        View view;
        view = inflater.inflate(layout.value(), container, false);
        ButterKnife.inject(this, view);

        afterCreateView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresentationModel().resume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (this.presentationModel != null) {
            inject(this.presentationModel);
        }

    }

    abstract protected PM createPresentationModel(Bundle savedInstanceState);
}
