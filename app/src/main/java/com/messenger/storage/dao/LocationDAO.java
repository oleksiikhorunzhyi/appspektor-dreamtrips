package com.messenger.storage.dao;

import android.content.Context;
import android.net.Uri;

import com.messenger.entities.DataLocationAttachment;
import com.messenger.entities.DataLocationAttachment$Adapter;
import com.messenger.entities.DataLocationAttachment$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

public class LocationDAO extends BaseAttachmentDAO<DataLocationAttachment> {

   public LocationDAO(Context context, RxContentResolver rxContentResolver) {
      super(context, rxContentResolver);
   }

   @Override
   protected ModelAdapter<DataLocationAttachment> getModelAdapter() {
      return new DataLocationAttachment$Adapter();
   }

   @Override
   protected Uri getModelTableUri() {
      return DataLocationAttachment.CONTENT_URI;
   }

   @Override
   protected String getModelTableName() {
      return DataLocationAttachment.TABLE_NAME;
   }

   @Override
   protected String getIDColumnName() {
      return DataLocationAttachment$Table._ID;
   }
}
