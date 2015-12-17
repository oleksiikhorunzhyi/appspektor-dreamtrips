package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import com.facebook.drawee.drawable.ScalingUtils;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImageFragment;

@Layout(R.layout.fragment_dtl_image_details)
public class DtlImageFragment extends BaseImageFragment {

    @Override
    public void setSize(boolean fullscreen) {
        super.setSize(fullscreen);
        ivImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
    }
}
