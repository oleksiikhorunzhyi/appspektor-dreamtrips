package com.worldventures.dreamtrips.modules.common.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.fragment.InjectingFragment;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import butterknife.ButterKnife;
import icepick.Icepick;


public abstract class BaseFragment<PM extends Presenter> extends InjectingFragment implements Presenter.View {

    private PM presenter;

    public PM getPresenter() {
        return presenter;
    }

    protected abstract PM createPresenter(Bundle savedInstanceState);

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.presenter = createPresenter(savedInstanceState);
        if (this.presenter == null) {
            throw new IllegalArgumentException("Presenter can't be null");
        }
        inject(this.presenter);
        this.presenter.onInjected();
        Icepick.restoreInstanceState(this, savedInstanceState);
        this.presenter.restoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
        if (presenter != null) this.presenter.saveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Layout layout = this.getClass().getAnnotation(Layout.class);
        if (layout == null) {
            throw new IllegalArgumentException("ConfigurableFragment should have Layout annotation");
        }
        return inflater.inflate(layout.value(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        afterCreateView(view);
        restoreState(savedInstanceState);
        //
        this.presenter.takeView(this);
    }

    protected void restoreState(Bundle savedInstanceState) {
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (this.presenter != null) this.presenter.onMenuPrepared();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        presenter.dropView();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Notif helpers
    ///////////////////////////////////////////////////////////////////////////

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
                builder.title(R.string.alert).content(s).positiveText(R.string.OK).show();
            }));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Misc helpers
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isTabletLandscape() {
        return ViewUtils.isTablet(getActivity()) && ViewUtils.isLandscapeOrientation(getActivity());
    }

    public void hideSoftInput(View view) {
        SoftInputUtil.hideSoftInputMethod(view);
    }

}
