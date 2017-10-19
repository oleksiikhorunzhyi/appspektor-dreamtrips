package com.worldventures.dreamtrips.modules.media_picker.view.cell;

import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.modules.picker.model.MediaPickerModelImpl;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;

import butterknife.InjectView;

public abstract class MediaPickerModelCell<T extends MediaPickerModelImpl> extends BaseAbstractDelegateCell<T, CellDelegate<T>> {

   @InjectView(R.id.iv_photo) SimpleDraweeView previewImageView;
   @InjectView(R.id.pick) ImageView pick;

   public MediaPickerModelCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      setImage();

      itemView.setOnClickListener(v -> {
         getModelObject().setChecked(!getModelObject().isChecked());
         getModelObject().setPickedTime(getModelObject().isChecked() ? System.currentTimeMillis() : -1);
         cellDelegate.onCellClicked(getModelObject());
      });

      updatePickState();
   }

   private void updatePickState() {
      if (getModelObject().isChecked()) {
         pick.setImageResource(R.drawable.add_photo_icon_selected);
      } else {
         pick.setImageResource(R.drawable.add_photo_icon);
      }
   }

   private void setImage() {
      ViewUtils.runTaskAfterMeasure(previewImageView, () -> {
         // Fresco has some strange performance issues loading large amount pics with a size of a cell during fast scrolling.
         // Loading them twice smaller provides some balance between quality of the pictures and performance
         PipelineDraweeController controller = GraphicUtils
               .provideFrescoResizingController(getModelObject().getUri(), previewImageView.getController(),
                     previewImageView.getWidth() / 2 , previewImageView.getHeight() / 2);
         previewImageView.setController(controller);
      });
   }
}
