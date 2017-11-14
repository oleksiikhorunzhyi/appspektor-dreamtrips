package com.messenger.delegate;

import com.messenger.entities.DataUser;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;

import static com.worldventures.dreamtrips.core.module.FragmentClassProviderModule.PROFILE;


public class ProfileCrosser {

   private final FragmentClassProvider<Integer> fragmentClassProvider;
   private final Router router;

   @Inject
   public ProfileCrosser(Router router, @Named(PROFILE) FragmentClassProvider<Integer> fragmentClassProvider) {
      this.router = router;
      this.fragmentClassProvider = fragmentClassProvider;
   }

   public void crossToProfile(DataUser user) {
      User socialUser = new User(user.getSocialId());
      router.moveTo(fragmentClassProvider.provideFragmentClass(socialUser.getId()), NavigationConfigBuilder.forActivity()
            .data(new UserBundle(socialUser))
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build());
   }
}
