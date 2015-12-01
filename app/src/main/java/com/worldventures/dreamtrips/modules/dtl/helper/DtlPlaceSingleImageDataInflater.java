package com.worldventures.dreamtrips.modules.dtl.helper;

import android.graphics.PointF;
import android.net.Uri;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceMedia;

import java.util.List;

import butterknife.InjectView;

public class DtlPlaceSingleImageDataInflater extends DtlPlaceCommonDataInflater {

    @InjectView(R.id.place_details_cover)
    SimpleDraweeView cover;

    final static PointF FOCUS_POINT;

    static {
        FOCUS_POINT = new PointF(0.5f, 0.5f);
    }

    public DtlPlaceSingleImageDataInflater(DtlPlaceHelper helper) {
        super(helper);
    }

    @Override
    protected void onPlaceApply(DTlMerchant place) {
        super.onPlaceApply(place);
        setImage(place.getImages());
    }

    private void setImage(List<DtlPlaceMedia> mediaList) {
        DtlPlaceMedia media = Queryable.from(mediaList).firstOrDefault();
        if (media == null) {
            return;
        }
        //
        cover.getHierarchy().setActualImageFocusPoint(FOCUS_POINT);
        cover.setImageURI(Uri.parse(media.getImagePath()));
    }
}
