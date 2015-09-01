package com.worldventures.dreamtrips.modules.profile.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.events.OpenFriendPrefsEvent;
import com.worldventures.dreamtrips.modules.friends.events.UnfriendEvent;

import de.greenrobot.event.EventBus;

public class FriendActionDialogDelegate {

    Context c;
    EventBus eventBus;

    public FriendActionDialogDelegate(Context c, EventBus eventBus) {
        this.c = c;
        this.eventBus = eventBus;
    }

    public void showFriendDialog(User user, Drawable profileIcon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(user.getFullName());
        if (profileIcon != null)
            builder.setIcon(profileIcon);
        builder.setNegativeButton(R.string.friend_cancel, (dialogInterface, i) ->
                dialogInterface.dismiss());
        builder.setItems(new String[]{
                        c.getString(R.string.social_remove_friend_title),
                        c.getString(R.string.social_friend_preference_title)
                },
                (dialogInterface, i) -> {
                    if (i == 0) {
                        showConfirmationDialog((dialog, which) -> eventBus.post(new UnfriendEvent(user)));
                    } else if (i == 1) {
                        eventBus.post(new OpenFriendPrefsEvent(user));
                    }
                }
        );
        builder.show();

    }

    private void showConfirmationDialog(DialogInterface.OnClickListener accept) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(c.getString(R.string.social_unfriend_confirmation_title));
        builder.setPositiveButton(c.getString(R.string.social_unfriend_confiramation_accept), accept);
        builder.setNegativeButton(c.getString(R.string.social_unfriend_confiramation_cancel), null);
        builder.show();
    }
}
