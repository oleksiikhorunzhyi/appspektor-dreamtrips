package com.messenger.storage.dao;

import android.content.Context;
import android.net.Uri;

import com.messenger.entities.DataAttachment$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;

import rx.Observable;

public abstract class BaseAttachmentDAO<E extends BaseProviderModel> extends BaseDAO {

    protected Class<E> clazz;

    public BaseAttachmentDAO(Context context, RxContentResolver rxContentResolver) {
        super(context, rxContentResolver);
        this.clazz = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void save(List<E> attachments) {
        bulkInsert(attachments, getModelAdapter(), getModelTableUri());
    }

    public void save(E attachment) {
        // BaseProviderModel.save() saves all null strings as "null"(https://github.com/Raizlabs/DBFlow/pull/430)
        save(Collections.singletonList(attachment));
    }

    protected abstract ModelAdapter<E> getModelAdapter();

    protected abstract Uri getModelTableUri();

    protected abstract String getModelTableName();

    protected abstract String getIDColumnName();

    public Observable<E> getAttachmentById(String attachmentId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + getModelTableName() + " " +
                        "WHERE " + getIDColumnName() + "=?")
                .withSelectionArgs(new String[]{attachmentId})
                .build();
        return query(q, getModelTableUri())
                .compose(DaoTransformers.toAttachment(clazz));
    }

    public Observable<E> getAttachmentByMessageId(String messageId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT m.* " +
                        "FROM " + getModelTableName() + " m " +
                        "LEFT JOIN " + DataAttachment$Table.TABLE_NAME + " a " +
                        "ON a." + DataAttachment$Table._ID + "=m." + getIDColumnName() + " " +
                        "WHERE a." + DataAttachment$Table.MESSAGEID + "=?")
                .withSelectionArgs(new String[]{messageId})
                .build();
        return query(q, getModelTableUri())
                .compose(DaoTransformers.toAttachment(clazz));
    }

    public void delete(E attachment) {
        attachment.delete();
    }


}
