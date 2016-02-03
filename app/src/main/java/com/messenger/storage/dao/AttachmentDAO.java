package com.messenger.storage.dao;

import android.content.Context;

import com.messenger.entities.Attachment;
import com.messenger.entities.Attachment$Adapter;
import com.messenger.util.RxContentResolver;

import java.util.List;

public class AttachmentDAO extends BaseDAO {

    public AttachmentDAO(Context context, RxContentResolver rxContentResolver) {
        super(context, rxContentResolver);
    }

    public void save(List<Attachment> attachments) {
        bulkInsert(attachments, new Attachment$Adapter(), Attachment.CONTENT_URI);
    }

    public void save(Attachment attachment) {
        attachment.save();
    }
}
