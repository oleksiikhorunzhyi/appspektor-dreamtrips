package com.worldventures.core.ui.view.custom.horizontal_photo_view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.R;
import com.worldventures.core.model.EntityStateHolder;
import com.worldventures.core.model.ImagePathHolder;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.view.cell.AbstractDelegateCell;
import com.worldventures.core.ui.view.cell.CellDelegate;

import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

public class StatefulPhotoCell<Photo extends ImagePathHolder, Delegate extends CellDelegate<EntityStateHolder<Photo>>>
      extends AbstractDelegateCell<EntityStateHolder<Photo>, Delegate> {

   protected SimpleDraweeView ivPhoto;
   private FabButton fabProgress;
   private CircleImageView circleView;

   public StatefulPhotoCell(View view) {
      super(LayoutInflater.from(view.getContext()).inflate(R.layout.item_horizantal_photo_cell, (ViewGroup)view, false));
      setUpView();
   }

   //TODO unobvious! it called via reflection in AdapterHelper
   public StatefulPhotoCell(ViewGroup viewGroup) {
      super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_horizantal_photo_cell, viewGroup, false));
      setUpView();
   }

   private void setUpView() {
      itemView.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
      ivPhoto = itemView.findViewById(R.id.imageViewPhoto);
      fabProgress = itemView.findViewById(R.id.fab_progress);
      fabProgress.setOnClickListener(fab -> cellDelegate.onCellClicked(getModelObject()));
      circleView = itemView.findViewById(R.id.fabbutton_circle);
   }

   @Override
   protected void syncUIStateWithModel() {
      Photo model = getModelObject().entity();
      EntityStateHolder.State state = getModelObject().state();

      final Uri uri = model.getImagePath() == null ? null : Uri.parse(model.getImagePath());
      final int size = ivPhoto.getResources().getDimensionPixelSize(R.dimen.size_normal);
      final DraweeController controller = Fresco.newDraweeControllerBuilder()
            .setOldController(ivPhoto.getController())
            .setLowResImageRequest(GraphicUtils.createResizeImageRequest(uri, size, size))
            .build();

      ivPhoto.setController(controller);
      switch (state) {
         case PROGRESS: {
            fabProgress.setVisibility(View.VISIBLE);
            fabProgress.setIcon(R.drawable.ic_horizontal_photo_fab_drawable, R.drawable.ic_horizontal_photo_fab_drawable);
            fabProgress.setIndeterminate(true);
            int color = ContextCompat.getColor(fabProgress.getContext(), R.color.horizontal_photo_view_fab_color);
            circleView.setColor(color);
            fabProgress.showProgress(true);
         }
         break;
         case DONE: {
            ivPhoto.getHierarchy().setActualImageFocusPoint(new PointF(0.0f, 0.0f));
            fabProgress.setVisibility(View.GONE);
            fabProgress.showProgress(false);
         }
         break;
         case FAIL: {
            fabProgress.showProgress(false);
            fabProgress.setIcon(R.drawable.ic_horizontal_photo_fab_drawable_retry, R.drawable.ic_horizontal_photo_fab_drawable_retry);
            int color = ContextCompat.getColor(fabProgress.getContext(), R.color.horizontal_photo_view_fab_color_error);
            circleView.setColor(color);
         }
         break;
      }
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
