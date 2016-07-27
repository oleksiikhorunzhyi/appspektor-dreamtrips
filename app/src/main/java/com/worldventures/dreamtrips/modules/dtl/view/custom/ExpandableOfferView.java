package com.worldventures.dreamtrips.modules.dtl.view.custom;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.jakewharton.rxbinding.internal.Preconditions;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.AnimationUtils;
import com.worldventures.dreamtrips.modules.profile.view.widgets.ExpandableLayout;

import butterknife.ButterKnife;

public class ExpandableOfferView extends ExpandableLayout {

    private View rotateView;

    public ExpandableOfferView(Context context) {
        super(context);
        setup();
    }

    public ExpandableOfferView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public ExpandableOfferView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {
        rotateView = ButterKnife.findById(this, getRotateViewResourceId());
        Preconditions.checkNotNull(rotateView, "rotate view not found in layout");
    }

    @Override
    protected void expand(View v) {
        super.expand(v);

        if (isAnimationRunning()) return;
        AnimationUtils.rotateByDegrees(rotateView, 180, getDuration());
    }

    @Override
    protected void collapse(View v) {
        super.collapse(v);

        if (isAnimationRunning()) return;
        AnimationUtils.rotateByDegrees(rotateView, 0, getDuration());
    }

    protected int getRotateViewResourceId() {
        return R.id.view_arrow;
    }

    @Override
    public void showWithoutAnimation() {
        super.showWithoutAnimation();
        ViewCompat.setRotation(rotateView, 180);
    }

    @Override
    public void hideWithoutAnimation() {
        super.hideWithoutAnimation();
        ViewCompat.setRotation(rotateView, 0);
    }
}
