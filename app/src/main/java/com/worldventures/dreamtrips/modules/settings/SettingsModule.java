package com.worldventures.dreamtrips.modules.settings;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.settings.view.fragment.SettingsDetailsFragment;
import com.worldventures.dreamtrips.modules.settings.view.fragment.SettingsFragment;
import com.worldventures.dreamtrips.modules.settings.view.presenter.SettingsDetailsPresenter;
import com.worldventures.dreamtrips.modules.settings.view.presenter.SettingsPresenter;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                SettingsPresenter.class,
                SettingsFragment.class,

                SettingsDetailsFragment.class,
                SettingsDetailsPresenter.class,
        },
        complete = false,
        library = true
)
public class SettingsModule {

    public static final String SETTINGS = Route.SETTINGS.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideSettingsComponent() {
        return new ComponentDescription(Route.SETTINGS.name(), R.string.settings, R.string.settings, R.drawable.ic_membership, SettingsFragment.class);
    }
}
