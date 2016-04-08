package com.messenger.ui.presenter;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import android.view.MenuItem;

import com.messenger.delegate.ConversationAvatarDelegate;
import com.messenger.delegate.CropImageDelegate;
import com.messenger.delegate.actions.AvatarAction;
import com.messenger.entities.DataConversation;
import com.messenger.ui.view.settings.GroupChatSettingsScreen;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Notification;
import rx.Observable;
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
            conversationObservable.first(),
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

        conversationAvatarDelegate.listenToAvatarUpdates(conversationId)
                .compose(bindView())
                .doOnNext(state -> getView().invalidateToolbarMenu())
                .subscribe(getUploadAvatarSubscriber());

        conversationsDAO.getConversation(conversationId)
                .compose(bindViewIoToMainComposer())
                .compose(new NonNullFilter<>())
                .subscribe(getView()::setConversation);
    }

    private ActionStateSubscriber<AvatarAction> getUploadAvatarSubscriber() {
        return new ActionStateSubscriber<AvatarAction>()
                .beforeEach(state -> {
                    if (state.status == ActionState.Status.START
                        || state.status == ActionState.Status.PROGRESS) {
                        getView().showChangingAvatarProgressBar();
                    } else {
                        getView().hideChangingAvatarProgressBar();
                    }
                })
                .onSuccess(action -> {
                    conversationAvatarDelegate.clearReplays();
                    getView().setConversation(action.getResult());
                    getView().invalidateToolbarMenu();
                })
                .onFail((state, error) -> {
                    conversationAvatarDelegate.clearReplays();
                    getView().showErrorDialog(R.string.chat_settings_error_changing_avatar_subject);

                });
    }

    protected void onAvatarCropped(DataConversation conversation, File croppedAvatarFile) {
        String path = Uri.fromFile(croppedAvatarFile).toString();
        conversationAvatarDelegate.saveAvatar(conversation, path);
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
