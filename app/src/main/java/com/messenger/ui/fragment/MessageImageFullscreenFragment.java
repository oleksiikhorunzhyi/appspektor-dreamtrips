package com.messenger.ui.fragment;

import android.os.Bundle;

import com.messenger.entities.PhotoAttachment;
import com.messenger.ui.presenter.MessageImageFullscreenPresenter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.FullScreenPhotoFragment;

@Layout(R.layout.fragment_fullscreen_message_photo)
public class MessageImageFullscreenFragment extends FullScreenPhotoFragment<MessageImageFullscreenPresenter, PhotoAttachment>
                                            implements MessageImageFullscreenPresenter.View {

    @Override
    protected MessageImageFullscreenPresenter createPresenter(Bundle savedInstanceState) {
        return new MessageImageFullscreenPresenter((PhotoAttachment) getArgs().getPhoto(), getArgs().getType());
    }

}
