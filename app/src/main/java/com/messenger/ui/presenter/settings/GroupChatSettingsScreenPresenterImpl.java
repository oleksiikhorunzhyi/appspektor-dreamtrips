package com.messenger.ui.presenter.settings;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.delegate.ConversationAvatarInteractor;
import com.messenger.delegate.command.avatar.RemoveChatAvatarCommand;
import com.messenger.delegate.command.avatar.SendChatAvatarCommand;
import com.messenger.delegate.command.avatar.SetChatAvatarCommand;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.chat.GroupChat;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.view.settings.GroupChatSettingsScreen;
import com.messenger.ui.viewstate.ChatSettingsViewState;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.modules.picker.helper.PickerPermissionChecker;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.core.ui.util.permission.PermissionUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class GroupChatSettingsScreenPresenterImpl extends BaseGroupChatSettingsScreenPresenterImpl {

   @Inject PermissionDispatcher permissionDispatcher;
   @Inject ConversationAvatarInteractor conversationAvatarInteractor;
   @Inject PickerPermissionChecker pickerPermissionChecker;
   @Inject PermissionUtils permissionUtils;

   public GroupChatSettingsScreenPresenterImpl(Context context, Injector injector, String conversationId) {
      super(context, injector, conversationId);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      registerPermissionCallbacks();
      cropImageDelegate.getCroppedImagesStream().compose(bindView()).subscribe(notification -> {
         if (notification.isOnNext()) {
            onAvatarCropped(conversationId, notification.getValue());
         } else if (notification.isOnError()) {
            Timber.w(notification.getThrowable(), "Could not crop image");
            getView().showErrorDialog(R.string.chat_settings_error_changing_avatar_subject);
         }
      });

      conversationAvatarInteractor.getSendChatAvatarCommandPipe()
            .observe()
            .compose(bindView())
            .filter(this::filterActionState)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<SendChatAvatarCommand>().onFail((command, throwable) -> onChangeAvatarFailed(throwable))
                  .onSuccess(command -> onChangeAvatarSuccess()));
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
            pickerPermissionChecker.checkPermission();
            return true;
         case R.id.action_remove_chat_avatar:
            onRemoveAvatar();
            return true;
         case R.id.action_edit_chat_name:
            onEditChatName();
            return true;
         default:
            break;
      }
      return super.onToolbarMenuItemClick(item);
   }

   @Override
   public void applyViewState() {
      super.applyViewState();
      ChatSettingsViewState viewState = getViewState();
      ChatSettingsViewState.UploadingState uploadingState = getViewState().getUploadAvatar();
      if (uploadingState == null) {
         return;
      }
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
      conversationAvatarInteractor.getRemoveChatAvatarCommandPipe().send(new RemoveChatAvatarCommand(conversationId));
   }

   private void onEditChatName() {
      conversationObservable.map(DataConversation::getSubject)
            .subscribe(subject -> getView().showSubjectDialog(subject));
   }

   @Override
   public void applyNewChatSubject(String subject) {
      final String newSubject = subject == null ? null : subject.trim();

      Observable<GroupChat> multiUserChatObservable = facade.getChatManager()
            .createGroupChatObservable(conversationId, facade.getUsername())
            .flatMap(multiUserChat -> multiUserChat.setSubject(newSubject));

      Observable.zip(multiUserChatObservable, conversationObservable.first(), (multiUserChat, conversation) -> conversation)
            .compose(new IoToMainComposer<>())
            .subscribe(conversation -> {
               conversation.setSubject(newSubject);
               conversationsDAO.save(conversation);
            }, throwable -> {
               getView().showErrorDialog(R.string.chat_settings_error_change_subject);
            });
   }

   private boolean filterActionState(ActionState<SendChatAvatarCommand> commandActionState) {
      return TextUtils.equals(commandActionState.action.getConversationId(), conversationId);
   }

   protected void onAvatarCropped(String conversationId, File croppedAvatarFile) {
      String path = Uri.fromFile(croppedAvatarFile).toString();
      //noinspection ConstantConditions
      getView().showChangingAvatarProgressBar();
      getViewState().setUploadAvatar(ChatSettingsViewState.UploadingState.UPLOADING);
      conversationAvatarInteractor.getSetChatAvatarCommandPipe().send(new SetChatAvatarCommand(conversationId, path));
   }

   protected void onChangeAvatarSuccess() {
      getViewState().setUploadAvatar(ChatSettingsViewState.UploadingState.UPLOADED);
      GroupChatSettingsScreen screen = getView();
      if (screen != null) {
         screen.invalidateToolbarMenu();
         screen.hideChangingAvatarProgressBar();
      }
   }

   protected void onChangeAvatarFailed(Throwable throwable) {
      getViewState().setUploadAvatar(ChatSettingsViewState.UploadingState.ERROR);
      Timber.e(throwable, "");
      GroupChatSettingsScreen screen = getView();
      if (screen != null) {
         screen.hideChangingAvatarProgressBar();
         screen.showErrorDialog(R.string.chat_settings_error_changing_avatar_subject);
      }
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      cropImageDelegate.destroy();
   }

   private void registerPermissionCallbacks() {
      pickerPermissionChecker.registerCallback(
            () -> {
               if (getView() != null) {
                  getView().openPicker();
               }
            }, () -> {
               if (getView() != null) {
                  getView().showPermissionDenied(PickerPermissionChecker.PERMISSIONS);
               }
            }, () -> {
               if (getView() != null) {
                  getView().showPermissionExplanationText(PickerPermissionChecker.PERMISSIONS);
               }
            });
   }

   public void recheckPermission(String[] permissions, boolean userAnswer) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionChecker.recheckPermission(userAnswer);
      }
   }
}
