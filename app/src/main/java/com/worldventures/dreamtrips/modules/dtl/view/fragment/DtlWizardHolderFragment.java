package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

public class DtlWizardHolderFragment extends BaseFragmentWithArgs<Presenter, DtlPlace> {

    @Override
    protected Presenter createPresenter(Bundle savedInstanceState) {
        return new Presenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (getFragmentManager().getBackStackEntryCount() > 0) return;

        router.moveTo(Route.DTL_SCAN_RECEIPT, NavigationConfigBuilder.forFragment()
                .containerId(R.id.container_wizard)
                .fragmentManager(getChildFragmentManager())
                .data(getArgs())
                .build());
    }
}
