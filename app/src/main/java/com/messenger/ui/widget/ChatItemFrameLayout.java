package com.messenger.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.worldventures.dreamtrips.R;

public class ChatItemFrameLayout extends FrameLayout {

    private static final float MESSAGE_SCREEN_WIDTH_SHARE = 0.6f;

    // Use this variable as margins that determine free space left in row after message and avatar took up
    // the space needed.
    private int freeSpaceForMessageRowOwnMessage;
    private int freeSpaceForMessageRowUserMessage;

    @DrawableRes
    private int backgroundForFollowingMessage;
    @DrawableRes
    private int backgroundForInitialMessage;

    private boolean isOwn;

    public ChatItemFrameLayout(Context context) {
        super(context);
        init(null);
    }

    public ChatItemFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) return;

        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.ChatItemFrameLayout);
        backgroundForFollowingMessage = arr.getResourceId(R.styleable.ChatItemFrameLayout_background_following, 0);
        backgroundForInitialMessage = arr.getResourceId(R.styleable.ChatItemFrameLayout_background_initial, 0);
        isOwn = arr.getBoolean(R.styleable.ChatItemFrameLayout_own, false);
        arr.recycle();

        calculateSpacings();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        setMargins((MarginLayoutParams) params);
        super.setLayoutParams(params);
    }

    private void setMargins(ViewGroup.MarginLayoutParams params) {
        int startMargin = isOwn ? freeSpaceForMessageRowOwnMessage : params.leftMargin;
        int endMargin = isOwn ? params.rightMargin : freeSpaceForMessageRowUserMessage;
        params.setMargins(startMargin, params.topMargin, endMargin, params.bottomMargin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setMarginStart(startMargin);
            params.setMarginEnd(endMargin);
        }
    }

    public void setPreviousMessageFromSameUser(boolean previousMessageFromSameUser) {
        setBackgroundResource(previousMessageFromSameUser
                ? backgroundForFollowingMessage : backgroundForInitialMessage);
    }

    private void calculateSpacings() {
        Resources res = getResources();
        int screenWidth = res.getDisplayMetrics().widthPixels;
        int messageWidth = (int) (screenWidth * MESSAGE_SCREEN_WIDTH_SHARE);
        int ownMessageWidth = 2 * res.getDimensionPixelSize(R.dimen.chat_list_item_horizontal_padding)
                + messageWidth;
        freeSpaceForMessageRowOwnMessage = screenWidth - ownMessageWidth;
        int userMessageWidth = ownMessageWidth + res.getDimensionPixelSize(R.dimen.chat_list_item_horizontal_padding)
                + res.getDimensionPixelSize(R.dimen.list_item_small_avatar_image_size);
        freeSpaceForMessageRowUserMessage = screenWidth - userMessageWidth;
    }
}
