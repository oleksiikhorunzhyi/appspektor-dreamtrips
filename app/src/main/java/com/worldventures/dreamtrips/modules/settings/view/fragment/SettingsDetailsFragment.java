package com.worldventures.dreamtrips.modules.settings.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.settings.view.presenter.SettingsDetailsPresenter;

@Layout(R.layout.fragment_settings)
public class SettingsDetailsFragment extends BaseFragmentWithArgs<SettingsDetailsPresenter, Bundle> implements SettingsDetailsPresenter.View {

    @Override
    protected SettingsDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new SettingsDetailsPresenter();
    }
}
