package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;

import java.util.List;

import butterknife.InjectView;

public class DtlMerchantSingleImageDataInflater extends DtlMerchantCommonDataInflater {

    @InjectView(R.id.merchant_details_cover)
    SimpleDraweeView cover;

    @Override
    protected void onMerchantApply(DtlMerchant merchant) {
        super.onMerchantApply(merchant);
        setImage(merchant.getImages());
    }

    private void setImage(List<DtlMerchantMedia> mediaList) {
        DtlMerchantMedia media = Queryable.from(mediaList).firstOrDefault();
        if (media == null) {
            return;
        }
        //
        cover.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (v.getWidth() == 0) return;
                cover.removeOnLayoutChangeListener(this);
                cover.setController(GraphicUtils.provideFrescoResizingController(
                        Uri.parse(media.getImagePath()), cover.getController(),
                        cover.getWidth(), cover.getHeight())
                );
            }
        });
    }
}
