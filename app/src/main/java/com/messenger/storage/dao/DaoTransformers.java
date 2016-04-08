package com.messenger.storage.dao;

import android.database.Cursor;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.raizlabs.android.dbflow.sql.SqlUtils;

import java.util.List;

import rx.Observable;

public class DaoTransformers {
    public static Observable.Transformer<Cursor, List<DataUser>> toDataUsers() {
        return cursorObservable -> cursorObservable
                .map(cursor -> {
                    List<DataUser> users = SqlUtils.convertToList(DataUser.class, cursor);
                    cursor.close();
                    return users;
                });
    }

    public static Observable.Transformer<Cursor, DataUser> toDataUser() {
        return cursorObservable -> cursorObservable
                .map(cursor -> {
                    DataUser user = SqlUtils.convertToModel(false, DataUser.class, cursor);
                    cursor.close();
                    return user;
                });
    }

    public static Observable.Transformer<Cursor, DataConversation> toDataConversation() {
        return cursorObservable -> cursorObservable
                .map(cursor -> {
                    DataConversation user = SqlUtils.convertToModel(false, DataConversation.class, cursor);
                    cursor.close();
                    return user;
                });
    }

    public static Observable.Transformer<Cursor, DataMessage> toDataMessage() {
        return cursorObservable -> cursorObservable.map(cursor -> {
            DataMessage res = SqlUtils.convertToModel(false, DataMessage.class, cursor);
            cursor.close();
            return res;
        });
    }

    public static Observable.Transformer<Cursor, DataTranslation> toDataTranslation() {
        return cursorObservable -> cursorObservable.map(cursor -> {
            DataTranslation dataTranslation = SqlUtils.convertToModel(false, DataTranslation.class, cursor);
            cursor.close();
            return dataTranslation;
        });
    }

    public static Observable.Transformer<Cursor, DataAttachment> toDataAttachment() {
        return cursorObservable -> cursorObservable.map(cursor -> {
            DataAttachment models = SqlUtils.convertToModel(false, DataAttachment.class, cursor);
            cursor.close();
            return models;
        });
    }

    public static Observable.Transformer<Cursor, List<DataAttachment>> toDataAttachments() {
        return cursorObservable -> cursorObservable.map(cursor -> {
            List<DataAttachment> models = SqlUtils.convertToList(DataAttachment.class, cursor);
            cursor.close();
            return models;
        });
    }
}
