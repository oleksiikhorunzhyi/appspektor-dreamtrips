package com.worldventures.dreamtrips.modules.feed.view.custom.collage.layoutmanager;

import android.view.View;
import android.widget.FrameLayout;

import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageView;

import java.util.ArrayList;
import java.util.List;

class LayoutManagerSingle extends LayoutManager {

    private Size holderSize;

    @Override
    public List<View> getLocatedViews(int holderSide, CollageView.ItemClickListener itemClickListener) {
        int originalWidth = (int) ViewUtils.pxFromDp(context, items.get(0).width);
        float scaleCoefficient = originalWidth < holderSide ? 1 : originalWidth / holderSide;

        int originalHeight = (int) ViewUtils.pxFromDp(context, items.get(0).height);
        int allowableMaxHeight = holderSide / 4 * 5;
        int resultHeight = originalHeight > allowableMaxHeight ? allowableMaxHeight : originalHeight;
        resultHeight /= scaleCoefficient;

        holderSize = new Size(holderSide, resultHeight);

        List<View> views = new ArrayList<>(items.size());
        views.add(getImageView(0, new FrameLayout.LayoutParams(holderSide, resultHeight), itemClickListener));

        return views;
    }

    @Override
    public Size getHolderSize() {
        return holderSize;
    }
}
