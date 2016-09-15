package com.messenger.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.worldventures.dreamtrips.R;

public class ChatItemFrameLayout extends FrameLayout {

   @DrawableRes private int backgroundForFollowingMessage;
   @DrawableRes private int backgroundForInitialMessage;

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
      arr.recycle();
   }

   public void setPreviousMessageFromSameUser(boolean previousMessageFromSameUser) {
      setBackgroundResource(previousMessageFromSameUser ? backgroundForFollowingMessage : backgroundForInitialMessage);
   }
}
