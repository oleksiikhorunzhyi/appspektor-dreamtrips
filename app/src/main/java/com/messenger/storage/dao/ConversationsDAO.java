package com.messenger.storage.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataConversation$Adapter;
import com.messenger.entities.DataConversation$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataParticipant$Table;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
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
    public static final String ATTACHMENT_TYPE_COLUMN = "attachmentType";

    @Deprecated
    public ConversationsDAO(Context context) {
        super(context);
    }

    public ConversationsDAO(Context context, RxContentResolver rxContentResolver) {
        super(context, rxContentResolver);
    }

    @Deprecated
    @Nullable
    public static DataConversation getConversationById(String conversationId) {
        return new Select()
                .from(DataConversation.class)
                .byIds(conversationId)
                .querySingle();
    }

    public Observable<DataConversation> getConversation(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection(new Select().from(DataConversation.class).byIds(conversationId).toString())
                .build();
        return query(q, DataConversation.CONTENT_URI)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .map(cursor -> {
                    DataConversation conversation = SqlUtils.convertToModel(false, DataConversation.class, cursor);
                    cursor.close();
                    return conversation;
                });
    }

    public List<DataConversation> getConversationsList(@ConversationType.Type String type) {
        return new Select()
                .from(DataConversation.class)
                .where(Condition.column(DataConversation$Table.TYPE).is(type))
                .queryList();
    }

    public void save(List<DataConversation> conversations) {
        bulkInsert(conversations, new DataConversation$Adapter(), DataConversation.CONTENT_URI);
    }

    public void save(DataConversation conversation) {
        conversation.save();
    }

    public void deleteConversation(String conversationId) {
        //server sends so many packets about kicking as devices with the same user are online
        DataConversation conversation = getConversationById(conversationId);
        if (conversation != null) conversation.delete();
    }

    @Deprecated
    public void deleteConversations(@Nullable Collection<DataConversation> conversations) {
        if (conversations != null && conversations.size() > 0) {
            String firstArg = Queryable.from(conversations).first().getId();
            String[] args = Queryable.from(conversations).skip(1).map(DataConversation::getId).toArray(String.class);
            new Delete()
                    .from(DataParticipant.class)
                    .where(Condition.column(DataParticipant$Table.CONVERSATIONID).in(firstArg, args))
                    .query();

            new Delete()
                    .from(DataConversation.class)
                    .where(Condition.column(DataConversation$Table._ID).in(firstArg, args))
                    .query();
            getContentResolver().notifyChange(DataConversation.CONTENT_URI, null);
            getContentResolver().notifyChange(DataParticipant.CONTENT_URI, null);
        }
    }

    public void deleteBySyncTime(long time) {
        new Delete().from(DataConversation.class)
                .where(Condition.column(DataConversation$Table.SYNCTIME).lessThan(time))
                .and(Condition.column(DataConversation$Table.SYNCTIME).isNot(0))
                .queryClose();
    }

    public Observable<Cursor> selectConversationsList(@Nullable @ConversationType.Type String type) {
        StringBuilder query = new StringBuilder("SELECT c.*, " +
                "m." + DataMessage$Table.TEXT + " as " + DataMessage$Table.TEXT + ", " +
                "m." + DataMessage$Table.FROMID + " as " + DataMessage$Table.FROMID + ", " +
                "m." + DataMessage$Table.DATE + " as " + DataMessage$Table.DATE + ", " +
                "u." + DataUser$Table.USERNAME + " as " + DataUser$Table.USERNAME + ", " +
                "a." + DataAttachment$Table.TYPE + " as  " + ATTACHMENT_TYPE_COLUMN + " " +

                "FROM " + DataConversation.TABLE_NAME + " c " +
                "LEFT JOIN " + DataMessage.TABLE_NAME + " m " +
                "ON m." + DataMessage$Table._ID + "=(" +
                "SELECT " + DataMessage$Table._ID + " FROM " + DataMessage.TABLE_NAME + " mm " +
                "WHERE mm." + DataMessage$Table.CONVERSATIONID + "=c." + DataConversation$Table._ID + " " +
                "ORDER BY mm." + DataMessage$Table.DATE + " DESC LIMIT 1) " +

                "LEFT JOIN " + DataUser.TABLE_NAME + " u " +
                "ON m." + DataMessage$Table.FROMID + "=u." + DataUser$Table._ID + " " +

                "LEFT JOIN " + DataParticipant.TABLE_NAME + " p " +
                "ON p." + DataParticipant$Table.CONVERSATIONID + "=c." + DataConversation$Table._ID + " " +

                "LEFT JOIN " + DataAttachment.TABLE_NAME + " a " +
                "ON a." + DataAttachment$Table.MESSAGEID + "=m." + DataMessage$Table._ID + " "

        );

        query.append("WHERE c." + DataConversation$Table.STATUS + " = ? ");
        boolean onlyGroup = type != null && ConversationType.GROUP.equals(type);
        if (onlyGroup) {
            query.append("AND c."+ DataConversation$Table.TYPE + " not like ?");
        }

        query.append("GROUP BY c." + DataConversation$Table._ID + " " +
                "HAVING c." + DataConversation$Table.TYPE + "=? " +
                "OR COUNT(p." + DataParticipant$Table.ID + ") > 1 " +
                "ORDER BY c." + DataConversation$Table.LASTACTIVEDATE + " DESC"
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
        return query(queryBuilder.build(), DataConversation.CONTENT_URI, DataMessage.CONTENT_URI, DataParticipant.CONTENT_URI);
    }

    public int updateDate(String conversationId, long date) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(DataConversation$Table.LASTACTIVEDATE, date);
        return getContentResolver().update(DataConversation.CONTENT_URI, contentValues,
                DataConversation$Table._ID + "=?", new String[]{conversationId});
    }

    public void incrementUnreadField(String conversationId) {
        new Update<>(DataConversation.class)
                .set(DataConversation$Table.UNREADMESSAGECOUNT + " = " + DataConversation$Table.UNREADMESSAGECOUNT + " +1 ")
                .where(Condition.column(DataConversation$Table._ID).is(conversationId))
                .queryClose();
        getContentResolver().notifyChange(DataConversation.CONTENT_URI, null);
    }

    public Observable<Integer> getUnreadConversationsCount() {
        String selection = "SELECT con." + DataConversation$Table._ID // cause Count(con._id) is interpreted as field for every id
                + " FROM " + DataConversation.TABLE_NAME + " con"
                        + " LEFT JOIN " + DataParticipant.TABLE_NAME + " par"
                        + " ON par." + DataParticipant$Table.CONVERSATIONID + "=con." + DataConversation$Table._ID
                        + " WHERE " + DataConversation$Table.UNREADMESSAGECOUNT + ">0"
                        + " AND " + DataConversation$Table.STATUS + " like ? "
                        + " GROUP BY con." + DataConversation$Table._ID
                        + " HAVING con." + DataConversation$Table.TYPE + "=? "
                        + " OR COUNT(par." + DataParticipant$Table.ID + ")>1 ";
        String[] args = new String[] {ConversationStatus.PRESENT, ConversationType.CHAT};

        RxContentResolver.Query query = new RxContentResolver.Query.Builder(null).withSelection(selection)
                                            .withSelectionArgs(args).build();

        return query(query, DataConversation.CONTENT_URI)
                .map(cursor -> cursor.getCount()); // BUG!!! because query is interpreted as object list with one field COUNT(*)
    }
}