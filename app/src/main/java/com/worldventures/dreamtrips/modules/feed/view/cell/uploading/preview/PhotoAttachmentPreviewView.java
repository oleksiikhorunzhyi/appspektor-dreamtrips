package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview;

import android.view.ViewGroup;

import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;

import java.util.List;

public interface PhotoAttachmentPreviewView {

   void showPreview(List<PhotoAttachment> attachments);

   void attachView(ViewGroup viewGroup);
}
