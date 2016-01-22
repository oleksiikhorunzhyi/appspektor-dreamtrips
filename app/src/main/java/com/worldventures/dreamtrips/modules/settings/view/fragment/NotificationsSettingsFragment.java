package com.worldventures.dreamtrips.modules.settings.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.view.presenter.NotificationsSettingsPresenter;

@Layout(R.layout.fragment_settings_notifications)
public class NotificationsSettingsFragment extends SettingsFragment<NotificationsSettingsPresenter>
        implements NotificationsSettingsPresenter.View {

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
    }

    @Override
    protected void registerCells() {

    }

    @Override
    protected NotificationsSettingsPresenter createPresenter(Bundle savedInstanceState) {
        return new NotificationsSettingsPresenter();
    }
}
