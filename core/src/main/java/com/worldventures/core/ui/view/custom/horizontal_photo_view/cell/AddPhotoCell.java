package com.worldventures.core.ui.view.custom.horizontal_photo_view.cell;

import android.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.worldventures.core.R;
import com.worldventures.core.ui.view.cell.AbstractDelegateCell;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.ui.view.custom.horizontal_photo_view.model.AddPhotoModel;

public class AddPhotoCell extends AbstractDelegateCell<AddPhotoModel, CellDelegate<AddPhotoModel>> {

   private ImageView ivPhoto;

   public AddPhotoCell(View view) {
      super(view);
      setUpView();
   }

   public AddPhotoCell(ViewGroup viewGroup) {
      super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_horizontal_photo_add, viewGroup, false));
      setUpView();
   }

   private void setUpView() {
      ivPhoto = itemView.findViewById(R.id.imageViewPhoto);
      ivPhoto.setOnClickListener(imgView -> cellDelegate.onCellClicked(getModelObject()));
   }

   @Override
   protected void syncUIStateWithModel() {
      itemView.setVisibility(View.VISIBLE);
      ivPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ivPhoto.setImageResource(R.drawable.ic_horizontal_photo_add);
      ivPhoto.setBackgroundColor(ivPhoto.getContext().getResources().getColor(R.color.grey_lighter));


      itemView.setVisibility(getModelObject().isVisible() ? View.VISIBLE : View.GONE);
      ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
      layoutParams.height = getModelObject().isVisible() ? ActionBar.LayoutParams.MATCH_PARENT : 0;
      layoutParams.width = getModelObject().isVisible() ? ActionBar.LayoutParams.MATCH_PARENT : 0;
      itemView.setLayoutParams(layoutParams);
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}