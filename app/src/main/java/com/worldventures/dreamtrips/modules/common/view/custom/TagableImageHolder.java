package com.worldventures.dreamtrips.modules.common.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TagableImageHolder extends RelativeLayout {

    @InjectView(R.id.iv_image)
    public ImageView imageView;

    public ImageView getImageView() {
        return imageView;
    }

    public TagableImageHolder(Context context) {
        super(context);
    }

    public TagableImageHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagableImageHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagableImageHolder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                addTagView(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void addTagView(float x, float y) {
        View view = new TagView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        float defaultCardSize = getResources().getDimension(R.dimen.tag_zone_default_size);
        int marginLeft = (int) (x - defaultCardSize / 2);
        int marginTop = (int) (y - defaultCardSize / 2);

        if (marginLeft < 0) {
            marginLeft = 0;
        }
        if (marginLeft > getWidth() - defaultCardSize) {
            marginLeft = 0;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        if (marginTop < 0) {
            marginTop = 0;
        }
        if (marginTop > getHeight() - defaultCardSize) { //todo think about edit text above tag rect
            marginTop = 0;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }

        layoutParams.leftMargin = marginLeft;
        layoutParams.topMargin = marginTop;
        view.setLayoutParams(layoutParams);
        addView(view, layoutParams);
    }
}
