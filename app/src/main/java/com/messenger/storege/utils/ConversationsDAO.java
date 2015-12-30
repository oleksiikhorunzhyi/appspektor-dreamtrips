package com.messenger.storege.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Conversation$Table;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.util.RxContentResolver;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ConversationsDAO {

    public static void leaveConversation(ContentResolver contentResolver, String conversationId, boolean isOwner) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(Conversation$Table.ABANDONED, isOwner);
        contentResolver.update(Conversation.CONTENT_URI, contentValues, Conversation$Table._ID + "=?",
                new String[] {conversationId});
    }

    public static Observable<Cursor> selectConversationsList(RxContentResolver resolver,
                                                             @Nullable @Conversation.Type.ConversationType String type,
                                                             Uri... observeUris) {
        StringBuilder query = new StringBuilder("SELECT c.*, " +
                "m." + Message.COLUMN_TEXT + " as " + Message.COLUMN_TEXT + ", " +
                "m." + Message.COLUMN_FROM + " as " + Message.COLUMN_FROM + ", " +
                "m." + Message.COLUMN_DATE + " as " + Message.COLUMN_DATE + ", " +
                "u." + User.COLUMN_NAME + " as " + User.COLUMN_NAME + " " +
                "FROM " + Conversation.TABLE_NAME + " c " +
                "LEFT JOIN " + Message.TABLE_NAME + " m " +
                "ON m." + Message._ID + "=(" +
                "SELECT " + Message._ID + " FROM " + Message.TABLE_NAME + " mm " +
                "WHERE mm." + Message.COLUMN_CONVERSATION_ID + "=c." + Conversation$Table._ID +
                " ORDER BY mm." + Message.COLUMN_DATE + " DESC LIMIT 1) " +
                "LEFT JOIN " + User.TABLE_NAME + " u " +
                "ON m." + Message.COLUMN_FROM + "=u." + User.COLUMN_ID
        );


        boolean onlyGroup = type != null && Conversation.Type.GROUP.equals(type);
        if (onlyGroup) {
            query.append(" WHERE c.type not like ?");
        }

        query.append(" ORDER BY c." + Conversation$Table.ABANDONED + ", m." + Message.COLUMN_DATE + " DESC");

        RxContentResolver.Query.Builder queryBuilder = new RxContentResolver.Query.Builder(null)
                .withSelection(query.toString());

        if (onlyGroup) {
            queryBuilder.withSelectionArgs(new String[]{Conversation.Type.CHAT});
        }

        return resolver
                .query(queryBuilder.build(), observeUris)
                .subscribeOn(Schedulers.io());
    }
}
