package com.worldventures.dreamtrips.modules.common.view.dialog;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.ShareType;

public class ShareDialog extends MaterialDialog {

   public ShareDialog(Context context, ShareDialogCallback callback) {
      this(new MaterialDialog.Builder(context).title(R.string.action_share)
            .items(R.array.share_dialog_items)
            .itemsCallback((dialog, view, which, text) -> {
               String type = which == 0 ? ShareType.FACEBOOK : ShareType.TWITTER;
               callback.onShareType(type);
            }));
   }

   protected ShareDialog(Builder builder) {
      super(builder);
   }

   public interface ShareDialogCallback {
      void onShareType(@ShareType String type);
   }
}
