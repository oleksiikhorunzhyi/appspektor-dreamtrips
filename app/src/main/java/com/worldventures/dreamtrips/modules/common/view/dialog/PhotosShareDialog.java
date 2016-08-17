package com.worldventures.dreamtrips.modules.common.view.dialog;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.ShareType;

public class PhotosShareDialog extends ShareDialog {

   public PhotosShareDialog(Context context, ShareDialogCallback callback) {
      super(context, callback);

      getBuilder().items(R.array.share_dialog_with_download_items).itemsCallback((dialog, view, which, text) -> {
         String type;
         switch (which) {
            case 0:
               type = ShareType.FACEBOOK;
               break;
            case 1:
               type = ShareType.TWITTER;
               break;
            case 2:
               type = ShareType.EXTERNAL_STORAGE;
               break;
            default:
               type = ShareType.TWITTER;
         }
         callback.onShareType(type);
      });
   }
}
