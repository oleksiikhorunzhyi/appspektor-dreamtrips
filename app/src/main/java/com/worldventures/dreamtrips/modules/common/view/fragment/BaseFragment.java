package com.worldventures.dreamtrips.modules.common.view.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.fragment.InjectingFragment;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
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
    public void informUser(int stringId) {
        if (getActivity() != null && isAdded()) {
            SnackBar snackbar = new SnackBar(getActivity(), getActivity().getString(stringId));
            snackbar.show();
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
    public boolean isTabletLandscape() {
        return ViewUtils.isTablet(getActivity()) && ViewUtils.isLandscapeOrientation(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        getPresenter().onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().onStart();
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
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
            getPresenter().resume();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (this.presenter != null) {
            inject(this.presenter);
        }
    }

    public void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    abstract protected PM createPresenter(Bundle savedInstanceState);
}
