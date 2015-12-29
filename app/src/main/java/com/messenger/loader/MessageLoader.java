package com.messenger.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;

@Deprecated
public class MessageLoader extends BaseCursorRowLoader {
    private final ForceLoadContentObserver observer = new ForceLoadContentObserver();
    private final ContentResolver contentResolver;
    private final String conversationId;

    public MessageLoader(Context context, String conversationId) {
        super(context);
        this.contentResolver = context.getContentResolver();
        this.conversationId = conversationId;
    }

    @Override
    public Cursor loadInBackground() {
        contentResolver.registerContentObserver(Message.CONTENT_URI, true, observer);

// TODO: 12/16/15 add sort
        final String request =
                "SELECT m.*, u." + User.COLUMN_NAME + " as " + User.COLUMN_NAME +
                    ", u." + User.COLUMN_AVATAR + " as " + User.COLUMN_AVATAR +
                    ", u." + User.COLUMN_SOCIAL_ID + " as " + User.COLUMN_SOCIAL_ID +

                        " FROM " + Message.TABLE_NAME + " m LEFT JOIN " + User.TABLE_NAME + " u" +
                        " ON m." + Message.COLUMN_FROM + " = u." + User.COLUMN_ID +
                        " WHERE " + Message.COLUMN_CONVERSATION_ID + " = ?" +
                        " ORDER BY " + Message.COLUMN_DATE
                ;

        return rawQuery(request, new String[] {conversationId});
    }

    @Override
    protected void onReset() {
        contentResolver.unregisterContentObserver(observer);
        contentResolver.unregisterContentObserver(observer);
        super.onReset();
    }
}
