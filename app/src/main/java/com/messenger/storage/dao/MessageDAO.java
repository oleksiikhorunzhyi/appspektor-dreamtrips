package com.messenger.storage.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataConversation$Table;
import com.messenger.entities.DataLocationAttachment;
import com.messenger.entities.DataLocationAttachment$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Adapter;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.entities.DataPhotoAttachment$Table;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataTranslation$Table;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.constant.MessageType;
import com.messenger.util.ChatDateUtils;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;

import java.util.Collections;
import java.util.List;

import rx.Observable;

public class MessageDAO extends BaseDAO {

    public static final String ATTACHMENT_ID = DataAttachment$Table.TABLE_NAME + DataAttachment$Table._ID;
    public static final String TRANSLATION_ID = DataTranslation$Table.TABLE_NAME + DataTranslation$Table._ID;
    public static final String CONVERSATION_TYPE = DataConversation$Table.TABLE_NAME + "_" + DataConversation$Table.TYPE;
    public static final String USER_ID = DataUser$Table.TABLE_NAME + "_" + DataUser$Table._ID;
    public static final String USER_FIRST_NAME = DataUser$Table.TABLE_NAME + "_" + DataUser$Table.FIRSTNAME;
    public static final String USER_LAST_NAME = DataUser$Table.TABLE_NAME + "_" + DataUser$Table.LASTNAME;

    public static final String ATTACHMENT_TYPE = DataAttachment$Table.TABLE_NAME + "_" + DataAttachment$Table.TYPE;

    public MessageDAO(RxContentResolver rxContentResolver, Context context) {
        super(context, rxContentResolver);
    }

    public Observable<DataMessage> getMessage(String messageId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataMessage$Table.TABLE_NAME + " " +
                        "WHERE " + DataMessage$Table._ID + "=?")
                .withSelectionArgs(new String[]{messageId})
                .build();

