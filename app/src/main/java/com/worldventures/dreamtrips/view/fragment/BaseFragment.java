package com.worldventures.dreamtrips.view.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.Dialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.fragment.InjectingFragment;
import com.worldventures.dreamtrips.presentation.BasePresentation;
import com.worldventures.dreamtrips.utils.anotation.IgnoreRobobinding;
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
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), stringId, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void alert(String s) {
        if (getActivity() != null) getActivity().runOnUiThread(() -> {
            getActivity().runOnUiThread(() -> {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
                builder.title("Alert").content(s).positiveText("Ok").show();
            });;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.presentationModel = createPresentationModel(savedInstanceState);

        if (this.presentationModel != null) {
            inject(this.presentationModel);
            this.presentationModel.init();
        } else {
            throw new IllegalArgumentException("Presentation model can't be null");
        }


        Layout layout = this.getClass().getAnnotation(Layout.class);

        if (layout == null) {
            throw new IllegalArgumentException("ConfigurableFragment should have Layout annotation");
        }

        ViewBinder viewBinder = ((BaseActivity) getActivity()).createViewBinder();

        View view;
        IgnoreRobobinding pmAnnotation = presentationModel.getClass().getAnnotation(IgnoreRobobinding.class);

        if (pmAnnotation != null) {
            view = inflater.inflate(layout.value(), container, false);
        } else {
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
