package com.worldventures.dreamtrips.modules.settings.view.fragment;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.settings.view.presenter.GeneralSettingsPresenter;

public class GeneralSettingsFragment extends SettingsFragment<GeneralSettingsPresenter>
        implements GeneralSettingsPresenter.View {

    @Override
    protected void registerCells() {

    }

    @Override
    protected GeneralSettingsPresenter createPresenter(Bundle savedInstanceState) {
        return new GeneralSettingsPresenter();
    }
}
