package com.worldventures.dreamtrips.modules.facebook;

import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookAlbumPresenter;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPhotoPresenter;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookPhotoFragment;

import dagger.Module;

@Module(
      injects = {
            FacebookAlbumPresenter.class,
            FacebookPhotoPresenter.class,
            FacebookAlbumFragment.class,
            FacebookPhotoFragment.class,
      },
      complete = false,
      library = true)
public class FacebookModule {
}
