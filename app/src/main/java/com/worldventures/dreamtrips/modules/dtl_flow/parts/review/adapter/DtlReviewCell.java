package com.worldventures.dreamtrips.modules.dtl_flow.parts.review.adapter;

import android.view.View;

import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.utils.Size;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_dtl_attach_photo)
public class DtlReviewCell extends BaseAbstractDelegateCell<PhotoPickerModel, CellDelegate<PhotoPickerModel>> {

   @InjectView(R.id.remove) View removeView;
   @InjectView(R.id.review_image) SimpleDraweeView attachedPhotoView;
   @InjectView(R.id.photo_container) View photoContainer;


   public DtlReviewCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      if (itemView.getWidth() > 0) {
         updateUi();
      } else {
         ViewUtils.runTaskAfterMeasure(itemView, this::updateUi);
      }
   }

   protected void updateUi() {
      photoContainer.getLayoutParams().width = itemView.getWidth();
      photoContainer.getLayoutParams().height = calculateHeight();
      photoContainer.requestLayout();
      PipelineDraweeController draweeController = GraphicUtils.provideFrescoResizingController(getModelObject().getUri(), attachedPhotoView
            .getController());
      attachedPhotoView.setController(draweeController);
      removeView.setOnClickListener(click -> cellDelegate.onCellClicked(getModelObject()));
   }

   private int calculateHeight() {
      Size size = getModelObject().getSize();

      int width = size != null ? size.getWidth() : 0;
      int height = size != null ? size.getHeight() : 0;
      int cellWidth = itemView.getWidth();
      //in case of server response width = 0, height = 0;
      if (width == 0 || height == 0) {
         width = cellWidth;
         height = cellWidth;
      }
      return (int) (cellWidth / (float) width * height);
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
