package com.messenger.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.messenger.entities.User;
import com.messenger.model.ChatUser;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectViews;

public class GroupAvatarsView extends GridLayout {

    private static final int MAX_AVATARS_COUNT = 4;
    private static final int COLUMN_COUNT = 2;
    private static final int ROWS_COUNT = 2;

    @InjectViews({R.id.conversation_avatar_1, R.id.conversation_avatar_2,
            R.id.conversation_avatar_3, R.id.conversation_avatar_4})
    List<SimpleDraweeView> avatarImageViews;

    public GroupAvatarsView(Context context) {
        super(context);
        init(null);
    }

    public GroupAvatarsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_group_avatar, this, true);
        setColumnCount(COLUMN_COUNT);
        setRowCount(ROWS_COUNT);
        ButterKnife.inject(this, this);
        if (attrs != null) {
            TypedArray a = getContext().getTheme()
                    .obtainStyledAttributes(attrs, R.styleable.GroupAvatarsView, 0, 0);
            try {
                assignAvatarSizesAndSpacing(a);
            } finally {
                a.recycle();
            }
        }
    }

    private void assignAvatarSizesAndSpacing(TypedArray a) {
        int avatarSize = a.getDimensionPixelSize(R.styleable.GroupAvatarsView_gav_avatar_size,
                getResources().getDimensionPixelSize(R.dimen.list_item_small_avatar_image_size));
        int avatarSpacing = a.getDimensionPixelSize(R.styleable.GroupAvatarsView_gav_avatar_spacing,
                getResources().getDimensionPixelSize(R.dimen.list_item_small_avatar_image_spacing));
        for (int i = 0; i < avatarImageViews.size(); i++) {
            ImageView imageView = avatarImageViews.get(i);
            MarginLayoutParams params = (MarginLayoutParams) imageView.getLayoutParams();
            params.width = avatarSize;
            params.height = avatarSize;
            switch (imageView.getId()) {
                case R.id.conversation_avatar_2:
                    params.leftMargin = avatarSpacing;
                    break;
                case R.id.conversation_avatar_3:
                    params.topMargin = avatarSpacing;
                    break;
                case R.id.conversation_avatar_4:
                    params.leftMargin = avatarSpacing;
                    params.topMargin = avatarSpacing;
                    break;
            }
        }
    }

    public void updateAvatars(List<User> chatUsers) {
        for (int i = 0; i < MAX_AVATARS_COUNT; i++) {
            SimpleDraweeView avatarImageView = avatarImageViews.get(i);
            if (i > chatUsers.size() - 1) {
                changeVisibility(avatarImageView, INVISIBLE);
            } else {
                changeVisibility(avatarImageView, VISIBLE);
                ChatUser chatUser = chatUsers.get(i);
                avatarImageView.setImageURI(Uri.parse(chatUser.getAvatarUrl()));
            }
        }
    }

    private void changeVisibility(View view, int visible) {
        if (view.getVisibility() != visible) {
            view.setVisibility(visible);
        }
    }
}
