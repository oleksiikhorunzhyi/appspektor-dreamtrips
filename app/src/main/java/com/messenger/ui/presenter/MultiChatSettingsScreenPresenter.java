package com.messenger.ui.presenter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.MenuItem;

import com.messenger.delegate.ConversationAvatarDelegate;
import com.messenger.delegate.CropImageDelegate;
import com.messenger.delegate.command.ChangeAvatarCommand;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.view.settings.GroupChatSettingsScreen;
import com.messenger.ui.viewstate.ChatSettingsViewState;
import com.messenger.ui.viewstate.ChatSettingsViewState.UploadingState;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class MultiChatSettingsScreenPresenter extends ChatSettingsScreenPresenterImpl<GroupChatSettingsScreen> {

    @Inject CropImageDelegate cropImageDelegate;
    @Inject ConversationAvatarDelegate conversationAvatarDelegate;
    @Inject PermissionDispatcher permissionDispatcher;

    public MultiChatSettingsScreenPresenter(Context context, Injector injector, String conversationId) {
        super(context, injector, conversationId);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        TrackingHelper.groupSettingsOpened();

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
    protected void onConversationChanged(DataConversation conversation, DataUser owner, List<DataUser> participants) {
        super.onConversationChanged(conversation, owner, participants);
        getView().setLeaveButtonVisible(ConversationHelper.isPresent(conversation)
                && !ConversationHelper.isOwner(conversation, currentUser));
    }

    private boolean filterActionState(ActionState<ChangeAvatarCommand> commandActionState) {
        return TextUtils.equals(commandActionState.action.getConversationId(), conversationId);
    }

    @Override
    public void applyViewState() {
        super.applyViewState();
        ChatSettingsViewState viewState = getViewState();
        UploadingState uploadingState = getViewState().getUploadAvatar();
        if (uploadingState == null) return;
        if (uploadingState == UploadingState.UPLOADING) {
            getView().showChangingAvatarProgressBar();
        } else {
            getView().hideChangingAvatarProgressBar();
            viewState.setUploadAvatar(null);
        }
    }

    protected void onAvatarCropped(String conversationId, File croppedAvatarFile) {
        String path = Uri.fromFile(croppedAvatarFile).toString();
        //noinspection ConstantConditions
        getView().showChangingAvatarProgressBar();
        getViewState().setUploadAvatar(UploadingState.UPLOADING);
        conversationAvatarDelegate.setAvatarToConversation(conversationId, path);
    }

    protected void onChangeAvatarSuccess() {
        getViewState().setUploadAvatar(UploadingState.UPLOADED);
        GroupChatSettingsScreen screen = getView();
        if (screen != null) {
            screen.invalidateToolbarMenu();
            screen.hideChangingAvatarProgressBar();
        }
    }

    protected void onChangeAvatarFailed(Throwable throwable) {
        getViewState().setUploadAvatar(UploadingState.ERROR);
        Timber.e(throwable, "");
        GroupChatSettingsScreen screen = getView();
        if (screen != null) {
            screen.hideChangingAvatarProgressBar();
            screen.showErrorDialog(R.string.chat_settings_error_changing_avatar_subject);
        }
    }

    protected void onRemoveAvatar() {
        //noinspection ConstantConditions
        getView().showChangingAvatarProgressBar();
        conversationAvatarDelegate.removeAvatar(conversationId);
    }

    @Override
    public void onConversationAvatarClick() {
        // nothing to do
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

        }
        return super.onToolbarMenuItemClick(item);
    }

    public void openPicker() {
        permissionDispatcher.requestPermission(PermissionConstants.STORE_PERMISSIONS, false)
                .compose(bindView())
                .subscribe(new PermissionSubscriber()
                        .onPermissionGrantedAction(() -> getView().showAvatarPhotoPicker())
                );
    }
}
