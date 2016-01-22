package com.worldventures.dreamtrips.modules.settings;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.settings.view.cell.SettingsGroupCell;
import com.worldventures.dreamtrips.modules.settings.view.fragment.GeneralSettingsFragment;
import com.worldventures.dreamtrips.modules.settings.view.fragment.NotificationsSettingsFragment;
import com.worldventures.dreamtrips.modules.settings.view.fragment.SettingsFragment;
import com.worldventures.dreamtrips.modules.settings.view.fragment.SettingsGroupFragment;
import com.worldventures.dreamtrips.modules.settings.view.presenter.GeneralSettingsPresenter;
import com.worldventures.dreamtrips.modules.settings.view.presenter.NotificationsSettingsPresenter;
import com.worldventures.dreamtrips.modules.settings.view.presenter.SettingsGroupPresenter;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                SettingsGroupPresenter.class,
                SettingsGroupFragment.class,
                NotificationsSettingsFragment.class,
                NotificationsSettingsPresenter.class,
                GeneralSettingsFragment.class,
                GeneralSettingsPresenter.class,

                SettingsFragment.class,

                SettingsGroupCell.class,

        },
        complete = false,
        library = true
)
public class SettingsModule {

    public static final String SETTINGS = Route.SETTINGS.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideSettingsComponent() {
        return new ComponentDescription(Route.SETTINGS.name(), R.string.settings, R.string.settings, R.drawable.ic_membership, SettingsGroupFragment.class);
    }
}
