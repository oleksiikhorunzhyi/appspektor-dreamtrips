package com.messenger.storage.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.Conversation;
import com.messenger.entities.Conversation$Adapter;
import com.messenger.entities.Conversation$Table;
import com.messenger.entities.Message;
import com.messenger.entities.Message$Table;
import com.messenger.entities.ParticipantsRelationship;
import com.messenger.entities.ParticipantsRelationship$Table;
import com.messenger.entities.User;
import com.messenger.entities.User$Table;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
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
    @Nullable
    public static Conversation getConversationById(String conversationId) {
        return new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();
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

    public List<Conversation> getConversationsList(@ConversationType.Type String type) {
        return new Select()
                .from(Conversation.class)
                .where(Condition.column(Conversation$Table.TYPE).is(type))
                .queryList();
    }

    public void save(List<Conversation> conversations) {
        bulkInsert(conversations, new Conversation$Adapter(), Conversation.CONTENT_URI);
    }

    public void deleteConversation(String conversationId) {
        //server sends so many packets about kicking as devices with the same user are online
        Conversation conversation = getConversationById(conversationId);
        if (conversation != null) conversation.delete();
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

    public Observable<Cursor> selectConversationsList(@Nullable @ConversationType.Type String type) {
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

        query.append("WHERE c." + Conversation$Table.STATUS + " = ? ");
        boolean onlyGroup = type != null && ConversationType.GROUP.equals(type);
        if (onlyGroup) {
            query.append("AND c.type not like ?");
        }

        query.append("GROUP BY c." + Conversation$Table._ID + " " +
                "HAVING c." + Conversation$Table.TYPE + "=? " +
                "OR COUNT(p." + ParticipantsRelationship$Table.ID + ") > 1 " +
                "ORDER BY c." + Conversation$Table.ABANDONED + ", " +
                "c." + Conversation$Table.LASTACTIVEDATE + " DESC"
        );

        RxContentResolver.Query.Builder queryBuilder = new RxContentResolver.Query.Builder(null)
                .withSelection(query.toString());

        String[] args;
        if (onlyGroup) {
            args = new String[]{ConversationStatus.PRESENT, ConversationType.CHAT, ConversationType.CHAT};
        } else {
            args = new String[]{ConversationStatus.PRESENT, ConversationType.CHAT};
        }
        queryBuilder.withSelectionArgs(args);
        return query(queryBuilder.build(), Conversation.CONTENT_URI, Message.CONTENT_URI, ParticipantsRelationship.CONTENT_URI);
    }

    public int updateDate(String conversationId, long date) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(Conversation$Table.LASTACTIVEDATE, date);
        return getContentResolver().update(Conversation.CONTENT_URI, contentValues,
                Conversation$Table._ID + "=?", new String[]{conversationId});
    }

    public void incrementUnreadField(String conversationId) {
        new Update<>(Conversation.class)
                .set(Conversation$Table.UNREADMESSAGECOUNT + " = " + Conversation$Table.UNREADMESSAGECOUNT + " +1 ")
                .where(Condition.column(Conversation$Table._ID).is(conversationId))
                .queryClose();
        getContentResolver().notifyChange(Conversation.CONTENT_URI, null);
    }

    public Observable<Integer> getUnreadConversationsCount() {
        String selection = "SELECT con." + Conversation$Table._ID // cause Count(con._id) is interpreted as field for every id
                + " FROM " + Conversation.TABLE_NAME + " con"
                        + " LEFT JOIN " + ParticipantsRelationship.TABLE_NAME + " par"
                        + " ON par." + ParticipantsRelationship$Table.CONVERSATIONID + "=con." + Conversation$Table._ID
                        + " WHERE " + Conversation$Table.UNREADMESSAGECOUNT + ">0"
                        + " AND " + Conversation$Table.STATUS + " like ? "
                        + " GROUP BY con." + Conversation$Table._ID
                        + " HAVING con." + Conversation$Table.TYPE + "=? "
                        + " OR COUNT(par." + ParticipantsRelationship$Table.ID + ")>1 ";
        String[] args = new String[] {ConversationStatus.PRESENT, ConversationType.CHAT};

        RxContentResolver.Query query = new RxContentResolver.Query.Builder(null).withSelection(selection)
                                            .withSelectionArgs(args).build();

        return query(query, Conversation.CONTENT_URI)
                .map(cursor -> cursor.getCount()); // BUG!!! because query is interpreted as object list with one field COUNT(*)
    }
}
