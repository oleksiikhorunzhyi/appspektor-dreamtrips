package com.worldventures.dreamtrips.modules.dtl.helper;

import android.net.Uri;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;

import java.util.List;

import butterknife.InjectView;

public class DtlPlaceSingleImageDataInflater extends DtlPlaceCommonDataInflater {

    @InjectView(R.id.place_details_cover)
    SimpleDraweeView cover;

    public DtlPlaceSingleImageDataInflater(DtlPlaceHelper helper) {
        super(helper);
    }

    @Override
    protected void onPlaceApply(DtlMerchant place) {
        super.onPlaceApply(place);
        setImage(place.getImages());
    }

    private void setImage(List<DtlMerchantMedia> mediaList) {
        DtlMerchantMedia media = Queryable.from(mediaList).firstOrDefault();
        if (media == null) {
            return;
        }
        //
        cover.setImageURI(Uri.parse(media.getImagePath()));
    }
}
