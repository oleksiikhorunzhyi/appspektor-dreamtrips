package com.messenger.storage.dao;

import android.content.Context;

import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataTranslation$Adapter;
import com.messenger.entities.DataTranslation$Table;
import com.messenger.util.RxContentResolver;

import java.util.Collections;
import java.util.List;

import rx.Observable;

public class TranslationsDAO extends BaseDAO{

    public TranslationsDAO(RxContentResolver rxContentResolver, Context context) {
        super(context, rxContentResolver);
    }

    public Observable<DataTranslation> getTranslation(String id) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataTranslation.TABLE_NAME + " " +
                        "WHERE " + DataTranslation$Table._ID + "=?")
                .withSelectionArgs(new String[] {id})
                .build();
        return query(q, DataTranslation.CONTENT_URI)
                .compose(DaoTransformers.toDataTranslation());
    }

    public void save(List<DataTranslation> dataTranslations){
        bulkInsert(dataTranslations, new DataTranslation$Adapter(), DataTranslation.CONTENT_URI);
    }

    public void save(DataTranslation dataTranslation){
        save(Collections.singletonList(dataTranslation));
    }
}
