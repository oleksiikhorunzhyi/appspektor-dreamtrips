package com.worldventures.dreamtrips.modules.profile.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.events.OpenFriendPrefsEvent;
import com.worldventures.dreamtrips.modules.friends.events.StartSingleChatEvent;
import com.worldventures.dreamtrips.modules.friends.events.UnfriendEvent;

import de.greenrobot.event.EventBus;

public class FriendActionDialogDelegate {

   Context context;
   EventBus eventBus;

   public FriendActionDialogDelegate(Context context, EventBus eventBus) {
      this.context = context;
      this.eventBus = eventBus;
   }

   public void showFriendDialog(User user, Drawable profileIcon) {
      showFriendSettingsDialog(user, profileIcon, context.getResources()
            .getStringArray(R.array.friend_settings_dialog));
   }

   public void showFriendDialogSkipChat(User user, Drawable profileIcon) {
      showFriendSettingsDialog(user, profileIcon, context.getResources()
            .getStringArray(R.array.friend_settings_dialog_skip_chat));
   }

   public void showFriendSettingsDialog(User user, Drawable profileIcon, String... items) {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
               eventBus.post(new UnfriendEvent(user));
               TrackingHelper.tapMyFriendsButtonFeed(TrackingHelper.ATTRIBUTE_UNFRIEND);
            });
            break;
         case 1:
            eventBus.post(new OpenFriendPrefsEvent(user));
            break;
         case 2:
            eventBus.post(new StartSingleChatEvent(user));
            break;
      }
   }

   private void showConfirmationDialog(DialogInterface.OnClickListener accept) {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setTitle(context.getString(R.string.social_unfriend_confirmation_title));
      builder.setPositiveButton(context.getString(R.string.social_unfriend_confiramation_accept), accept);
      builder.setNegativeButton(context.getString(R.string.social_unfriend_confiramation_cancel), null);
      builder.show();
   }
}
