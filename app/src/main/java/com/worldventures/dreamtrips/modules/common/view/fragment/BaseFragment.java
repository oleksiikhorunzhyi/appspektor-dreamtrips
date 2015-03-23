package com.worldventures.dreamtrips.modules.common.view.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.fragment.InjectingFragment;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import butterknife.ButterKnife;

public abstract class BaseFragment<PM extends Presenter> extends InjectingFragment implements Presenter.View {

    private PM presenter;

    public PM getPresenter() {
        return presenter;
    }

    @Override
    public void informUser(String stringId) {
        if (getActivity() != null && isAdded()) {
            getActivity().runOnUiThread(() -> {
                SnackBar snackBar = new SnackBar(getActivity(), stringId);
                snackBar.setDismissTimer(stringId.length() > 100 ? 4000 : 2000);
                snackBar.show();
            });
        }
    }

    @Override
    public void alert(String s) {
        if (getActivity() != null && isAdded()) {
            getActivity().runOnUiThread(() -> getActivity().runOnUiThread(() -> {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
                builder.title("Alert").content(s).positiveText("Ok").show();
            }));
        }
    }

    @Override
    public void onStop() {
        stopSpiceManger();
        super.onStop();
    }

    private void stopSpiceManger() {
        DreamSpiceManager dreamSpiceManager = getPresenter().getDreamSpiceManager();
        if (dreamSpiceManager.isStarted()) {
            dreamSpiceManager.shouldStop();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (needSpiceManager()) {
            DreamSpiceManager dreamSpiceManager = getPresenter().getDreamSpiceManager();

            if (!dreamSpiceManager.isStarted()) {
                dreamSpiceManager.start(getActivity());
            }
        }
    }

    protected boolean needSpiceManager() {
        return true;
    }

    @Override
    public void onDestroyView() {
        if (getPresenter() != null) {
            getPresenter().destroyView();
        }
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.presenter = createPresenter(savedInstanceState);

        if (this.presenter == null) {
            throw new IllegalArgumentException("Presenter can't be null");
        }

        inject(this.presenter);

        Layout layout = this.getClass().getAnnotation(Layout.class);

        if (layout == null) {
            throw new IllegalArgumentException("ConfigurableFragment should have Layout annotation");
        }

        View view = inflater.inflate(layout.value(), container, false);
        ButterKnife.inject(this, view);

        this.presenter.init();
        afterCreateView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().resume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (this.presenter != null) {
            inject(this.presenter);
        }
    }

    abstract protected PM createPresenter(Bundle savedInstanceState);
}
