package com.messenger.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.messenger.entities.DataConversation;

public class ConversationAvatarView extends FrameLayout {

    private GroupAvatarsView defaultGroupAvatarsView;

    public ConversationAvatarView(Context context) {
        super(context);
        init();
    }

    public ConversationAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

    }

    public void setConversation(DataConversation dataConversation) {

    }
}
