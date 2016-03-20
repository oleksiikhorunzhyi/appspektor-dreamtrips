package com.messenger.ui.presenter;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import android.view.MenuItem;

import com.messenger.delegate.ConversationAvatarDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.delegate.CropImageDelegate;
import com.messenger.ui.view.settings.GroupChatSettingsScreen;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

public class MultiChatSettingsScreenPresenter extends ChatSettingsScreenPresenterImpl<GroupChatSettingsScreen> {

    private static final int ASPECT_RATIO_AVATAR_X = 1;
    private static final int ASPECT_RATIO_AVATAR_Y = 1;

    @Inject
    CropImageDelegate cropImageDelegate;
    @Inject
    ConversationAvatarDelegate conversationAvatarDelegate;

    public MultiChatSettingsScreenPresenter(Context context, String conversationId) {
        super(context, conversationId);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        cropImageDelegate.setAspectRatio(ASPECT_RATIO_AVATAR_X, ASPECT_RATIO_AVATAR_Y);
        getView().getAvatarImagesStream().subscribe(cropImageDelegate::cropImage);
        Observable.combineLatest(cropImageDelegate.getCroppedImagesStream(),
                conversationObservable.first(),
                (image, conversation) -> new Pair<>(conversation, image))
                .compose(bindView())
                .subscribe(pair -> onAvatarCropped(pair.first, pair.second),
                getErrorAction("Could not crop avatar image"));
        conversationObservable.first().subscribe(conversation -> {
            conversationAvatarDelegate
                    .listenToAvatarUpdates(conversation)
                    .compose(bindView())
                    .subscribe(getView()::setConversation, e -> {
                        Timber.e(e, "Cannot refresh conversation avatar");
                        getView().showErrorDialog(R.string.chat_settings_error_changing_avatar_subject);
                    });
        });
    }

    protected void onAvatarCropped(DataConversation conversation, File croppedAvatarFile) {
        String path = Uri.fromFile(croppedAvatarFile).toString();
        conversation.setAvatar(path);
        // TODO do this from updates from ConversationDAO
        getView().setConversation(conversation);
        conversationAvatarDelegate.saveAvatar(conversation);
}

    protected void onRemoveAvatar() {
        conversationObservable.first().subscribe(conversationAvatarDelegate::removeAvatar);
    }

    private Action1<Throwable> getErrorAction(String logMessage) {
        return e -> {
            Timber.w(e, logMessage);
            getView().showErrorDialog(R.string.chat_settings_error_changing_avatar_subject);
            conversationObservable.first().subscribe(conversation -> {
                conversation.setAvatar(null);
                conversationsDAO.save(conversation);
                getView().setConversation(conversation);
            });
        };
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
                getView().hideAvatarPhotoPicker();
                onRemoveAvatar();
                return true;

        }
        return super.onToolbarMenuItemClick(item);
    }
}
