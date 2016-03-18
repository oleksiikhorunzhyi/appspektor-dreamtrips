package com.messenger.ui.widget;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.messenger.entities.DataConversation;
import com.messenger.ui.helper.GroupAvatarColorHelper;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GroupAvatarsView extends FrameLayout {

    private static final GroupAvatarColorHelper COLOR_HELPER = new GroupAvatarColorHelper();

    @InjectView(R.id.group_avatar_default_view)
    RoundBackgroundImageView defaultGroupAvatar;
    @InjectView(R.id.group_avatar_custom_view)
    SimpleDraweeView customImageGroupAvatar;

    public GroupAvatarsView(Context context) {
        super(context);
        init();
    }

    public GroupAvatarsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_group_avatars_view, this, true);
        ButterKnife.inject(this, this);
    }

    public void setConversationAvatar(DataConversation conversation) {
        if (TextUtils.isEmpty(conversation.getAvatar())) {
            showDefaultAvatar(conversation.getId());
            hideCustomAvatar();
        } else {
            loadCustomAvatar(conversation);
        }
    }

    private void showDefaultAvatar(String conversationId) {
        defaultGroupAvatar.setRoundBackgroundColor(COLOR_HELPER.obtainColor(getContext(), conversationId));
        defaultGroupAvatar.setVisibility(VISIBLE);
    }

    private void hideDefaultAvatar() {
        defaultGroupAvatar.setVisibility(GONE);
    }

    private void showCustomAvatar() {
        customImageGroupAvatar.setVisibility(VISIBLE);
    }

    private void hideCustomAvatar() {
        customImageGroupAvatar.setVisibility(GONE);
    }

    private void loadCustomAvatar(DataConversation conversation) {
        // show default avatar first as placeholder,
        // while loading the pic custom avatar will be transparent
        showDefaultAvatar(conversation.getId());
        showCustomAvatar();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        hideDefaultAvatar();
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        hideCustomAvatar();
                        showDefaultAvatar(conversation.getId());
                    }
                })
                .setOldController(customImageGroupAvatar.getController())
                .setUri(Uri.parse(conversation.getAvatar()))
                .build();
        customImageGroupAvatar.setController(controller);
    }
}
