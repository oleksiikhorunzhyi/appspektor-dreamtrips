package com.messenger.storage.dao;

import android.content.Context;
import android.database.Cursor;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataConversation$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.entities.DataPhotoAttachment$Table;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.messenger.entities.PhotoAttachment;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.util.RxContentResolver;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;

public class MediaDAO extends BaseDAO {

    private static final String FLAGGING_ENABLED_COLUMN = "flagging_enabled";

    public MediaDAO(RxContentResolver rxContentResolver, Context context) {
        super(context, rxContentResolver);
    }

    public Observable<List<PhotoAttachment>> getPhotoAttachmentsSinceTime(String conversationId, String currentUserId, long syncTime) {
        RxContentResolver.Query query = new RxContentResolver.Query.Builder(null)
                .withSelection(" SELECT p." + DataPhotoAttachment$Table.PHOTOATTACHMENTID + " as " + DataPhotoAttachment$Table.PHOTOATTACHMENTID + "," +
                        " a." + DataAttachment$Table.MESSAGEID + " as " + DataAttachment$Table.MESSAGEID + "," +
                        " a." + DataAttachment$Table.CONVERSATIONID + " as " + DataAttachment$Table.CONVERSATIONID + "," +
                        " m." + DataMessage$Table.FROMID + " as " + DataMessage$Table.FROMID + "," +
                        " u." + DataUser$Table.SOCIALID + " as " + DataUser$Table.SOCIALID + "," +
                        " CASE WHEN m." + DataMessage$Table.STATUS + " != ? " +
                            " THEN m." + DataMessage$Table.DATE + " ELSE 0 END as " + DataMessage$Table.DATE + "," +
                        " CASE WHEN p." + DataPhotoAttachment$Table.URL + " IS NOT NULL " +
                            " THEN p." + DataPhotoAttachment$Table.URL + " ELSE p." + DataPhotoAttachment$Table.LOCALPATH + " END" +
                                " as " + DataPhotoAttachment$Table.URL + "," +
                        " CASE WHEN m." + DataMessage$Table.FROMID + " != ? AND c." + DataConversation$Table.TYPE + " = ? " +
                            " THEN 1 ELSE 0 END as " + FLAGGING_ENABLED_COLUMN +

                        " FROM " + DataPhotoAttachment.TABLE_NAME + " p" +
                        " LEFT JOIN " + DataAttachment.TABLE_NAME + " a" +
                        " ON p." + DataPhotoAttachment$Table.PHOTOATTACHMENTID + " = a." + DataAttachment$Table._ID +
                        " LEFT JOIN " + DataMessage.TABLE_NAME + " m" +
                        " ON a." + DataAttachment$Table.MESSAGEID + " = m." + DataMessage$Table._ID +
                        " LEFT JOIN " + DataUser.TABLE_NAME + " u" +
                        " ON m." + DataMessage$Table.FROMID + " = u." + DataUser$Table._ID +
                        " LEFT JOIN " + DataConversation.TABLE_NAME + " c"+
                        " ON a." + DataAttachment$Table.CONVERSATIONID + " = c." + DataConversation$Table._ID +

                        " WHERE m." + DataMessage$Table.CONVERSATIONID + " = ? " +
                        " AND m." + DataMessage$Table.SYNCTIME + " >= ? " +
                        " ORDER BY m." + DataMessage$Table.DATE + " ")
                .withSelectionArgs(new String[] {Integer.toString(MessageStatus.ERROR), currentUserId, ConversationType.TRIP,
                                                conversationId, Long.toString(syncTime)})
                .build();

        return query(query, DataPhotoAttachment.CONTENT_URI, DataMessage.CONTENT_URI)
                .map(this::obtainPhotoAttachmentListFromCursor)
                .filter(photoAttachments -> !photoAttachments.isEmpty());
    }

    private List<PhotoAttachment> obtainPhotoAttachmentListFromCursor(Cursor cursor) {
        ArrayList<PhotoAttachment> photoAttachments = new ArrayList<>();
        while (cursor.moveToNext()) photoAttachments.add(obtainPhotoAttachmentFromCursor(cursor));

        cursor.close();
        return photoAttachments;
    }

    private PhotoAttachment obtainPhotoAttachmentFromCursor(Cursor cursor) {
        String photoAttachmentId = cursor.getString(cursor.getColumnIndex(DataPhotoAttachment$Table.PHOTOATTACHMENTID));
        String url = cursor.getString(cursor.getColumnIndex(DataPhotoAttachment$Table.URL));
        String conversationId = cursor.getString(cursor.getColumnIndex(DataAttachment$Table.CONVERSATIONID));
        String messageId = cursor.getString(cursor.getColumnIndex(DataAttachment$Table.MESSAGEID));
        String userName = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID));
        int userSocialId = cursor.getInt(cursor.getColumnIndex(DataUser$Table.SOCIALID));
        boolean flaggingEnabled = cursor.getInt(cursor.getColumnIndex(FLAGGING_ENABLED_COLUMN)) == 1;
        Date date = new Date(cursor.getLong(cursor.getColumnIndex(DataMessage$Table.DATE)));

        Image image = new Image();
        image.setUrl(url);
        image.setFromFile(false);
        User user = new User(userSocialId);
        user.setUsername(userName);

        return new PhotoAttachment.Builder()
                .photoAttachmentId(photoAttachmentId)
                .image(image)
                .conversationId(conversationId)
                .messageId(messageId)
                .user(user)
                .flaggingEnabled(flaggingEnabled)
                .date(date)
                .build();
    }
}
