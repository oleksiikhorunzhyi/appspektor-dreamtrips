package com.messenger.storage.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

import java.util.Collections;
import java.util.List;

import dagger.Lazy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class ConversationsDAO extends BaseDAO {
    public static final String ATTACHMENT_TYPE_COLUMN = "attachmentType";
    public static final String SINGLE_CONVERSATION_NAME_COLUMN = "oneToOneName";
    private Lazy<DataUser> currentUser;

    public ConversationsDAO(Context context, RxContentResolver rxContentResolver, Lazy<DataUser> currentUser) {
        super(context, rxContentResolver);
        this.currentUser = currentUser;
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

    public void updateDefaultSubject(String conversationId, @NonNull String name) {
        ContentValues cv = new ContentValues(1);
        cv.put(DataConversation$Table.DEFAULTSUBJECT, name);
        getContentResolver().update(DataConversation.CONTENT_URI,
                cv, DataConversation$Table._ID + "=?", new String[] {conversationId});
    }

    public void save(List<DataConversation> conversations) {
        bulkInsert(conversations, new DataConversation$Adapter(), DataConversation.CONTENT_URI);
    }

    public void save(DataConversation conversation) {
        save(Collections.singletonList(conversation));
    }

    public void deleteConversation(String conversationId) {
        //server sends so many packets about kicking as devices with the same user are online
        DataConversation conversation = getConversationById(conversationId);
        if (conversation != null) conversation.delete();
    }

    public void deleteBySyncTime(long time) {
        new Delete().from(DataConversation.class)
                .where(Condition.column(DataConversation$Table.SYNCTIME).lessThan(time))
                .and(Condition.column(DataConversation$Table.SYNCTIME).isNot(0))
                .queryClose();
    }

    public Observable<Cursor> getGroupConversationNames() {
        String selection = "SELECT " + DataConversation$Table._ID + ", " + DataConversation$Table.SUBJECT + " " +
                "FROM " + DataConversation.TABLE_NAME + " WHERE " + DataConversation$Table.TYPE + "=?";
        return query(new RxContentResolver.Query.Builder(null)
                .withSelection(selection)
                .withSelectionArgs(new String[] {String.valueOf(ConversationType.GROUP)})
                .build(),
                DataConversation.CONTENT_URI);
    }

    public Observable<Cursor> selectConversationsList(@Nullable @ConversationType.Type String type) {
        StringBuilder query = new StringBuilder("SELECT c.*, " +
                "m." + DataMessage$Table.TEXT + " as " + DataMessage$Table.TEXT + ", " +
                "m." + DataMessage$Table.FROMID + " as " + DataMessage$Table.FROMID + ", " +
                "m." + DataMessage$Table.DATE + " as " + DataMessage$Table.DATE + ", " +
                "u." + DataUser$Table.USERNAME + " as " + DataUser$Table.USERNAME + ", " +
                "uu." + DataUser$Table.USERAVATARURL + " as " + DataUser$Table.USERAVATARURL + ", " +
                "uu." + DataUser$Table.ONLINE + " as " + DataUser$Table.ONLINE + ", " +
                "uu." + DataUser$Table.USERNAME + " as " + SINGLE_CONVERSATION_NAME_COLUMN + ", " +
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
                "ON a." + DataAttachment$Table.MESSAGEID + "=m." + DataMessage$Table._ID + " " +

                "LEFT JOIN " + DataUser.TABLE_NAME + " uu " +
                "ON uu." + DataUser$Table._ID + "=(" +
                "SELECT pp." + DataParticipant$Table.USERID + " FROM " + DataParticipant.TABLE_NAME + " pp " +
                "WHERE pp." + DataParticipant$Table.CONVERSATIONID + "=c." + DataConversation$Table._ID + " " +
                "AND pp." + DataParticipant$Table.USERID + "<>? LIMIT 1)"
        );

        query.append("WHERE c." + DataConversation$Table.STATUS + "='" + ConversationStatus.PRESENT + "' ");
        boolean onlyGroup = type != null && ConversationType.GROUP.equals(type);
        if (onlyGroup) {
            query.append("AND c." + DataConversation$Table.TYPE + " not like '" + ConversationType.CHAT + "'");
        }

        query.append("GROUP BY c." + DataConversation$Table._ID + " " +
                "HAVING c." + DataConversation$Table.TYPE + "='" + ConversationType.CHAT + "' " +
                "OR COUNT(p." + DataParticipant$Table.ID + ") > 1 " +
                "ORDER BY c." + DataConversation$Table.LASTACTIVEDATE + " DESC"
        );

        RxContentResolver.Query.Builder queryBuilder = new RxContentResolver.Query.Builder(null)
                .withSelection(query.toString());

        String[] args = new String[]{currentUser.get().getId()};
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
        String[] args = new String[]{ConversationStatus.PRESENT, ConversationType.CHAT};

        RxContentResolver.Query query = new RxContentResolver.Query.Builder(null).withSelection(selection)
                .withSelectionArgs(args).build();

        return query(query, DataConversation.CONTENT_URI)
                .map(cursor -> cursor.getCount()); // BUG!!! because query is interpreted as object list with one field COUNT(*)
    }
}
