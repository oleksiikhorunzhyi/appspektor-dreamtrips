package com.worldventures.dreamtrips.view.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.fragment.InjectingFragment;
import com.worldventures.dreamtrips.core.api.spice.DreamSpiceManager;
import com.worldventures.dreamtrips.presentation.BasePresentation;
import com.worldventures.dreamtrips.utils.anotation.IgnoreRobobinding;

import butterknife.ButterKnife;

public abstract class BaseFragment<PM extends BasePresentation> extends InjectingFragment implements BasePresentation.View {

    private PM presentationModel;

    public PM getPresentationModel() {
        return presentationModel;
    }

    @Override
    public void informUser(String stringId) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                SnackBar snackBar = new SnackBar(getActivity(), stringId);
                snackBar.setDismissTimer(stringId.length() > 100 ? 4000 : 2000);
                snackBar.show();
            });
        }
    }

    @Override
    public void alert(String s) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> getActivity().runOnUiThread(() -> {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
                builder.title("Alert").content(s).positiveText("Ok").show();
            }));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        stopSpiceManger();
        super.onStop();
    }

    private void stopSpiceManger() {
        DreamSpiceManager dreamSpiceManager = getPresentationModel().getDreamSpiceManager();
        if (dreamSpiceManager.isStarted()) {
            dreamSpiceManager.shouldStop();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (needSpiceManager()) {
            DreamSpiceManager dreamSpiceManager = getPresentationModel().getDreamSpiceManager();

            if (!dreamSpiceManager.isStarted()) dreamSpiceManager.start(getActivity());
        }
    }

    protected boolean needSpiceManager() {
        return true;
    }

    @Override
    public void onDestroy() {
        if (needSpiceManager()) {
            stopSpiceManager();
        }
        super.onDestroy();
    }

    private void stopSpiceManager() {
        if (getPresentationModel() != null)
            getPresentationModel().destroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.presentationModel = createPresentationModel(savedInstanceState);

        if (this.presentationModel != null) {
            inject(this.presentationModel);
        } else {
            throw new IllegalArgumentException("Presentation model can't be null");
        }


        Layout layout = this.getClass().getAnnotation(Layout.class);

        if (layout == null) {
            throw new IllegalArgumentException("ConfigurableFragment should have Layout annotation");
        }


        View view;
        view = inflater.inflate(layout.value(), container, false);

        ButterKnife.inject(this, view);
        this.presentationModel.init();
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
