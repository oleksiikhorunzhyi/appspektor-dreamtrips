package com.worldventures.dreamtrips.modules.common.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.worldventures.dreamtrips.modules.common.view.custom.tagview.PojoTag;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagViewCreator;

public class TagableImageHolder extends RelativeLayout {

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
        addTagView(x, y, null);
    }

    public void addTagView(float x, float y, PojoTag pojoTag){
        TagView view = new TagViewCreator(pojoTag).build(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        float tagWidth = view.getTagWidthInPx();
        float tagHeight = view.getTagHeightInPx();

        int marginLeft = (int) (x - tagWidth / 2);
        int marginTop = (int) (y - tagHeight / 2);

        if (marginLeft < 0) {
            marginLeft = 0;
        }
        if (marginLeft > getWidth() - tagWidth) {
            marginLeft = 0;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        if (marginTop < 0) {
            marginTop = 0;
        }
        if (marginTop > getHeight() - tagHeight) {
            marginTop = 0;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }

        layoutParams.leftMargin = marginLeft;
        layoutParams.topMargin = marginTop;
        view.setLayoutParams(layoutParams);
        addView(view, layoutParams);
    }

    public interface TagListener {
    }
}
