package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.apptentive.android.sdk.Log;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoAsCoverRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoDeleteRequestEvent;

import java.io.File;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_bucket_photo_cell)
public class BucketPhotoCellForEdit extends BucketPhotoCell{

    public BucketPhotoCellForEdit(View view) {
        super(view);
    }


    @OnClick(R.id.iv_photo)
    public void onCellClick(View view) {
        showItemDialog(view);
    }

    private void showItemDialog(View view) {
        try {
            String url = getModelObject().getUrl();
            Resources res = view.getResources();
            Drawable d = getDrawableFromCache(url, res);
            MaterialDialog.Builder builder = new MaterialDialog.Builder(view.getContext());
            builder.items(R.array.dialog_action_bucket_photo)
                    .icon(d)
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

    private Drawable getDrawableFromCache(String url, Resources res) {
        File file2 = ImageLoader.getInstance().getDiskCache().get(url);
        Drawable src = Drawable.createFromPath(file2.getAbsolutePath());
        Bitmap bitmap = ((BitmapDrawable) src).getBitmap();
        int newSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, res.getDisplayMetrics());
        return new BitmapDrawable(res, Bitmap.createScaledBitmap(bitmap, newSize, newSize, true));
    }

}
