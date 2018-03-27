package com.worldventures.dreamtrips.social.di;

import com.messenger.ui.module.flagging.FlaggingPresenterImpl;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.navigation.DialogFragmentNavigator;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragmentWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.dialog.MessageDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.dialog.ProgressDialogFragment;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.social.ui.activity.ConfigChangesAwareComponentActivity;
import com.worldventures.dreamtrips.social.ui.activity.SocialComponentActivity;
import com.worldventures.dreamtrips.social.ui.activity.SocialMainActivity;
import com.worldventures.dreamtrips.social.ui.activity.TransparentSocialComponentActivity;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.social.ui.activity.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.util.PermissionLocationDelegate;
import com.worldventures.dreamtrips.social.ui.share.presenter.SharePresenter;
import com.worldventures.dreamtrips.social.ui.share.view.ShareFragment;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
      MainActivityPresenter.class,
      SocialMainActivity.class,
      ShareFragment.class,
      SocialComponentActivity.class,
      ConfigChangesAwareComponentActivity.class,
      TransparentSocialComponentActivity.class,
      FlaggingPresenterImpl.class,
      ComponentPresenter.class,
      ProgressDialogFragment.class,
      MessageDialogFragment.class,
      DialogFragmentNavigator.NavigationDialogFragment.class,
      BaseDialogFragmentWithPresenter.class,
      SharePresenter.class,

}, complete = false, library = true)
public class SocialCommonActivityModule {

   @Provides
   PermissionLocationDelegate provideWebLocationDelegate(LocationDelegate locationDelegate, PermissionDispatcher permissionDispatcher) {
      return new PermissionLocationDelegate(locationDelegate, permissionDispatcher);
   }
}
