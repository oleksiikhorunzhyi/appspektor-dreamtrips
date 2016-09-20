package com.worldventures.dreamtrips.modules.profile.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.lang.ref.WeakReference;

import rx.functions.Action1;

public class FriendActionDialogDelegate {

   private WeakReference<Context> context;
   private Action1<User> onUnfriendAction;
   private Action1<User> onFriendPrefsAction;
   private Action1<User> onStartSingleChatAction;

   public FriendActionDialogDelegate(Context context) {
      this.context = new WeakReference<>(context);
   }

   public FriendActionDialogDelegate onUnfriend(Action1<User> onUnfriendAction) {
      this.onUnfriendAction = onUnfriendAction;
      return this;
   }

   public FriendActionDialogDelegate onFriendPrefsAction(Action1<User> onFriendPrefsAction) {
      this.onFriendPrefsAction = onFriendPrefsAction;
      return this;
   }

   public FriendActionDialogDelegate onStartSingleChatAction(Action1<User> onStartSingleChatAction) {
      this.onStartSingleChatAction = onStartSingleChatAction;
      return this;
   }

   public void showFriendDialog(User user, Drawable profileIcon) {
      if (context.get() == null) return;

      showFriendSettingsDialog(user, profileIcon, context.get().getResources()
            .getStringArray(R.array.friend_settings_dialog));
   }

   public void showFriendDialogSkipChat(User user, Drawable profileIcon) {
      if (context.get() == null) return;

      showFriendSettingsDialog(user, profileIcon, context.get().getResources()
            .getStringArray(R.array.friend_settings_dialog_skip_chat));
   }

   public void showFriendSettingsDialog(User user, Drawable profileIcon, String... items) {
      if (context.get() == null) return;

      AlertDialog.Builder builder = new AlertDialog.Builder(context.get());
      builder.setTitle(user.getFullName());
      if (profileIcon != null) builder.setIcon(profileIcon);
      builder.setNegativeButton(R.string.friend_cancel, (dialogInterface, i) -> dialogInterface.dismiss());
      builder.setItems(items, (dialogInterface, i) -> processUserChoice(user, i));
      builder.show();
   }

   private void processUserChoice(User user, int actionCode) {
      switch (actionCode) {
         case 0:
            showConfirmationDialog((dialog, which) -> {
               onUnfriendAction.call(user);
               TrackingHelper.tapMyFriendsButtonFeed(TrackingHelper.ATTRIBUTE_UNFRIEND);
            });
            break;
         case 1:
            onFriendPrefsAction.call(user);
            break;
         case 2:
            onStartSingleChatAction.call(user);
            break;
      }
   }

   private void showConfirmationDialog(DialogInterface.OnClickListener accept) {
      if (context.get() == null) return;

      AlertDialog.Builder builder = new AlertDialog.Builder(context.get());
      builder.setTitle(context.get().getString(R.string.social_unfriend_confirmation_title));
      builder.setPositiveButton(context.get().getString(R.string.social_unfriend_confiramation_accept), accept);
      builder.setNegativeButton(context.get().getString(R.string.social_unfriend_confiramation_cancel), null);
      builder.show();
   }
}
