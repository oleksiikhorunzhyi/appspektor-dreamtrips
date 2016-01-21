package com.worldventures.dreamtrips.modules.settings.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.settings.view.presenter.SettingsPresenter;

@Layout(R.layout.fragment_settings)
public class SettingsFragment extends BaseFragment<SettingsPresenter> implements SettingsPresenter.View {

    @Override
    protected SettingsPresenter createPresenter(Bundle savedInstanceState) {
        return new SettingsPresenter();
    }
}
