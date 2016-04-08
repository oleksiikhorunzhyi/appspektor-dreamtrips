package com.messenger.ui.presenter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;

import com.messenger.delegate.ConversationAvatarDelegate;
import com.messenger.delegate.CropImageDelegate;
import com.messenger.delegate.command.ChangeAvatarCommand;
import com.messenger.entities.DataConversation;
import com.messenger.ui.view.settings.GroupChatSettingsScreen;
import com.techery.spares.module.Injector;
import com.messenger.ui.viewstate.ChatSettingsViewState;
import com.messenger.ui.viewstate.ChatSettingsViewState.UploadingState;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Notification;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class MultiChatSettingsScreenPresenter extends ChatSettingsScreenPresenterImpl<GroupChatSettingsScreen> {

    @Inject
    CropImageDelegate cropImageDelegate;

    @Inject
    ConversationAvatarDelegate conversationAvatarDelegate;

    public MultiChatSettingsScreenPresenter(Context context, Injector injector, String conversationId) {
        super(context, injector, conversationId);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        TrackingHelper.groupSettingsOpened();

        getView().getAvatarImagesStream().subscribe(cropImageDelegate::cropImage);

        Observable.combineLatest(
                cropImageDelegate.getCroppedImagesStream(),
                conversationObservable.take(1),
                (image, conversation) -> new Pair<>(conversation, image))
                .compose(bindView())
                .subscribe(pair -> {
                    DataConversation conversation = pair.first;
                    Notification<File> notification = pair.second;
                    if (notification.isOnNext()) {
                        onAvatarCropped(conversation, notification.getValue());
                    } else if (notification.isOnError()) {
                        Timber.w(notification.getThrowable(), "Could not crop image");
                        getView().showErrorDialog(R.string.chat_settings_error_changing_avatar_subject);
                    }
                });

        conversationAvatarDelegate.getReadChangeAvatarCommandActionPipe()
                .observe()
                .compose(bindView())
                .compose(new ActionStateToActionTransformer<>())
                .map(ChangeAvatarCommand::getConversation)
                .filter(conversation -> TextUtils.equals(conversation.getId(), conversationId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onChangeAvatarSuccess, this::onChangeAvatarFailed);
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

    protected void onAvatarCropped(DataConversation conversation, File croppedAvatarFile) {
        String path = Uri.fromFile(croppedAvatarFile).toString();
        //noinspection ConstantConditions
        getView().showChangingAvatarProgressBar();
        getViewState().setUploadAvatar(UploadingState.UPLOADING);
        conversationAvatarDelegate.setAvatarToConversation(conversation, path);
    }

    protected void onChangeAvatarSuccess(DataConversation conversation) {
        getViewState().setUploadAvatar(UploadingState.UPLOADED);
        GroupChatSettingsScreen screen = getView();
        if (screen != null) {
            screen.setConversation(conversation);
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
        conversationObservable
                .take(1)
                .subscribe(conversationAvatarDelegate::removeAvatar);
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
