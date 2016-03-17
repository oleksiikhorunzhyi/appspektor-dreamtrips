package com.messenger.ui.presenter;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import android.view.MenuItem;

import com.messenger.entities.DataConversation;
import com.messenger.ui.util.avatar.CropImageDelegate;
import com.messenger.ui.view.settings.GroupChatSettingsScreen;
import com.worldventures.dreamtrips.R;

import java.io.File;

import javax.inject.Inject;

import timber.log.Timber;

public class MultiChatSettingsScreenPresenter extends ChatSettingsScreenPresenterImpl<GroupChatSettingsScreen> {

    private static final int ASPECT_RATIO_AVATAR_X = 1;
    private static final int ASPECT_RATIO_AVATAR_Y = 1;

    @Inject
    CropImageDelegate cropImageDelegate;

    public MultiChatSettingsScreenPresenter(Context context, String conversationId) {
        super(context, conversationId);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        cropImageDelegate.setAspectRatio(ASPECT_RATIO_AVATAR_X, ASPECT_RATIO_AVATAR_Y);
        getView().getAvatarImagesStream().subscribe(cropImageDelegate::cropImage);
        cropImageDelegate.getCroppedImagesStream().
                zipWith(conversationObservable, (image, conversation) -> new Pair<>(conversation, image))
                .subscribe(pair -> onAvatarCropped(pair.first, pair.second),
                e -> Timber.w(e, "Could not crop avatar"));
    }

    protected void onAvatarCropped(DataConversation conversation, File croppedAvatarFile) {
        conversation.setAvatar(Uri.fromFile(croppedAvatarFile).toString());
        getView().setConversation(conversation);
    }

    protected void onRemoveAvatar() {
        // TODO check if supported on server and implement
    }

    @Override
    public void onConversationAvatarClick() {
        // nothing to do
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_chat_avatar:
                getView().showAvatarPhotoPicker();
                return true;
            case R.id.action_remove_chat_avatar:
                onRemoveAvatar();
                return true;

        }
        return super.onToolbarMenuItemClick(item);
    }
}
