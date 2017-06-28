package com.worldventures.dreamtrips.modules.common.view.horizontal_photo_view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import butterknife.InjectView;
import butterknife.OnClick;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.adapter_item_bucket_photo_upload_cell)
public class StatefulPhotoCell<Photo extends IFullScreenObject, Delegate extends CellDelegate<EntityStateHolder<Photo>>>
      extends AbstractDelegateCell<EntityStateHolder<Photo>, Delegate> {

   @InjectView(R.id.imageViewPhoto) SimpleDraweeView ivPhoto;

   @InjectView(R.id.fab_progress) FabButton fabProgress;

   @InjectView(R.id.fabbutton_circle) CircleImageView circleView;

   public StatefulPhotoCell(View view) {
      super(view);
      view.setOnClickListener(v -> onProgressClicked());
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
            fabProgress.setIcon(R.drawable.ic_upload_cloud, R.drawable.ic_upload_cloud);
            fabProgress.setIndeterminate(true);
            int color = ContextCompat.getColor(fabProgress.getContext(), R.color.bucket_blue);
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
            fabProgress.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
            int color = ContextCompat.getColor(fabProgress.getContext(), R.color.bucket_red);
            circleView.setColor(color);
         }
         break;
      }
   }

   @OnClick(R.id.fab_progress)
   void onProgressClicked() {
      cellDelegate.onCellClicked(getModelObject());
   }
}
