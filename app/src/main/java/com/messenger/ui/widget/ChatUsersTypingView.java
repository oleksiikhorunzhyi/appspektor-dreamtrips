package com.messenger.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.messenger.entities.DataUser;
import com.messenger.ui.model.ChatUser;
import com.messenger.ui.anim.SimpleAnimatorListener;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

public class ChatUsersTypingView extends RelativeLayout {

    public static final int TYPING_ANIM_FADE_IN_MS = 300;
    public static final int TYPING_ANIM_NEXT_ANIM_DELAY_MS = 300;

    @InjectViews({ R.id.chat_typing_avatar_1, R.id.chat_typing_avatar_2, R.id.chat_typing_avatar_3})
    List<SimpleDraweeView> avatarImageViews;
    @InjectViews({ R.id.chat_typing_circle_1, R.id.chat_typing_circle_2, R.id.chat_typing_circle_3})
    List<ImageView> typingCircles;
    @InjectView(R.id.chat_typing_layout_usernames_textview) TextView chatTypingTextView;

    private AnimatorSet typingAnimatorSet = new AnimatorSet();
    private boolean cancelTypingAnimation;

    private ValueAnimator showViewAnimator;
    private ValueAnimator hideViewAnimator;

    private List<ChatUser> typingUsers = new ArrayList<>();

    public ChatUsersTypingView(Context context) {
        super(context);
        init(context);
    }

    public ChatUsersTypingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_chat_users_typing, this, true);
        ButterKnife.inject(this, this);
        initTypingAnimator();
        int verticalPadding = getResources().getDimensionPixelSize(R.dimen.chat_typing_vertical_margin);
        int horizontalPadding = getResources().getDimensionPixelSize(R.dimen.chat_list_item_horizontal_padding);
        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
    }

    private void initTypingAnimator() {
        typingAnimatorSet = new AnimatorSet();
        ObjectAnimator firstDotAnimator = getFadeInAnimator(0, TYPING_ANIM_NEXT_ANIM_DELAY_MS);
        ObjectAnimator secondDotAnimator = getFadeInAnimator(1, TYPING_ANIM_NEXT_ANIM_DELAY_MS);
        ObjectAnimator thirdDotAnimator = getFadeInAnimator(2, TYPING_ANIM_NEXT_ANIM_DELAY_MS);
        typingAnimatorSet.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (cancelTypingAnimation) {
                    return;
                }
                for (View view : typingCircles) {
                    view.setVisibility(View.INVISIBLE);
                }
                // Update UI along with animation loop
                post(typingAnimatorSet::start);
            }
        });
        typingAnimatorSet.playSequentially(firstDotAnimator, secondDotAnimator, thirdDotAnimator);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        typingAnimatorSet.start();
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelTypingAnimation = true;
        typingAnimatorSet.cancel();
        if (showViewAnimator != null) {
            showViewAnimator.cancel();
            showViewAnimator = null;
        }
        if (hideViewAnimator != null) {
            hideViewAnimator.cancel();
            hideViewAnimator = null;
        }
    }

    private ObjectAnimator getFadeInAnimator(int index, int delay) {
        final View view = typingCircles.get(index);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        animator.setDuration(TYPING_ANIM_FADE_IN_MS);
        animator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }
        });
        animator.setStartDelay(delay);
        return animator;
    }

    public void changeTypingUsers(List<DataUser> users) {
        typingUsers.clear();
        typingUsers.addAll(users);
        updateUI();
    }

    private void updateUI() {
        if (typingUsers.isEmpty()) {
            hideView();
            typingAnimatorSet.cancel();
            return;
        }

        boolean hideViewAnimatorRunning = hideViewAnimator != null && hideViewAnimator.isRunning();
        if (getVisibility() != VISIBLE || hideViewAnimatorRunning) {
            if (hideViewAnimatorRunning) {
                hideViewAnimator.cancel();
            }
            showView();
        }

        if (!typingAnimatorSet.isStarted()) {
            typingAnimatorSet.start();
        }

        for (int i = 0; i < avatarImageViews.size(); i++) {
            final SimpleDraweeView imageView = avatarImageViews.get(i);
            if (i <= typingUsers.size() - 1) {
                if (imageView.getVisibility() != VISIBLE) {
                    imageView.setVisibility(VISIBLE);
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
                    fadeIn.start();
                }
                ChatUser user = typingUsers.get(i);
                imageView.setImageURI(Uri.parse(user.getAvatarUrl()));
            } else {
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);
                fadeOut.start();
                fadeOut.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        imageView.setVisibility(GONE);
                    }
                });
            }
        }
        if (typingUsers.size() > avatarImageViews.size()) {
            chatTypingTextView.setVisibility(VISIBLE);
            int sizeDiff = typingUsers.size() - avatarImageViews.size();
            String moreTypingUsers = String.format(getContext().getString(R.string.chat_more_typing_users_hint_format),
                    sizeDiff);
            chatTypingTextView.setText(moreTypingUsers);
        } else {
            chatTypingTextView.setVisibility(INVISIBLE);
        }
    }

    private void hideView() {
        if (hideViewAnimator != null) return;

        ValueAnimator animator = ValueAnimator.ofFloat(0, -getMeasuredHeight());
        animator.addUpdateListener(this::animatorUpdateListener);
        animator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                setVisibility(View.GONE);
                hideViewAnimator = null;
            }
        });
        animator.start();
        hideViewAnimator = animator;
    }

    private void showView() {
        if (showViewAnimator != null) return;

        setVisibility(View.VISIBLE);
        if (getMeasuredHeight() == 0) {
            int x = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            measure(x, x);
        }

        ValueAnimator animator = ValueAnimator.ofFloat(-getMeasuredHeight(), 0);
        animator.addUpdateListener(this::animatorUpdateListener);
        animator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                showViewAnimator = null;
            }
        });

        animator.start();
        showViewAnimator = animator;
    }

    private void animatorUpdateListener(ValueAnimator valueAnimator){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        float margin = (Float) valueAnimator.getAnimatedValue();
        params.bottomMargin = (int) margin;
        requestLayout();
    }
}
