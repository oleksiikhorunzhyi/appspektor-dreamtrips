package com.messenger.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.messenger.ui.helper.GroupAvatarColorHelper;
import com.worldventures.dreamtrips.R;

public class GroupAvatarsView extends ImageView {

    private static GroupAvatarColorHelper colorHelper;
    private Paint paint;

    public GroupAvatarsView(Context context) {
        super(context);
        init();
    }

    public GroupAvatarsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        colorHelper = new GroupAvatarColorHelper();
        paint = new Paint();

        setImageResource(R.drawable.regular_group);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setConversationAvatar(String groupConversationId) {
        paint.setColor(colorHelper.obtainColor(getContext(), groupConversationId));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int radius = getMeasuredHeight() / 2;
        canvas.drawCircle(radius, radius, radius, paint);
        super.onDraw(canvas);
    }
}
