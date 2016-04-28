package com.messenger.storage.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataConversation$Adapter;
import com.messenger.entities.DataConversation$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataParticipant$Table;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataTranslation$Table;
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
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ConversationsDAO extends BaseDAO {
    public static final String ATTACHMENT_TYPE_COLUMN = "attachmentType";
    public static final String SINGLE_CONVERSATION_NAME_COLUMN = "oneToOneName";
    public static final String GROUP_CONVERSATION_NAME_COLUMN = "groupName";
    public static final String GROUP_CONVERSATION_USER_COUNT_COLUMN = "groupUserCount";
    public static final String LAST_MESSAGE_AUTHOR_COLUMN = "authorName";

    private SessionHolder<UserSession> appSessionHolder;

    public ConversationsDAO(Context context, RxContentResolver rxContentResolver, SessionHolder<UserSession> appSessionHolder) {
        super(context, rxContentResolver);
        this.appSessionHolder = appSessionHolder;
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
                .subscribeOn(Schedulers.io())
                .compose(DaoTransformers.toEntity(DataConversation.class));
    }

    public Observable<Integer> conversationsCount() {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT COUNT(_id) FROM " + DataConversation.TABLE_NAME + " " +
                        "WHERE " + DataConversation$Table.STATUS + " =  ?"
                )
                .withSelectionArgs(new String[]{ConversationStatus.PRESENT})
                .build();

        return query(q, null)
                .map(cursor -> {
                    int res = cursor.moveToFirst() ? cursor.getInt(0) : 0;
                    cursor.close();
                    return res;
                });
    }

    public Observable<Pair<DataConversation, List<DataUser>>> getConversationWithParticipants(String conversationId) {
        //TODO: rename DataUser#_ID field to userId, because we don't use cursor with DataUser in list
        String stringQuery = "SELECT c.*, u.*" +
                "FROM " + DataConversation.TABLE_NAME + " c " +

                "JOIN " + DataParticipant.TABLE_NAME + " p " +
                "ON p." + DataParticipant$Table.CONVERSATIONID + "= c." + DataConversation$Table._ID + " " +

                "JOIN " + DataUser$Table.TABLE_NAME + " u " +
                "ON p." + DataParticipant$Table.USERID + "=u." + DataUser$Table._ID + " " +

                "WHERE c." + DataConversation$Table._ID + "=? " +
                "ORDER BY u." + DataUser$Table._ID;

        RxContentResolver.Query query = new RxContentResolver.Query.Builder(null)
                .withSelection(stringQuery)
                .withSelectionArgs(new String[] {conversationId})
                .build();
        return query(query, DataConversation.CONTENT_URI, DataParticipant.CONTENT_URI, DataUser.CONTENT_URI)
                .subscribeOn(Schedulers.io())
                .map(cursor -> convertConversationWithParticipantFromCursor(cursor, conversationId));
    }

    @Nullable
    private Pair<DataConversation, List<DataUser>> convertConversationWithParticipantFromCursor(Cursor cursor, String conversationId) {
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        DataConversation conversation = SqlUtils.convertToModel(false, DataConversation.class, cursor);
        // TODO: 3/21/16 because _id exist in conversation and user table, see todo in #getConversationWithParticipants(String)
        conversation.setId(conversationId);
        List<DataUser> users = SqlUtils.convertToList(DataUser.class, cursor);
        cursor.close();
        return new Pair<>(conversation, users);
    }

    public void save(List<DataConversation> conversations) {
        bulkInsert(conversations, new DataConversation$Adapter(), DataConversation.CONTENT_URI);
    }

    public void save(DataConversation conversation) {
        // BaseProviderModel.save() saves all null strings as "null"(https://github.com/Raizlabs/DBFlow/pull/430)
        save(Collections.singletonList(conversation));
    }

    public void deleteBySyncTime(long time) {
        new Delete().from(DataConversation.class)
                .where(Condition.column(DataConversation$Table.SYNCTIME).lessThan(time))
                .and(Condition.column(DataConversation$Table.SYNCTIME).isNot(0))
                .queryClose();
    }

    public Observable<Cursor> selectConversationsList(@Nullable @ConversationType.Type String type, String searchQuery) {
        String currentUserId = appSessionHolder.get().get().getUser().getUsername(); // username is id for messenger

        StringBuilder query = new StringBuilder("SELECT c.*, " +
                "m." + DataMessage$Table.TEXT + " as " + DataMessage$Table.TEXT + ", " +
                "m." + DataMessage$Table.FROMID + " as " + DataMessage$Table.FROMID + ", " +
                "m." + DataMessage$Table.DATE + " as " + DataMessage$Table.DATE + ", " +

                "IFNULL(u." + DataUser$Table.FIRSTNAME  + ",'') || ' ' || IFNULL(u." + DataUser$Table.LASTNAME + ",'') "+
                                                            "as " + LAST_MESSAGE_AUTHOR_COLUMN + ", " +

                "uu." + DataUser$Table.USERAVATARURL + " as " + DataUser$Table.USERAVATARURL + ", " +
                "uu." + DataUser$Table.ONLINE + " as " + DataUser$Table.ONLINE + ", " +
                "IFNULL(uu." + DataUser$Table.FIRSTNAME  + ",'') || ' ' || IFNULL(uu." + DataUser$Table.LASTNAME + ",'') "+
                                                "as " + SINGLE_CONVERSATION_NAME_COLUMN + ", " +
                "a." + DataAttachment$Table.TYPE + " as  " + ATTACHMENT_TYPE_COLUMN + ", " +
                "t." + DataTranslation$Table.TRANSLATESTATUS + " as  " + DataTranslation$Table.TRANSLATESTATUS + ", " +
                "t." + DataTranslation$Table.TRANSLATION + " as  " + DataTranslation$Table.TRANSLATION + ", " +


                "GROUP_CONCAT(uuu." + DataUser$Table.FIRSTNAME + ", ', ') " +
                                                    "as " + GROUP_CONVERSATION_NAME_COLUMN + ", " +
                "COUNT(uuu." + DataUser$Table._ID + ") as " + GROUP_CONVERSATION_USER_COUNT_COLUMN + " " +

                "FROM " + DataConversation.TABLE_NAME + " c " +
                "LEFT JOIN " + DataMessage.TABLE_NAME + " m " +
                "ON m." + DataMessage$Table._ID + "=(" +
                "SELECT " + DataMessage$Table._ID + " FROM " + DataMessage.TABLE_NAME + " mm " +
                "WHERE mm." + DataMessage$Table.CONVERSATIONID + "=c." + DataConversation$Table._ID + " " +
                "ORDER BY mm." + DataMessage$Table.DATE + " DESC LIMIT 1) " +

                "LEFT JOIN " + DataUser.TABLE_NAME + " u " +
                "ON m." + DataMessage$Table.FROMID + "=u." + DataUser$Table._ID + " " +

                "JOIN " + DataParticipant.TABLE_NAME + " p " +
                "ON p." + DataParticipant$Table.CONVERSATIONID + "=c." + DataConversation$Table._ID + " " +

                "LEFT JOIN " + DataAttachment.TABLE_NAME + " a " +
                "ON a." + DataAttachment$Table.MESSAGEID + "=m." + DataMessage$Table._ID + " " +

                "LEFT JOIN " + DataTranslation.TABLE_NAME + " t " +
                "ON t." + DataTranslation$Table._ID + "=m." + DataMessage$Table._ID + " " +

                "JOIN " + DataUser.TABLE_NAME + " uuu " +
                "ON p." + DataParticipant$Table.USERID + "=uuu." + DataUser$Table._ID + " " +

                "LEFT JOIN " + DataUser.TABLE_NAME + " uu " +
                "ON uu." + DataUser$Table._ID + "=(" +
                "SELECT pp." + DataParticipant$Table.USERID + " FROM " + DataParticipant.TABLE_NAME + " pp " +
                "WHERE pp." + DataParticipant$Table.CONVERSATIONID + "=c." + DataConversation$Table._ID + " " +
                "AND pp." + DataParticipant$Table.USERID + "<>? LIMIT 1) " +

                "WHERE c." + DataConversation$Table.STATUS + "='" + ConversationStatus.PRESENT + "' "
        );


        boolean onlyGroup = type != null && ConversationType.GROUP.equals(type);
        if (onlyGroup) {
            query.append("AND c." + DataConversation$Table.TYPE + "<>'" + ConversationType.CHAT + "' ");
        }

        if (!TextUtils.isEmpty(searchQuery)) {
            String wherePattern = "AND (" +
                    "c." + DataConversation$Table.SUBJECT + " LIKE '%" + searchQuery + "%' " +
                    "OR c." + DataConversation$Table.SUBJECT + " IS '' AND uu." + DataUser$Table.FIRSTNAME + " || ' ' || uu." + DataUser$Table.LASTNAME  + " LIKE '%" + searchQuery + "%')";
            query.append(wherePattern);

        }

        query.append("GROUP BY c." + DataConversation$Table._ID + " " +
                "HAVING c." + DataConversation$Table.TYPE + "='" + ConversationType.CHAT + "' " +
                "OR COUNT(p." + DataParticipant$Table.ID + ") > 1 " +
                "ORDER BY c." + DataConversation$Table.LASTACTIVEDATE + " DESC"
        );

        RxContentResolver.Query.Builder queryBuilder = new RxContentResolver.Query.Builder(null)
                .withSelection(query.toString());

        String[] args = new String[]{currentUserId};
        queryBuilder.withSelectionArgs(args);
        return query(queryBuilder.build(), DataConversation.CONTENT_URI, DataMessage.CONTENT_URI, DataParticipant.CONTENT_URI, DataUser.CONTENT_URI, DataTranslation.CONTENT_URI);
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
                .map(cursor -> {
                    int count = cursor.getCount();
                    cursor.close();
                    return count;
                }); // BUG!!! because query is interpreted as object list with one field COUNT(*)
    }
}
