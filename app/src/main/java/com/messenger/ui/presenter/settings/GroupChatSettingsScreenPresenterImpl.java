package com.messenger.ui.presenter.settings;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.delegate.ConversationAvatarDelegate;
import com.messenger.delegate.CropImageDelegate;
import com.messenger.delegate.command.ChangeAvatarCommand;
import com.messenger.messengerservers.chat.GroupChat;
import com.messenger.synchmechanism.SyncStatus;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.viewstate.ChatSettingsViewState;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class GroupChatSettingsScreenPresenterImpl extends BaseGroupChatSettingsScreenPresenter {

    @Inject CropImageDelegate cropImageDelegate;
    @Inject PermissionDispatcher permissionDispatcher;
    @Inject ConversationAvatarDelegate conversationAvatarDelegate;

    public GroupChatSettingsScreenPresenterImpl(Context context, Injector injector, String conversationId) {
        super(context, injector, conversationId);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getView().getAvatarImagePathsStream().subscribe(cropImageDelegate::cropImage);

        cropImageDelegate.getCroppedImagesStream()
                .compose(bindView())
                .subscribe(notification -> {
                    if (notification.isOnNext()) {
                        onAvatarCropped(conversationId, notification.getValue());
                    } else if (notification.isOnError()) {
                        Timber.w(notification.getThrowable(), "Could not crop image");
                        getView().showErrorDialog(R.string.chat_settings_error_changing_avatar_subject);
                    }
                });

        conversationAvatarDelegate.getReadChangeAvatarCommandActionPipe()
                .observe()
                .compose(bindView())
                .filter(this::filterActionState)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ActionStateSubscriber<ChangeAvatarCommand>()
                        .onFail((command, throwable) -> onChangeAvatarFailed(throwable))
                        .onSuccess(command -> onChangeAvatarSuccess())
                );
    }

    @Override
    public void onToolbarMenuPrepared(Menu menu) {
        conversationsDAO.getConversation(conversationId)
                .compose(bindViewIoToMainComposer())
                .take(1)
                .subscribe(conversation -> {
                    if (!ConversationHelper.isOwner(conversation, currentUser)) {
                        menu.findItem(R.id.action_overflow).setVisible(false);
                        return;
                    }

                    if (TextUtils.isEmpty(conversation.getAvatar())) {
                        menu.findItem(R.id.action_remove_chat_avatar).setVisible(false);
                    }
                });
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_chat_avatar:
                openPicker();
                return true;
            case R.id.action_remove_chat_avatar:
                getView().hideAvatarPhotoPicker();
                onRemoveAvatar();
                return true;
            case R.id.action_edit_chat_name:
                onEditChatName();
                return true;

        }
        return super.onToolbarMenuItemClick(item);
    }

    @Override
    public void applyViewState() {
        super.applyViewState();
        ChatSettingsViewState viewState = getViewState();
        ChatSettingsViewState.UploadingState uploadingState = getViewState().getUploadAvatar();
        if (uploadingState == null) return;
        if (uploadingState == ChatSettingsViewState.UploadingState.UPLOADING) {
            getView().showChangingAvatarProgressBar();
        } else {
            getView().hideChangingAvatarProgressBar();
            viewState.setUploadAvatar(null);
        }
    }

    protected void onRemoveAvatar() {
        //noinspection ConstantConditions
        getView().showChangingAvatarProgressBar();
        conversationAvatarDelegate.removeAvatar(conversationId);
    }

    private void onEditChatName() {
        conversationObservable
                .map(conversation -> conversation.getSubject())
                .subscribe(subject -> getView().showSubjectDialog(subject));
    }

    public void openPicker() {
        permissionDispatcher.requestPermission(PermissionConstants.STORE_PERMISSIONS, false)
                .compose(bindView())
                .subscribe(new PermissionSubscriber()
                        .onPermissionGrantedAction(() -> getView().showAvatarPhotoPicker())
                );
    }

    @Override
    public void applyNewChatSubject(String subject) {
        final String newSubject = subject == null ? null : subject.trim();

        Observable<GroupChat> multiUserChatObservable = facade.getChatManager()
                .createGroupChatObservable(conversationId, facade.getUsername())
                .flatMap(multiUserChat -> multiUserChat.setSubject(newSubject));

        Observable.zip(multiUserChatObservable, conversationObservable.first(),
                (multiUserChat, conversation) -> conversation)
                .compose(new IoToMainComposer<>())
                .subscribe(conversation -> {
                    conversation.setSubject(newSubject);
                    conversationsDAO.save(conversation);
                }, throwable -> {
                    getView().showErrorDialog(R.string.chat_settings_error_change_subject);
                });
    }

    private boolean filterActionState(ActionState<ChangeAvatarCommand> commandActionState) {
        return TextUtils.equals(commandActionState.action.getConversationId(), conversationId);
    }

    protected void onAvatarCropped(String conversationId, File croppedAvatarFile) {
        String path = Uri.fromFile(croppedAvatarFile).toString();
        //noinspection ConstantConditions
        getView().showChangingAvatarProgressBar();
        getViewState().setUploadAvatar(ChatSettingsViewState.UploadingState.UPLOADING);
        // delay setting avatar till sync is finished to avoid scenario when its value in
        // our database is overriden by cached data from sync
        connectionStatusStream
                .filter(status -> status == SyncStatus.CONNECTED)
                .take(1)
                .subscribe(syncStatus -> {
                    conversationAvatarDelegate.setAvatarToConversation(conversationId, path);
                });
    }

}
