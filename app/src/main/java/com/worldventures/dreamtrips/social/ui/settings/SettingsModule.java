package com.worldventures.dreamtrips.social.ui.settings;

import com.worldventures.dreamtrips.social.ui.settings.dialog.SelectDialog;
import com.worldventures.dreamtrips.social.ui.settings.view.cell.SettingsSelectCell;
import com.worldventures.dreamtrips.social.ui.settings.view.fragment.GeneralSettingsFragment;
import com.worldventures.dreamtrips.social.ui.settings.view.fragment.NotificationsSettingsFragment;
import com.worldventures.dreamtrips.social.ui.settings.view.fragment.SettingsFragment;
import com.worldventures.dreamtrips.social.ui.settings.view.fragment.SettingsGroupFragment;
import com.worldventures.dreamtrips.social.ui.settings.view.presenter.SettingsGroupPresenter;
import com.worldventures.dreamtrips.social.ui.settings.view.presenter.SettingsPresenter;

import dagger.Module;

@Module(
      injects = {
            SettingsGroupPresenter.class,
            SettingsGroupFragment.class,
            NotificationsSettingsFragment.class,
            GeneralSettingsFragment.class,
            SettingsPresenter.class,
            SettingsFragment.class,
            SettingsSelectCell.class,
            SelectDialog.class
      },
      complete = false,
      library = true)
public class SettingsModule {

}
