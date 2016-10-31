package com.worldventures.dreamtrips.modules.infopages.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.view.horizontal_photo_view.StatefulHorizontalPhotosView;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;

public class AttachmentImagesHorizontalView extends StatefulHorizontalPhotosView<FeedbackImageAttachment, CellDelegate<EntityStateHolder<FeedbackImageAttachment>>> {

   public AttachmentImagesHorizontalView(Context context) {
      super(context);
   }

   public AttachmentImagesHorizontalView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }
}
