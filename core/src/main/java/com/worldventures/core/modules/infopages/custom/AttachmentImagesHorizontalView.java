package com.worldventures.core.modules.infopages.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.worldventures.core.model.EntityStateHolder;
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.ui.view.custom.horizontal_photo_view.StatefulHorizontalPhotosView;

public class AttachmentImagesHorizontalView extends StatefulHorizontalPhotosView<FeedbackImageAttachment, CellDelegate<EntityStateHolder<FeedbackImageAttachment>>> {

   public AttachmentImagesHorizontalView(Context context) {
      super(context);
   }

   public AttachmentImagesHorizontalView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }
}
