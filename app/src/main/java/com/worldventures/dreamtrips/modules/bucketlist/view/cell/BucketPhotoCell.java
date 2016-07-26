package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketPhotoCellDelegate;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import timber.log.Timber;

@Layout(R.layout.adapter_item_bucket_photo_cell)
public class BucketPhotoCell extends AbstractDelegateCell<BucketPhoto, BucketPhotoCellDelegate> {

    @InjectView(R.id.imageViewPhoto)
    protected SimpleDraweeView imageViewPhoto;

    public BucketPhotoCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        imageViewPhoto.getHierarchy().setActualImageFocusPoint(new PointF(0.0f, 0.0f));
        imageViewPhoto.setImageURI(Uri.parse(getModelObject()
                .getFSImage().getThumbUrl(itemView.getResources())));
    }

    @OnClick(R.id.imageViewPhoto)
    public void onCellClick(View view) {
        showItemDialog(view);
    }

    @OnLongClick(R.id.imageViewPhoto)
    public boolean onCellLongClick(View view) {
        showItemDialog(view);
        return true;
    }

    protected void showItemDialog(View view) {
        try {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(view.getContext());
            builder.items(R.array.dialog_action_bucket_photo)
                    .title(view.getContext().getString(R.string.bucket_photo_dialog))
                    .itemsCallback((dialog, v, which, text) -> {
                        switch (which) {
                            case 0:
                                cellDelegate.choosePhoto(getModelObject());
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
        new MaterialDialog.Builder(context)
                .title(R.string.delete_photo_title)
                .content(R.string.delete_photo_text)
                .positiveText(R.string.delete_photo_positiove)
                .negativeText(R.string.delete_photo_negative)
                .onPositive((materialDialog, dialogAction) -> cellDelegate.deletePhotoRequest(getModelObject()))
                .onNegative((materialDialog, dialogAction) -> materialDialog.dismiss())
                .show();
    }

}
