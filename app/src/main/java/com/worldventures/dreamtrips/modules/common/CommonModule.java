package com.worldventures.dreamtrips.modules.common;

import android.content.Context;

import com.messenger.di.MessengerModule;
import com.messenger.ui.activity.MessengerActivity;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.ComponentsConfig;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.DialogFragmentNavigator;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImageFragment;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImagePresenter;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.command.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.delegate.PickImageDelegate;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.PlayerPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.SharePresenter;
import com.worldventures.dreamtrips.modules.common.presenter.TermsConditionsDialogPresenter;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ConfigChangesAwareComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.Player360Activity;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.common.view.activity.TransparentComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.adapter.DraggableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragmentWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.dialog.MessageDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.dialog.ProgressDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.dialog.TermsConditionsDialog;
import com.worldventures.dreamtrips.modules.common.view.horizontal_photo_view.cell.StatefulPhotoCell;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlModule;
import com.worldventures.dreamtrips.modules.feed.view.activity.FeedActivity;
import com.worldventures.dreamtrips.modules.media_picker.presenter.MediaPickerPresenter;
import com.worldventures.dreamtrips.modules.media_picker.view.fragment.MediaPickerFragment;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.picker.MediaPickerModule;
import com.worldventures.dreamtrips.modules.player.PodcastPlayerActivity;
import com.worldventures.dreamtrips.modules.player.presenter.PodcastPlayerPresenter;
import com.worldventures.dreamtrips.modules.video.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.wallet.di.WalletAppModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = {
            MediaPickerModule.class
      },
      injects = {
            ActivityPresenter.class,
            LaunchActivityPresenter.class,
            MainActivityPresenter.class,
            PlayerPresenter.class,
            Presenter.class, SharePresenter.class,
            TermsConditionsDialogPresenter.class,
            TermsConditionsDialog.class,
            LaunchActivity.class,
            MainActivity.class,
            FeedActivity.class,
            PlayerActivity.class,
            ShareFragment.class,
            Player360Activity.class,
            FilterableArrayListAdapter.class,
            DraggableArrayListAdapter.class,
            PresentationVideosPresenter.class,
            MessengerActivity.class,
            PodcastPlayerActivity.class,
            PodcastPlayerPresenter.class,
            DtlActivity.class,
            ComponentActivity.class,
            TransparentComponentActivity.class,
            ConfigChangesAwareComponentActivity.class,
            ComponentPresenter.class,
            CopyFileCommand.class,
            ProgressDialogFragment.class,
            MessageDialogFragment.class,
            PhotoPickerLayout.class,
            DialogFragmentNavigator.NavigationDialogFragment.class,
            BaseImageFragment.class,
            BaseImagePresenter.class,
            BaseDialogFragmentWithPresenter.class,
            ToolbarPresenter.class,
            MediaPickerFragment.class,
            MediaPickerPresenter.class,
            StatefulPhotoCell.class
      },
      complete = false,
      library = true)
public class CommonModule {

   @Provides
   RootComponentsProvider provideRootComponentsProvider(Set<ComponentDescription> descriptions, ComponentsConfig config) {
      return new RootComponentsProvider(descriptions, config);
   }

   @Provides
   ComponentsConfig provideComponentsConfig(FeatureManager featureManager, @ForActivity Context context) {
      List<String> activeComponents = new ArrayList<>();
      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(SocialAppModule.FEED));
      featureManager.with(Feature.TRIPS, () -> activeComponents.add(SocialAppModule.TRIPS));
      if (!ViewUtils.isTablet(context)) {
         featureManager.with(Feature.WALLET, () -> activeComponents.add(WalletAppModule.WALLET));
      }
      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(SocialAppModule.NOTIFICATIONS));
      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(MessengerModule.MESSENGER));
      featureManager.with(Feature.DTL, () -> activeComponents.add(DtlModule.DTL));
      featureManager.with(Feature.BOOK_TRAVEL, () -> activeComponents.add(SocialAppModule.OTA));
      activeComponents.add(SocialAppModule.TRIP_IMAGES);
      featureManager.with(Feature.MEMBERSHIP, () -> activeComponents.add(SocialAppModule.MEMBERSHIP));
      activeComponents.add(SocialAppModule.BUCKETLIST);
      activeComponents.add(SocialAppModule.ACCOUNT_PROFILE);
      featureManager.with(Feature.REP_TOOLS, () -> activeComponents.add(SocialAppModule.REP_TOOLS));
      activeComponents.add(SocialAppModule.SEND_FEEDBACK);
      activeComponents.add(SocialAppModule.SETTINGS);
      activeComponents.add(SocialAppModule.HELP);
      activeComponents.add(SocialAppModule.TERMS);
      activeComponents.add(SocialAppModule.MAP_TRIPS);
      activeComponents.add(SocialAppModule.LOGOUT);
      return new ComponentsConfig(activeComponents);
   }

   @Provides
   @Singleton
   PickImageDelegate pickImageDelegate(ActivityRouter activityRouter, MediaInteractor mediaInteractor) {
      return new PickImageDelegate(activityRouter, mediaInteractor);
   }

   @Provides
   @Singleton
   NavigationDrawerPresenter provideNavDrawerPresenter(@ForApplication Injector injector) {
      return new NavigationDrawerPresenter(injector);
   }

   @Provides
   @Singleton
   PhotoPickerLayoutDelegate providePhotoPickerLayoutDelegate(BackStackDelegate backStackDelegate) {
      return new PhotoPickerLayoutDelegate(backStackDelegate);
   }
}
