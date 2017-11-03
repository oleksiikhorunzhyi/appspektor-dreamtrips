package com.worldventures.dreamtrips.core.navigation.creator;

import android.support.v4.app.Fragment;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.social.ui.profile.view.fragment.AccountFragment;
import com.worldventures.dreamtrips.social.ui.profile.view.fragment.UserFragment;

public class ProfileFragmentClassProvider implements FragmentClassProvider<Integer> {

   private final SessionHolder appSessionHolder;

   public ProfileFragmentClassProvider(SessionHolder appSessionHolder) {
      this.appSessionHolder = appSessionHolder;
   }

   @Override
   public Class<? extends Fragment> provideFragmentClass(Integer arg) {
      Optional<UserSession> userSessionOptional = appSessionHolder.get();
      if (userSessionOptional.isPresent()) {
         if (arg == userSessionOptional.get().user().getId()) {
            return AccountFragment.class;
         } else {
            return UserFragment.class;
         }
      }
      return null;
   }
}
