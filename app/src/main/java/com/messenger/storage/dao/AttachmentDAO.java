package com.messenger.storage.dao;

import android.content.Context;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Adapter;
import com.messenger.util.RxContentResolver;

import java.util.List;

public class AttachmentDAO extends BaseDAO {

    public AttachmentDAO(Context context, RxContentResolver rxContentResolver) {
        super(context, rxContentResolver);
    }

    public void save(List<DataAttachment> attachments) {
        bulkInsert(attachments, new DataAttachment$Adapter(), DataAttachment.CONTENT_URI);
    }

    public void save(DataAttachment attachment) {
        attachment.save();
    }
}
