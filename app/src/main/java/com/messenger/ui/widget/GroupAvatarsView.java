package com.messenger.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.messenger.entities.DataConversation;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.helper.GroupAvatarColorHelper;
import com.worldventures.dreamtrips.R;

public class GroupAvatarsView extends SimpleDraweeView {

    private static final GroupAvatarColorHelper COLOR_HELPER = new GroupAvatarColorHelper();

    public GroupAvatarsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupAvatarsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GroupAvatarsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        getHierarchy().setFadeDuration(0);
        getHierarchy().setRoundingParams(RoundingParams.asCircle());
    }

    public void setConversationAvatar(DataConversation conversation) {
        if (ConversationHelper.isTripChat(conversation)) {
            setTripChatAvatar();
        } else {
            setGroupChatAvatar(conversation);
        }
    }

    private void setGroupChatAvatar(DataConversation conversation) {
        Drawable drawable = new RoundDrawableWrapper.Builder()
                .drawable(ContextCompat.getDrawable(getContext(), R.drawable.regular_group))
                .color(COLOR_HELPER.obtainColor(getContext(), conversation.getId()))
                .build();

        String avatarUrl = conversation.getAvatar();
        getHierarchy().setPlaceholderImage(drawable);
        getHierarchy().setFailureImage(drawable);
        setController(Fresco.newDraweeControllerBuilder()
                .setOldController(getController())
                .setUri(TextUtils.isEmpty(avatarUrl) ? Uri.EMPTY : Uri.parse(avatarUrl))
                .build());
    }

    private void setTripChatAvatar() {
        getHierarchy().setFailureImage(ContextCompat.getDrawable(getContext(), R.drawable.ic_trip_chat));
        // workaround for Fresco bug that does not work properly with VectorDrawable when
        // method like getHierarchy().setPlaceholderImage(drawable, ScalingUtils.ScaleType.CENTER) is called
        // which required to allow proper scaling of images for Android 4.x
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithResourceId(R.drawable.ic_trip_chat)
                .build();
        setController(Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .build());
    }
}
