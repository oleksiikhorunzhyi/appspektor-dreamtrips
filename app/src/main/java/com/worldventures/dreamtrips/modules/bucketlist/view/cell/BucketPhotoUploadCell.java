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
import com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketPhotoUploadCellDelegate;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;
import timber.log.Timber;

@Layout(R.layout.adapter_item_bucket_photo_upload_cell)
public class BucketPhotoUploadCell extends AbstractDelegateCell<EntityStateHolder<BucketPhoto>, BucketPhotoUploadCellDelegate> {
    @InjectView(R.id.imageViewPhoto)
    SimpleDraweeView ivPhoto;

    @InjectView(R.id.fab_progress)
    FabButton fabProgress;

    @InjectView(R.id.fabbutton_circle)
    CircleImageView circleView;

    public BucketPhotoUploadCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        BucketPhoto model = getModelObject().entity();
        EntityStateHolder.State state = getModelObject().state();

        ivPhoto.setImageURI(Uri.parse(model.getImagePath()));
        switch (state) {
            case PROGRESS: {
                fabProgress.setVisibility(View.VISIBLE);
                fabProgress.setIcon(R.drawable.ic_upload_cloud, R.drawable.ic_upload_cloud);
                fabProgress.setIndeterminate(true);
                int color = fabProgress.getContext().getResources().getColor(R.color.bucket_blue);
                circleView.setColor(color);
                fabProgress.showProgress(true);
            }
            break;
            case DONE: {
                ivPhoto.getHierarchy().setActualImageFocusPoint(new PointF(0.0f, 0.0f));
                ivPhoto.setImageURI(Uri.parse(model.getFSImage().getThumbUrl(itemView.getResources())));

                fabProgress.setVisibility(View.GONE);
                fabProgress.showProgress(false);
            }
            break;
            case FAIL: {
                fabProgress.showProgress(false);
                fabProgress.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
                int color = fabProgress.getContext().getResources().getColor(R.color.bucket_red);
                circleView.setColor(color);
            }
            break;
        }
    }

    @OnLongClick(R.id.imageViewPhoto)
    boolean onImageClicked(View v) {
        showItemDialog(v);
        return true;
    }

    @OnClick(R.id.fab_progress)
    void onCellClick() {
        cellDelegate.onCellClicked(getModelObject());
    }

    private void showItemDialog(View view) {
        try {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(view.getContext());
            builder.items(R.array.dialog_action_bucket_photo)
                    .title(view.getContext().getString(R.string.bucket_photo_dialog))
                    .itemsCallback((dialog, v, which, text) -> {
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
        new MaterialDialog.Builder(context)
                .title(R.string.delete_photo_title)
                .content(R.string.delete_photo_text)
                .positiveText(R.string.delete_photo_positiove)
                .negativeText(R.string.delete_photo_negative)
                .onPositive((materialDialog, dialogAction) -> cellDelegate.deletePhotoRequest(getModelObject().entity()))
                .onNegative((materialDialog, dialogAction) -> materialDialog.dismiss())
                .show();
    }
}