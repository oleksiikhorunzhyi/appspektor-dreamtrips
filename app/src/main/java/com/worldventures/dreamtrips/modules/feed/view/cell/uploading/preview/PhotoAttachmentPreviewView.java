package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview;


import android.net.Uri;
import android.view.ViewGroup;

import java.util.List;

public interface PhotoAttachmentPreviewView {

   void showPreview(List<Uri> attachments, boolean animate);

   void attachView(ViewGroup viewGroup);
}
