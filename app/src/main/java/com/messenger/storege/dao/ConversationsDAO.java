package com.messenger.storege.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Conversation$Table;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.ParticipantsRelationship$Table;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ConversationsDAO extends BaseDAO {

    @Deprecated
    public ConversationsDAO(Context context) {
        super(context);
    }

    public ConversationsDAO(Context context, RxContentResolver rxContentResolver) {
        super(context, rxContentResolver);
    }

    public static Conversation getConversationById(String conversationId) {
        return new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();
    }

    @Deprecated
    public static void leaveConversation(ContentResolver contentResolver, String conversationId, boolean isOwner) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(Conversation$Table.ABANDONED, isOwner);
        contentResolver.update(Conversation.CONTENT_URI, contentValues, Conversation$Table._ID + "=?",
                new String[]{conversationId});
    }

    public Observable<Cursor> selectConversationsList(@Nullable @Conversation.Type.ConversationType String type) {
        StringBuilder query = new StringBuilder("SELECT c.*, " +
                "m." + Message.COLUMN_TEXT + " as " + Message.COLUMN_TEXT + ", " +
                "m." + Message.COLUMN_FROM + " as " + Message.COLUMN_FROM + ", " +
                "m." + Message.COLUMN_DATE + " as " + Message.COLUMN_DATE + ", " +
                "u." + User$Table.USERNAME + " as " + User$Table.USERNAME + " " +
                "FROM " + Conversation.TABLE_NAME + " c " +
                "LEFT JOIN " + Message.TABLE_NAME + " m " +
                "ON m." + Message._ID + "=(" +
                "SELECT " + Message._ID + " FROM " + Message.TABLE_NAME + " mm " +
                "WHERE mm." + Message.COLUMN_CONVERSATION_ID + "=c." + Conversation$Table._ID + " " +
                "ORDER BY mm." + Message.COLUMN_DATE + " DESC LIMIT 1) " +
                "LEFT JOIN " + User.TABLE_NAME + " u " +
                "ON m." + Message.COLUMN_FROM + "=u." + User$Table._ID + " " +
                "LEFT JOIN " + ParticipantsRelationship.TABLE_NAME + " p " +
                "ON p." + ParticipantsRelationship$Table.CONVERSATIONID + "=c." + Conversation$Table._ID + " "
        );


        boolean onlyGroup = type != null && Conversation.Type.GROUP.equals(type);
        if (onlyGroup) {
            query.append("WHERE c.type not like ? ");
        }

        query.append("GROUP BY c." + Conversation$Table._ID + " " +
                "HAVING c." + Conversation$Table.TYPE + "=? " +
                "OR COUNT(p." + ParticipantsRelationship$Table.ID + ") > 1 " +
                "ORDER BY c." + Conversation$Table.ABANDONED + ", m." + Message.COLUMN_DATE + " DESC");

        RxContentResolver.Query.Builder queryBuilder = new RxContentResolver.Query.Builder(null)
                .withSelection(query.toString());

        String[] args;
        if (onlyGroup) {
            args = new String[]{Conversation.Type.CHAT, Conversation.Type.CHAT};
        } else {
            args = new String[]{Conversation.Type.CHAT};
        }
        queryBuilder.withSelectionArgs(args);
        return query(queryBuilder.build(), Conversation.CONTENT_URI, Message.CONTENT_URI, ParticipantsRelationship.CONTENT_URI)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io());
    }


    public Observable<Conversation> getConversation(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection(new Select().from(Conversation.class).byIds(conversationId).toString())
                .build();
        return query(q, Conversation.CONTENT_URI)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .map(cursor -> SqlUtils.convertToModel(false, Conversation.class, cursor));
    }

    public Observable<Conversation> getConversationWithoutObserve(String conversationId) {
        return Observable.defer(() ->
                Observable.just(new Select().from(Conversation.class).byIds(conversationId).querySingle()))
                .subscribeOn(Schedulers.io());
    }
}
