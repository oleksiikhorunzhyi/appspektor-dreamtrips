package com.worldventures.dreamtrips.social.di;

import com.worldventures.dreamtrips.core.navigation.DialogFragmentNavigator;
import com.worldventures.dreamtrips.social.ui.activity.SocialMainActivity;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.social.ui.activity.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.SharePresenter;
import com.worldventures.dreamtrips.social.ui.activity.SocialComponentActivity;
import com.worldventures.dreamtrips.social.ui.activity.ConfigChangesAwareComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.social.ui.activity.TransparentSocialComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragmentWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.dialog.MessageDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.dialog.ProgressDialogFragment;

import dagger.Module;

@Module(injects = {
      MainActivityPresenter.class,
      SocialMainActivity.class,
      ShareFragment.class,
      SocialComponentActivity.class,
      TransparentSocialComponentActivity.class,
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
