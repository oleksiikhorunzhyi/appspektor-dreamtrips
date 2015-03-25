package com.worldventures.dreamtrips.modules.facebook;

import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookAlbumPresenter;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPhotoPresenter;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPickPhotoPresenter;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FacebookPickPhotoActivity;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookAlbumItem;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookPhotoItem;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookPhotoFragment;

import dagger.Module;

@Module(
        injects = {
                FacebookPickPhotoPresenter.class,
                FacebookAlbumPresenter.class,
                FacebookPhotoPresenter.class,
                FacebookAlbumFragment.class,
                FacebookPhotoFragment.class,
                FacebookAlbumItem.class,
                FacebookPhotoItem.class,
                FacebookPickPhotoActivity.class,
        },
        complete = false,
        library = true
)
public class FacebookModule {
}
