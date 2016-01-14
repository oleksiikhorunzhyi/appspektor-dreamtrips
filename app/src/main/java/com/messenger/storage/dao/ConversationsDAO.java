package com.messenger.storage.dao;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Conversation$Adapter;
import com.messenger.messengerservers.entities.Conversation$Table;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.Message$Table;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.ParticipantsRelationship$Table;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Update;

import java.util.Collection;
import java.util.List;

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

    @Deprecated
    public static Conversation getConversationById(String conversationId) {
        return new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();
    }

    public List<Conversation> getConversationsList(@Conversation.Type.ConversationType String type) {
        return new Select()
                .from(Conversation.class)
                .where(Condition.column(Conversation$Table.TYPE).is(type))
                .queryList();
    }

    public void deleteConversation(String conversationId) {
        getConversationById(conversationId).delete();
    }

    @Deprecated
    public void deleteConversations(@Nullable Collection<Conversation> conversations) {
        if (conversations != null && conversations.size() > 0) {
            String firstArg = Queryable.from(conversations).first().getId();
            String[] args = Queryable.from(conversations).skip(1).map(Conversation::getId).toArray(String.class);
            new Delete()
                    .from(ParticipantsRelationship.class)
                    .where(Condition.column(ParticipantsRelationship$Table.CONVERSATIONID).in(firstArg, args))
                    .query();

            new Delete()
                    .from(Conversation.class)
                    .where(Condition.column(Conversation$Table._ID).in(firstArg, args))
                    .query();
            getContentResolver().notifyChange(Conversation.CONTENT_URI, null);
            getContentResolver().notifyChange(ParticipantsRelationship.CONTENT_URI, null);
        }
    }

    public void deleteBySyncTime(long time) {
        new Delete().from(Conversation.class)
                .where(Condition.column(Conversation$Table.SYNCTIME).lessThan(time))
                .and(Condition.column(Conversation$Table.SYNCTIME).isNot(0))
                .queryClose();
    }

    public Observable<Cursor> selectConversationsList(@Nullable @Conversation.Type.ConversationType String type) {
        StringBuilder query = new StringBuilder("SELECT c.*, " +
                "m." + Message$Table.TEXT + " as " + Message$Table.TEXT + ", " +
                "m." + Message$Table.FROMID + " as " + Message$Table.FROMID + ", " +
                "m." + Message$Table.DATE + " as " + Message$Table.DATE + ", " +
                "u." + User$Table.USERNAME + " as " + User$Table.USERNAME + " " +
                "FROM " + Conversation.TABLE_NAME + " c " +
                "LEFT JOIN " + Message.TABLE_NAME + " m " +
                "ON m." + Message$Table._ID + "=(" +
                "SELECT " + Message$Table._ID + " FROM " + Message.TABLE_NAME + " mm " +
                "WHERE mm." + Message$Table.CONVERSATIONID + "=c." + Conversation$Table._ID + " " +
                "ORDER BY mm." + Message$Table.DATE + " DESC LIMIT 1) " +
                "LEFT JOIN " + User.TABLE_NAME + " u " +
                "ON m." + Message$Table.FROMID + "=u." + User$Table._ID + " " +
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
                "ORDER BY c." + Conversation$Table.ABANDONED + ", m." + Message$Table.DATE + " DESC");

        RxContentResolver.Query.Builder queryBuilder = new RxContentResolver.Query.Builder(null)
                .withSelection(query.toString());

        String[] args;
        if (onlyGroup) {
            args = new String[]{Conversation.Type.CHAT, Conversation.Type.CHAT};
        } else {
            args = new String[]{Conversation.Type.CHAT};
        }
        queryBuilder.withSelectionArgs(args);
        return query(queryBuilder.build(), Conversation.CONTENT_URI, Message.CONTENT_URI, ParticipantsRelationship.CONTENT_URI);
    }

    public Observable<Conversation> getConversation(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection(new Select().from(Conversation.class).byIds(conversationId).toString())
                .build();
        return query(q, Conversation.CONTENT_URI)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .map(cursor -> {
                    Conversation conversation = SqlUtils.convertToModel(false, Conversation.class, cursor);
                    cursor.close();
                    return conversation;
                });
    }

    public void incrementUnreadField(String conversationId) {
        new Update<>(Conversation.class)
                .set(Conversation$Table.UNREADMESSAGECOUNT + "=" + Conversation$Table.UNREADMESSAGECOUNT + "+1")
                .where(Condition.column(Conversation$Table._ID).is(conversationId))
                .queryClose();
        getContentResolver().notifyChange(Conversation.CONTENT_URI, null);
    }

    public void save(List<Conversation> conversations) {
        bulkInsert(conversations, new Conversation$Adapter(), Conversation.CONTENT_URI);
    }
}
