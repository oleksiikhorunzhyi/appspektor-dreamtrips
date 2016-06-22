package com.messenger.storage.dao;

import android.content.Context;

import com.messenger.entities.DataTyping;
import com.messenger.entities.DataTyping$Table;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.messenger.util.RxContentResolver;

import java.util.List;

import rx.Observable;

public class TypingDAO extends BaseDAO {

    public TypingDAO(Context context, RxContentResolver rxContentResolver) {
        super(context, rxContentResolver);
    }

    public Observable<List<DataUser>> getTypingUser(String conversationId) {
        String query = "SELECT u.* FROM " + DataUser.TABLE_NAME + " u " +
                "INNER JOIN " + DataTyping.TABLE_NAME + " t " +
                "ON t." + DataTyping$Table.USERID + "=u." + DataUser$Table._ID + " " +
                "WHERE t." + DataTyping$Table.CONVERSATIONID + "=?";

        return query(new RxContentResolver.Query.Builder(null)
                .withSelection(query)
                .withSelectionArgs(new String[] {conversationId})
                .build(), DataTyping.CONTENT_URI)
                .compose(DaoTransformers.toEntityList(DataUser.class));
    }

    public void save(DataTyping dataTyping) {
        dataTyping.save();
    }

    public void deleteAll() {
        getContentResolver().delete(DataTyping.CONTENT_URI, null, null);
    }

    public void deleteById(String id) {
        getContentResolver()
                .delete(DataTyping.CONTENT_URI, DataTyping$Table.TYPINGID + "=?", new String[] {id});
    }

    public void deleteByUserId(String userId) {
        getContentResolver().delete(DataTyping.CONTENT_URI, DataTyping$Table.USERID + "=?",
                new String[] {userId});
    }
}
