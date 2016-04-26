package com.worldventures.dreamtrips.modules.feed.view.custom.collage.layoutmanager;

import android.view.Gravity;
import android.view.View;

import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageView;

import java.util.ArrayList;
import java.util.List;

class LayoutManagerThree extends LayoutManager {

    private Size holderSize;

    @Override
    public List<View> getLocatedViews(int holderSide, CollageView.ItemClickListener itemClickListener) {
        List<View> views = new ArrayList<>(items.size());

        int firstType = getType(items.get(0));
        int secondType = getType(items.get(1));
        int thirdType = getType(items.get(2));
        if (firstType == LANDSCAPE && secondType == LANDSCAPE && thirdType == LANDSCAPE) {
            views.add(getImageView(0, getLayoutParams(holderSide, holderSide * 2 / 3), getPaddings(0, 0, 0, halfPadding), itemClickListener));
            views.add(getImageView(1, getLayoutParams(holderSide / 2, holderSide / 3, Gravity.BOTTOM), getPaddings(0, halfPadding, halfPadding, 0), itemClickListener));
            views.add(getImageView(2, getLayoutParams(holderSide / 2, holderSide / 3, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0), itemClickListener));
            holderSize = new Size(holderSide, holderSide);
        } else if (firstType == PORTRAIT && secondType == PORTRAIT && thirdType == PORTRAIT) {
            views.add(getImageView(0, getLayoutParams(holderSide * 2 / 3, holderSide), getPaddings(0, 0, halfPadding, 0), itemClickListener));
            views.add(getImageView(1, getLayoutParams(holderSide / 3, holderSide / 2, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding), itemClickListener));
            views.add(getImageView(2, getLayoutParams(holderSide / 3, holderSide / 2, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0), itemClickListener));
            holderSize = new Size(holderSide, holderSide);
        } else if (firstType == LANDSCAPE) {
            views.add(getImageView(0, getLayoutParams(holderSide, holderSide / 2), getPaddings(0, 0, 0, halfPadding), itemClickListener));
            views.add(getImageView(1, getLayoutParams(holderSide / 2, holderSide / 2, Gravity.BOTTOM), getPaddings(0, halfPadding, halfPadding, 0), itemClickListener));
            views.add(getImageView(2, getLayoutParams(holderSide / 2, holderSide / 2, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0), itemClickListener));
            holderSize = new Size(holderSide, holderSide);
        } else if (firstType == PORTRAIT) {
            views.add(getImageView(0, getLayoutParams(holderSide / 2, holderSide), getPaddings(0, 0, halfPadding, 0), itemClickListener));
            views.add(getImageView(1, getLayoutParams(holderSide / 2, holderSide / 2, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding), itemClickListener));
            views.add(getImageView(2, getLayoutParams(holderSide / 2, holderSide / 2, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0), itemClickListener));
            holderSize = new Size(holderSide, holderSide);
        } else {
            views.add(getImageView(0, getLayoutParams(holderSide * 2 / 3, holderSide * 2 / 3), getPaddings(0, 0, halfPadding, 0), itemClickListener));
            views.add(getImageView(1, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding), itemClickListener));
            views.add(getImageView(2, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0), itemClickListener));
            holderSize = new Size(holderSide, holderSide * 2 / 3);
        }

        return views;
    }

    @Override
    public Size getHolderSize() {
        return holderSize;
    }
}
