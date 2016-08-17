package com.messenger.ui.util;

import android.content.Context;
import android.content.res.Resources;

import com.messenger.ui.model.AttachmentMenuItem;
import com.worldventures.dreamtrips.R;

import javax.inject.Inject;

public class AttachmentMenuProvider {

   private Resources res;

   @Inject
   public AttachmentMenuProvider(Context context) {
      this.res = context.getResources();
   }

   public AttachmentMenuItem[] provide() {
      return new AttachmentMenuItem[]{new AttachmentMenuItem(AttachmentMenuItem.LOCATION, res.getString(R.string.chat_share_dialog_location)), new AttachmentMenuItem(AttachmentMenuItem.IMAGE, res
            .getString(R.string.chat_share_dialog_image)), new AttachmentMenuItem(AttachmentMenuItem.CANCEL, res.getString(R.string.chat_share_dialog_cancel))};
   }
}
