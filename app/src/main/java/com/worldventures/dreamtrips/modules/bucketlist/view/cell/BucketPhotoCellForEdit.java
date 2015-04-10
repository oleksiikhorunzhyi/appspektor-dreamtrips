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
public class BucketPhotoCellForEdit extends BucketPhotoCell {

    public BucketPhotoCellForEdit(View view) {
        super(view);
    }


}
