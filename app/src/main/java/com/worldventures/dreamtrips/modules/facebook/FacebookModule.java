package com.worldventures.dreamtrips.modules.facebook;

import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookAlbumFragmentPM;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPhotoFragmentPM;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPickPhotoActivityPM;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FBPickPhotoActivity;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookAlbumItem;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookPhotoItem;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookPhotoFragment;

import dagger.Module;

/**
 * Created by 1 on 23.03.15.
 */
@Module(
        injects = {
                FacebookPickPhotoActivityPM.class,
                FacebookAlbumFragmentPM.class,
                FacebookPhotoFragmentPM.class,
                FacebookAlbumFragment.class,
                FacebookPhotoFragment.class,
                FacebookAlbumItem.class,
                FacebookPhotoItem.class,
                FBPickPhotoActivity.class,
        },
        complete = false,
        library = true
)
public class FacebookModule {
}
