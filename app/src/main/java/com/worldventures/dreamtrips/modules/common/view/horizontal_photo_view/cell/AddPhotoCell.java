package com.worldventures.dreamtrips.modules.common.view.horizontal_photo_view.cell;

import android.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.AddPhotoModel;

import butterknife.InjectView;
import butterknife.OnClick;


@Layout(R.layout.adapter_item_bucket_photo_cell)
public class AddPhotoCell extends AbstractDelegateCell<AddPhotoModel, CellDelegate<AddPhotoModel>> {

   @InjectView(R.id.imageViewPhoto) protected ImageView ivPhoto;

   public AddPhotoCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      itemView.setVisibility(View.VISIBLE);
      ivPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ivPhoto.setImageResource(R.drawable.ic_bucket_add_pic);
      ivPhoto.setBackgroundColor(ivPhoto.getContext().getResources().getColor(R.color.grey_lighter));


      itemView.setVisibility(getModelObject().isVisible() ? View.VISIBLE : View.GONE);
      ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
      layoutParams.height = getModelObject().isVisible() ? ActionBar.LayoutParams.MATCH_PARENT : 0;
      layoutParams.width = getModelObject().isVisible() ? ActionBar.LayoutParams.MATCH_PARENT : 0;
      itemView.setLayoutParams(layoutParams);
   }

   @OnClick(R.id.imageViewPhoto)
   public void onClick() {
      cellDelegate.onCellClicked(getModelObject());
   }
}