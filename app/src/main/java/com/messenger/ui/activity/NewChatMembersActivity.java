package com.messenger.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.view.ActivityAwareScreen;
import com.messenger.ui.view.NewChatMembersScreenImpl;


public class NewChatMembersActivity extends BaseMvpViewActivity<NewChatMembersScreenImpl> {

    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final String EXTRA_CONVERSATION_ID = "EXTRA_CONVERSATION_ID";

    public static final int MODE_NEW_CHAT = 1;
    public static final int MODE_CHAT_ADD_MEMBERS = 2;

    public static void startInNewChatMode(Context context, int requestCode) {
        Intent intent = new Intent(context, NewChatMembersActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_NEW_CHAT);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    public static void startInAddMembersMode(Activity activity, String conversationId,
                                             int requestCode) {
        Intent intent = new Intent(activity, NewChatMembersActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_CHAT_ADD_MEMBERS);
        intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    NewChatMembersScreenImpl createScreen() {
        return new NewChatMembersScreenImpl(this);
    }
}
