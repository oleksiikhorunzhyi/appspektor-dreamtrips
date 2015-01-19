package com.worldventures.dreamtrips.view.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.fragment.InjectingFragment;
import com.worldventures.dreamtrips.presentation.BasePresentation;
import com.worldventures.dreamtrips.view.activity.BaseActivity;

import org.robobinding.ViewBinder;

import butterknife.ButterKnife;

public abstract class BaseFragment<PM extends BasePresentation> extends InjectingFragment implements BasePresentation.View {

    private PM presentationModel;

    public PM getPresentationModel() {
        return presentationModel;
    }

    @Override
    public void informUser(String stringId) {

    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.presentationModel = createPresentationModel(savedInstanceState);

        if (this.presentationModel != null) {
            inject(this.presentationModel);
        }

        Layout layout = this.getClass().getAnnotation(Layout.class);

        if (layout == null) {
            throw new IllegalArgumentException("ConfigurableFragment should have Layout annotation");
        }

        ViewBinder viewBinder = ((BaseActivity) getActivity()).createViewBinder();

        View view;

        if (container != null) {
            view = viewBinder.inflateAndBindWithoutAttachingToRoot(
                    layout.value(),
                    this.presentationModel,
                    container
            );
        } else {
            view = viewBinder.inflateAndBind(
                    layout.value(),
                    this.presentationModel
            );
        }

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