        return query(q, DataMessage.CONTENT_URI)
                .compose(DaoTransformers.toEntity(DataMessage.class));
    }

    public Observable<DataMessage> getLastOtherUserMessage(String conversationId, String ownerId, long lastMessageDate) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataMessage$Table.TABLE_NAME + " " +
                        "WHERE " + DataMessage$Table.FROMID + "<>? " +
                        "AND " + DataMessage$Table.TYPE + "=? " +
                        "AND " + DataMessage$Table.CONVERSATIONID + "=? " +
                        "AND " + DataMessage$Table.STATUS + "=? " +
                        "AND " + DataMessage$Table.DATE + "<=? " +
                        "ORDER BY " + DataMessage$Table.DATE + " DESC " +
                        "LIMIT 1")
                .withSelectionArgs(new String[]{ownerId, MessageType.MESSAGE, conversationId,
                        String.valueOf(MessageStatus.SENT),
                        String.valueOf(lastMessageDate)})
                .build();

        return query(q, DataMessage.CONTENT_URI)
                .compose(DaoTransformers.toEntity(DataMessage.class));
    }


    public Observable<Cursor> getMessagesBySyncTime(String conversationId, long syncTime) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT m.*, " +
                        // message author
                        "u." + DataUser$Table.FIRSTNAME + " as " + DataUser$Table.FIRSTNAME + ", " +
                        "u." + DataUser$Table.LASTNAME + " as " + DataUser$Table.LASTNAME + ", " +
                        "u." + DataUser$Table.USERAVATARURL + " as " + DataUser$Table.USERAVATARURL + ", " +
                        "u." + DataUser$Table.SOCIALID + " as " + DataUser$Table.SOCIALID + ", " +

                        // message recipient
                        "uu." + DataUser$Table._ID + " as " + USER_ID + ", " +
                        "uu." + DataUser$Table.FIRSTNAME + " as " + USER_FIRST_NAME + ", " +
                        "uu." + DataUser$Table.LASTNAME + " as " + USER_LAST_NAME + ", " +

                        "a." + DataAttachment$Table._ID + " as " + ATTACHMENT_ID + ", " +
                        "a." + DataAttachment$Table.TYPE + " as " + ATTACHMENT_TYPE + ", " +

                        "p." + DataPhotoAttachment$Table.URL + " as " + DataPhotoAttachment$Table.URL + ", " +
                        "p." + DataPhotoAttachment$Table.LOCALPATH + " as " + DataPhotoAttachment$Table.LOCALPATH + ", " +
                        "p." + DataPhotoAttachment$Table.UPLOADSTATE + " as " + DataPhotoAttachment$Table.UPLOADSTATE + ", " +

                        "l." + DataLocationAttachment$Table.LAT + " as " + DataLocationAttachment$Table.LAT + ", " +
                        "l." + DataLocationAttachment$Table.LNG + " as " + DataLocationAttachment$Table.LNG + ", " +

                        "t." + DataTranslation$Table._ID + " as " + TRANSLATION_ID + ", " +
                        "t." + DataTranslation$Table.TRANSLATION + " as " + DataTranslation$Table.TRANSLATION + ", " +
                        "t." + DataTranslation$Table.TRANSLATESTATUS + " as " + DataTranslation$Table.TRANSLATESTATUS + ", " +

                        "c." + DataConversation$Table.TYPE + " as " + CONVERSATION_TYPE + " " +

                        "FROM " + DataMessage.TABLE_NAME + " m " +
                        "LEFT JOIN " + DataUser$Table.TABLE_NAME + " u " +
                        "ON m." + DataMessage$Table.FROMID + "=u." + DataUser$Table._ID + " " +
                        "LEFT JOIN " + DataUser$Table.TABLE_NAME + " uu " +
                        "ON m." + DataMessage$Table.TOID + "=uu." + DataUser$Table._ID + " " +
                        "LEFT JOIN " + DataAttachment.TABLE_NAME + " a " +
                        "ON m." + DataMessage$Table._ID + "=a." + DataAttachment$Table.MESSAGEID + " " +
                        "LEFT JOIN " + DataPhotoAttachment.TABLE_NAME + " p " +
                        "ON a." + DataAttachment$Table._ID + "=p." + DataPhotoAttachment$Table.PHOTOATTACHMENTID + " " +
                        "LEFT JOIN " + DataLocationAttachment.TABLE_NAME + " l " +
                        "ON a." + DataLocationAttachment$Table._ID + "=l." + DataLocationAttachment$Table._ID + " " +
                        "LEFT JOIN " + DataTranslation.TABLE_NAME + " t " +
                        "ON m." + DataMessage$Table._ID + "=t." + DataTranslation$Table._ID + " " +
                        "LEFT JOIN " + DataConversation.TABLE_NAME + " c " +
                        "ON m." + DataMessage$Table.CONVERSATIONID + "=c." + DataConversation$Table._ID + " " +

                        "WHERE m." + DataMessage$Table.CONVERSATIONID + "=? " +
                        "AND m." + DataMessage$Table.DATE + ">=c." + DataConversation$Table.CLEARTIME + " " +
                        "AND m." + DataMessage$Table.SYNCTIME + " >=? " +
                        "ORDER BY m." + DataMessage$Table.DATE)
                .withSelectionArgs(new String[]{conversationId, Long.toString(syncTime)}).build();

        return query(q, DataMessage.CONTENT_URI, DataUser.CONTENT_URI, DataTranslation.CONTENT_URI,
                DataAttachment.CONTENT_URI, DataPhotoAttachment.CONTENT_URI, DataLocationAttachment.CONTENT_URI);
    }

    public Observable<Integer> markMessagesAsRead(String conversationId, String userId, long visibleTime) {
        return Observable.create(subscriber -> {
            subscriber.onStart();
            String clause = DataMessage$Table.CONVERSATIONID + " = ? "
                    + "AND " + DataMessage$Table.DATE + " <= ? "
                    + "AND " + DataMessage$Table.FROMID + " <> ? "
                    + "AND " + DataMessage$Table.STATUS + " = ? ";
            ContentValues cv = new ContentValues(1);
            cv.put(DataMessage$Table.STATUS, MessageStatus.READ);
            try {
                subscriber.onNext(getContentResolver().update(DataMessage.CONTENT_URI, cv, clause,
                        new String[]{conversationId, String.valueOf(visibleTime),
                                userId, String.valueOf(MessageStatus.SENT)})
                );
                getContentResolver().notifyChange(DataMessage.CONTENT_URI, null);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public void deleteMessagesByConversation(String conversationId) {
        // TODO: fucking sqlite does not execute DELETE with JOINed tables
        // TODO: somewhen we replace this code via FOREIGN KEY
        String selectAttachments =
                "SELECT " + DataAttachment$Table._ID + " " +
                        "FROM " + DataAttachment$Table.TABLE_NAME + " " +
                        "WHERE " + DataAttachment$Table.CONVERSATIONID + "=?";
        String queryClearLocation =
                "DELETE FROM " + DataLocationAttachment.TABLE_NAME + " " +
                        "WHERE " + DataLocationAttachment$Table._ID + " IN (" + selectAttachments + ")";
        String queryClearPhoto =
                "DELETE FROM " + DataPhotoAttachment$Table.TABLE_NAME + " " +
                        "WHERE " + DataPhotoAttachment$Table.PHOTOATTACHMENTID + " IN (" + selectAttachments + ")";
        String queryClearAttachment =
                "DELETE FROM " + DataAttachment$Table.TABLE_NAME + " " +
                        "WHERE " + DataAttachment$Table.CONVERSATIONID + "=?";
        String queryClearMessages =
                "DELETE FROM " + DataMessage$Table.TABLE_NAME + " " +
                        "WHERE " + DataMessage$Table.CONVERSATIONID + "=?";

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.execSQL(queryClearLocation, new String[]{conversationId});
        db.execSQL(queryClearPhoto, new String[]{conversationId});
        db.execSQL(queryClearAttachment, new String[]{conversationId});
        db.execSQL(queryClearMessages, new String[]{conversationId});
        db.setTransactionSuccessful();
        db.endTransaction();

        ContentResolver contentResolver = getContentResolver();
        contentResolver.notifyChange(DataMessage.CONTENT_URI, null);
        contentResolver.notifyChange(DataAttachment.CONTENT_URI, null);
        contentResolver.notifyChange(DataPhotoAttachment.CONTENT_URI, null);
        contentResolver.notifyChange(DataLocationAttachment.CONTENT_URI, null);
    }

    public void save(List<DataMessage> messages) {
        bulkInsert(messages, new DataMessage$Adapter(), DataMessage.CONTENT_URI);
    }

    public void save(DataMessage message) {
        // BaseProviderModel.save() saves all null strings as "null"(https://github.com/Raizlabs/DBFlow/pull/430)
        save(Collections.singletonList(message));
    }

    public void delete(List<DataMessage> messages) {
        deleteMessageByIds(Queryable.from(messages).map(DataMessage::getId).toList());
    }

    public void deleteMessageByIds(List<String> messageIds) {
        getContentResolver().delete(DataMessage.CONTENT_URI,
                DataMessage$Table._ID + " IN (?)",
                new String[]{TextUtils.join(",", messageIds)}
        );
    }

    public void updateStatus(String msgId, int messageStatus, long time) {
        ContentValues contentValues = new ContentValues(3);
        contentValues.put(DataMessage$Table.STATUS, messageStatus);
        contentValues.put(DataMessage$Table.DATE, time);
        contentValues.put(DataMessage$Table.SYNCTIME, time);
        getContentResolver().update(DataMessage.CONTENT_URI, contentValues, DataMessage$Table._ID + "=?", new String[]{msgId});
    }

    public static DataMessage fromCursor(Cursor cursor, boolean moveToFirst) {
        return SqlUtils.convertToModel(!moveToFirst, DataMessage.class, cursor);
    }

    public int markSendingAsFailed() {
        ContentValues cv = new ContentValues();
        cv.put(DataMessage$Table.STATUS, MessageStatus.ERROR);
        cv.put(DataMessage$Table.SYNCTIME, ChatDateUtils.getErrorMessageDate());
        return getContentResolver().update(DataMessage.CONTENT_URI, cv,
                DataMessage$Table.STATUS + "=?",
                new String[]{String.valueOf(MessageStatus.SENDING)}
        );
    }
}
