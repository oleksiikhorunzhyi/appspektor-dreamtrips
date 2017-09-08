package com.worldventures.dreamtrips.modules.social_common;

import com.worldventures.dreamtrips.core.navigation.DialogFragmentNavigator;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.SharePresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ConfigChangesAwareComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.common.view.activity.TransparentComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragmentWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.dialog.MessageDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.dialog.ProgressDialogFragment;

import dagger.Module;

@Module(injects = {
      MainActivityPresenter.class,
      MainActivity.class,
      ShareFragment.class,
      ComponentActivity.class,
      TransparentComponentActivity.class,
      ConfigChangesAwareComponentActivity.class,
      ComponentPresenter.class,
      ProgressDialogFragment.class,
      MessageDialogFragment.class,
      DialogFragmentNavigator.NavigationDialogFragment.class,
      BaseDialogFragmentWithPresenter.class,
      SharePresenter.class,

}, complete = false, library = true)
public class SocialCommonActivityModule {
}
