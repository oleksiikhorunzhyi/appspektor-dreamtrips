package com.messenger.ui.presenter;

import android.content.Context;
import android.view.MenuItem;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.messenger.ui.view.settings.GroupChatSettingsScreen;
import com.worldventures.dreamtrips.R;

import java.sql.Time;

import rx.Observable;
import timber.log.Timber;

public class MultiChatSettingsScreenPresenter extends ChatSettingsScreenPresenterImpl<GroupChatSettingsScreen> {

    public MultiChatSettingsScreenPresenter(Context context, String conversationId) {
        super(context, conversationId);
    }

    @Override
    public void onConversationAvatarClick() {

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Observable.combineLatest(getView().getAvatarImagesStream(), conversationObservable.first(),
                ((chosenImage1, conversation) -> {
                    Timber.d("Conversation avatar picked %s", chosenImage1.getFileThumbnail());
                    conversation.setAvatar(chosenImage1.getFileThumbnail());
                    return conversation;
                }))
                .subscribe(conversation -> {
                    Timber.d("Conversation avatar set %s", conversation.getAvatar());
                    getView().setConversation(conversation);
                });
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_chat_avatar:
                getView().showAvatarPhotoPicker();
                return true;
            case R.id.action_remove_chat_avatar:
                onRemoveGroupChatAvatar();
                return true;

        }
        return super.onToolbarMenuItemClick(item);
    }

    protected void onRemoveGroupChatAvatar() {

    }
}
