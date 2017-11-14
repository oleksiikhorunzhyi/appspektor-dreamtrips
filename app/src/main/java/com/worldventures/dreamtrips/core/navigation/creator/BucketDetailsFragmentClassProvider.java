package com.worldventures.dreamtrips.core.navigation.creator;

import android.support.v4.app.Fragment;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketDetailsFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.ForeignBucketDetailsFragment;
public class BucketDetailsFragmentClassProvider implements FragmentClassProvider<Integer> {

   private final SessionHolder appSessionHolder;

   public BucketDetailsFragmentClassProvider(SessionHolder appSessionHolder) {
      this.appSessionHolder = appSessionHolder;
   }

   @Override
   public Class<? extends Fragment> provideFragmentClass(Integer arg) {
      Optional<UserSession> userSessionOptional = appSessionHolder.get();
      if (userSessionOptional.isPresent()) {
         if (arg == null || arg == userSessionOptional.get().user().getId()) {
            return BucketDetailsFragment.class;
         } else {
            return ForeignBucketDetailsFragment.class;
         }
      }
      return null;
   }
}
