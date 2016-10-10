package com.worldventures.dreamtrips.modules.common;

import android.content.Context;

import com.messenger.di.MessengerActivityModule;
import com.messenger.ui.activity.MessengerActivity;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
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
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.MediaPickerPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.SharePresenter;
import com.worldventures.dreamtrips.modules.common.presenter.TermsConditionsDialogPresenter;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.Player360Activity;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.common.view.adapter.DraggableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragmentWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.dialog.MessageDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.dialog.ProgressDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.dialog.TermsConditionsDialog;
import com.worldventures.dreamtrips.modules.common.view.fragment.MediaPickerFragment;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlActivityModule;
import com.worldventures.dreamtrips.modules.feed.FeedModule;
import com.worldventures.dreamtrips.modules.infopages.InfoModule;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.player.PodcastPlayerActivity;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.reptools.ReptoolsModule;
import com.worldventures.dreamtrips.modules.settings.SettingsModule;
import com.worldventures.dreamtrips.modules.trips.TripsModule;
import com.worldventures.dreamtrips.modules.tripsimages.TripsImagesModule;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.modules.video.VideoModule;
import com.worldventures.dreamtrips.modules.video.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.wallet.di.WalletActivityModule;
import com.worldventures.dreamtrips.wallet.ui.WalletActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            ActivityPresenter.class,
            LaunchActivityPresenter.class,
            MainActivityPresenter.class,
            Presenter.class, SharePresenter.class,
            TermsConditionsDialogPresenter.class,
            TermsConditionsDialog.class,

            LaunchActivity.class,
            MainActivity.class,
            PlayerActivity.class,
            ShareFragment.class,
            Player360Activity.class,
            FilterableArrayListAdapter.class,
            DraggableArrayListAdapter.class,
            PresentationVideosPresenter.class,
            MessengerActivity.class,
            PodcastPlayerActivity.class,
            DtlActivity.class,
            ComponentActivity.class,
            ComponentPresenter.class,
            CopyFileCommand.class,
            ProgressDialogFragment.class,
            MessageDialogFragment.class,
            PhotoPickerLayout.class,
            WalletActivity.class,

            DialogFragmentNavigator.NavigationDialogFragment.class,
            BaseImageFragment.class,
            BaseImagePresenter.class,
            BaseDialogFragmentWithPresenter.class,
            //
            ToolbarPresenter.class,
            //
            MediaPickerFragment.class,
            MediaPickerPresenter.class
      },
      complete = false,
      library = true)
public class CommonModule {

   public static final String LOGOUT = "Logout";

   @Provides
   RootComponentsProvider provideRootComponentsProvider(Set<ComponentDescription> descriptions, ComponentsConfig config) {
      return new RootComponentsProvider(descriptions, config);
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideLogoutComponent() {
      return new ComponentDescription(LOGOUT, 0, R.string.logout_component, R.drawable.ic_logout, null);
   }

   @Provides
   ComponentsConfig provideComponentsConfig(FeatureManager featureManager, @ForActivity Context context) {
      List<String> activeComponents = new ArrayList<>();

      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(FeedModule.FEED));

      featureManager.with(Feature.TRIPS, () -> activeComponents.add(TripsModule.TRIPS));

      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(FeedModule.NOTIFICATIONS));
      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(MessengerActivityModule.MESSENGER));
      featureManager.with(Feature.DTL, () -> activeComponents.add(DtlActivityModule.DTL));
      featureManager.with(Feature.BOOK_TRAVEL, () -> activeComponents.add(TripsModule.OTA));

      if (!ViewUtils.isTablet(context)) {
         featureManager.with(Feature.WALLET, () -> activeComponents.add(WalletActivityModule.WALLET));
      }

      activeComponents.add(TripsImagesModule.TRIP_IMAGES);
      featureManager.with(Feature.MEMBERSHIP, () -> activeComponents.add(VideoModule.MEMBERSHIP));

      activeComponents.add(BucketListModule.BUCKETLIST);
      activeComponents.add(ProfileModule.MY_PROFILE);

      featureManager.with(Feature.REP_TOOLS, () -> activeComponents.add(ReptoolsModule.REP_TOOLS));

      activeComponents.add(InfoModule.SEND_FEEDBACK);

      activeComponents.add(SettingsModule.SETTINGS);

      activeComponents.add(InfoModule.HELP);
      activeComponents.add(InfoModule.TERMS);

      activeComponents.add(TripsModule.MAP_TRIPS);

      activeComponents.add(LOGOUT);

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
