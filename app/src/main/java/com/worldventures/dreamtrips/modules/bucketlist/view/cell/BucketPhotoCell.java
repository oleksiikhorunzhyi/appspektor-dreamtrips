package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketPhotoUploadCellDelegate;
import com.worldventures.dreamtrips.modules.common.view.horizontal_photo_view.cell.PhotoCell;

import butterknife.OnLongClick;
import timber.log.Timber;

@Layout(R.layout.adapter_item_bucket_photo_upload_cell)
public class BucketPhotoCell extends PhotoCell<BucketPhoto, BucketPhotoUploadCellDelegate> {

   public BucketPhotoCell(View view) {
      super(view);
   }

   @OnLongClick(R.id.imageViewPhoto)
   boolean onImageClicked(View v) {
      showItemDialog(v);
      return true;
   }

   private void showItemDialog(View view) {
      try {
         MaterialDialog.Builder builder = new MaterialDialog.Builder(view.getContext());
         builder.items(R.array.dialog_action_bucket_photo).title(view.getContext()
               .getString(R.string.bucket_photo_dialog)).itemsCallback((dialog, v, which, text) -> {
            switch (which) {
               case 0:
                  cellDelegate.selectPhotoAsCover(getModelObject().entity());
                  break;
               case 1:
                  showDeleteDialog(view.getContext());
                  break;
               default:
                  Timber.d("default");
                  break;
            }
         }).show();
      } catch (Exception e) {
         Timber.e(e, "");
      }
   }

   private void showDeleteDialog(Context context) {
      new MaterialDialog.Builder(context).title(R.string.delete_photo_title)
            .content(R.string.delete_photo_text)
            .positiveText(R.string.delete_photo_positiove)
            .negativeText(R.string.delete_photo_negative)
            .onPositive((materialDialog, dialogAction) -> cellDelegate.deletePhotoRequest(getModelObject().entity()))
            .onNegative((materialDialog, dialogAction) -> materialDialog.dismiss())
            .show();
   }
}