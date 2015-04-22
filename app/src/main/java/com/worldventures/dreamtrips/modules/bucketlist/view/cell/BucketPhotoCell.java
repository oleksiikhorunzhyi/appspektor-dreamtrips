package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.apptentive.android.sdk.Log;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoAsCoverRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoDeleteRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;

@Layout(R.layout.adapter_item_bucket_photo_cell)
public class BucketPhotoCell extends AbstractCell<BucketPhoto> {

    @InjectView(R.id.imageViewPhoto)
    protected SimpleDraweeView imageViewPhoto;

    public BucketPhotoCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        imageViewPhoto.setImageURI(Uri.parse(getModelObject()
                .getFSImage().getThumbUrl(itemView.getResources())));
    }

    @Override
    public void prepareForReuse() {
        Log.v(this.getClass().getSimpleName(), "prepareForReuse");
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
            Drawable topLevelDrawable = imageViewPhoto.getTopLevelDrawable();
            builder.items(R.array.dialog_action_bucket_photo)
                    .icon(imageViewPhoto.getHierarchy().getTopLevelDrawable().getCurrent())
                    .title(view.getContext().getString(R.string.bucket_photo_dialog))
                    .itemsCallback((dialog, v, which, text) -> {
                        switch (which) {
                            case 0:
                                getEventBus().post(new BucketPhotoAsCoverRequestEvent(getModelObject()));
                                break;
                            case 1:
                                showDeleteDialog(view.getContext());
                                break;
                            default:
                                Log.v(this.getClass().getSimpleName(), "default");
                                break;
                        }
                    }).show();
        } catch (Exception e) {
            Log.e("", "", e);
        }
    }

    private void showDeleteDialog(Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.delete_photo_title)
                .content(R.string.delete_photo_text)
                .positiveText(R.string.delete_photo_positiove)
                .negativeText(R.string.delete_photo_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        getEventBus().post(new BucketPhotoDeleteRequestEvent(getModelObject()));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

}
