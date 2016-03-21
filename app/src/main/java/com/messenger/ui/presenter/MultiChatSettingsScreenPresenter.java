package com.messenger.ui.presenter;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import android.view.MenuItem;

import com.messenger.delegate.ConversationAvatarDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.delegate.CropImageDelegate;
import com.messenger.ui.view.settings.GroupChatSettingsScreen;
import com.worldventures.dreamtrips.R;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class MultiChatSettingsScreenPresenter extends ChatSettingsScreenPresenterImpl<GroupChatSettingsScreen> {

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
        getView().getAvatarImagesStream().subscribe(cropImageDelegate::cropImage);
        Observable.combineLatest(
            cropImageDelegate.getCroppedImagesStream(),
            conversationObservable.first(),
            (image, conversation) -> new Pair<>(conversation, image))
            .compose(bindView())
            .subscribe(pair -> onAvatarCropped(pair.first, pair.second),
                e -> {
                    Timber.w(e, "Could not crop image");
                    getView().showErrorDialog(R.string.chat_settings_error_changing_avatar_subject);
                }
            );

        conversationObservable.first().subscribe(conversation -> {
            conversationAvatarDelegate.listenToAvatarUpdates(conversation)
                .compose(bindView())
                .subscribe(
                   new ActionStateSubscriber<ConversationAvatarDelegate.BaseAvatarAction>()
                   .onSuccess(state -> getView().setConversation(state.getResult()))
                   .onFail((state, error) -> getView()
                           .showErrorDialog(R.string.chat_settings_error_changing_avatar_subject))
                );
        });

        conversationsDAO.getConversation(conversationId)
                .compose(bindViewIoToMainComposer())
                .subscribe(getView()::setConversation);
    }

    protected void onAvatarCropped(DataConversation conversation, File croppedAvatarFile) {
        String path = Uri.fromFile(croppedAvatarFile).toString();
        conversation.setAvatar(path);
        conversationAvatarDelegate.saveAvatar(conversation);
    }

    protected void onRemoveAvatar() {
        conversationAvatarDelegate.removeAvatar(conversationObservable.toBlocking().first());
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
