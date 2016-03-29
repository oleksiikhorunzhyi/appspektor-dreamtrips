package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.Status;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlLocationsBundle;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlStartPresenter;

import javax.inject.Inject;

import butterknife.InjectView;
import timber.log.Timber;

/**
 * Transitional fragment that determines further navigation.<br />
 */
@Layout(R.layout.fragment_dtl_start_empty)
@MenuResource(R.menu.menu_mock)
public class DtlStartFragment extends RxBaseFragment<DtlStartPresenter> implements DtlStartPresenter.View {

    private static final int REQUEST_CHECK_SETTINGS = 1488;

    @Inject
    ActivityResultDelegate activityResultDelegate;
    //
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    public void locationResolutionRequired(Status status) {
        try {
            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException th) {
            Timber.e(th, "Error opening settings activity.");
        }
    }

    @Override
    protected DtlStartPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlStartPresenter();
    }

    @Override
    public void openDtlLocationsScreen() {
        // TODO :: NOTE: needed commitAllowingStateLoss here so dropped Router's usage
        Bundle args = new Bundle();
        args.putParcelable(ComponentPresenter.EXTRA_DATA, new DtlLocationsBundle());
        //
        Fragment fragment = Fragment.instantiate(getActivity(), Route.DTL_LOCATIONS.getClazzName());
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.dtl_container, fragment, Route.DTL_LOCATIONS.getClazzName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void openMerchants() {
        // TODO :: NOTE: needed commitAllowingStateLoss here so dropped Router's usage
        Fragment fragment = Fragment.instantiate(getActivity(), Route.DTL_MERCHANTS_HOLDER.getClazzName());
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.dtl_container, fragment, Route.DTL_MERCHANTS_HOLDER.getClazzName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    public void activityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    getPresenter().onLocationResolutionGranted();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    getPresenter().onLocationResolutionDenied();
                    break;
            }
            activityResultDelegate.clear();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activityResult(activityResultDelegate.getRequestCode(), activityResultDelegate.getResultCode());
    }
}
