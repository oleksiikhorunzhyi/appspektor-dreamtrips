package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NewTagView extends TagView {

    @InjectView(R.id.new_user_input_name)
    public AutoCompleteTextView inputFriendName;

    private float dX, dY; //values for drag

    //region Constructors
    public NewTagView(Context context) {
        super(context);
    }

    public NewTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NewTagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //endregion

    @Override
    protected void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_tag_view_new, this, true);
        ButterKnife.inject(this);

        TagFriendAdapter adapter = new TagFriendAdapter(getContext(), userFriends);
        inputFriendName.setAdapter(adapter);
        inputFriendName.setDropDownBackgroundResource(R.drawable.background_common_tag_view);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
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

    @OnClick ({R.id.new_user_delete_tag})
    public void onClick(){
        deleteTag();
    }

    private void onDrag() {
    }

    private void finishDrag() {
    }
}
