package com.worldventures.dreamtrips.modules.common.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TagView extends RelativeLayout {
    @InjectView(R.id.btn_close)
    public Button btnClose;
    @InjectView(R.id.editext)
    public EditText editText;

    private float dX, dY;

    public TagView(Context context) {
        super(context);
        initialize();
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_tag_view, this, true);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.btn_close})
    public void onCloseClick(View view) {
        ((ViewGroup) getParent()).removeView(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View parent = (View) getParent();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                bringToFront();
                dX = getX() - event.getRawX();
                dY = getY() - event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                int x = (int) (event.getRawX() + dX);
                int y = (int) (event.getRawY() + dY);
                onDrag();
                animate()
                        .x(x < 0 ? 0 : (x + getWidth() > parent.getWidth()) ? parent.getWidth() - getWidth() : x)       //check bounds
                        .y(y < 0 ? 0 : (y + getHeight() > parent.getHeight()) ? parent.getHeight() - getHeight() : y)   //check bounds
                        .setDuration(0)
                        .start();
                return true;
            case MotionEvent.ACTION_UP:
                finishDrag();
                return true;
            default:
                return false;
        }
    }

     private void onDrag() {
        btnClose.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);
    }

    private void finishDrag() {
        btnClose.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
    }
}
