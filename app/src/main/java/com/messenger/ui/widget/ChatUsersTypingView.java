package com.messenger.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;
import com.messenger.model.ChatUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.ButterKnife;
import butterknife.InjectViews;

public class ChatUsersTypingView extends RelativeLayout {

    public static final int TYPING_ANIM_FADE_IN_MS = 300;
    public static final int TYPING_ANIM_NEXT_ANIM_DELAY_MS = 300;

    @InjectViews({ R.id.chat_typing_avatar_1, R.id.chat_typing_avatar_2, R.id.chat_typing_avatar_3})
    List<ImageView> avatarImageViews;
    @InjectViews({ R.id.chat_typing_circle_1, R.id.chat_typing_circle_2, R.id.chat_typing_circle_3})
    List<ImageView> typingCircles;
    @InjectView(R.id.chat_typing_layout_usernames_textview) TextView chatTypingTextView;

    private AnimatorSet typingAnimatorSet = new AnimatorSet();
    private boolean cancelTypingAnimation;

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
        // initTypingAnimator();
    }

    private void initTypingAnimator() {
        typingAnimatorSet = new AnimatorSet();
        ObjectAnimator firstDotAnimator = getFadeInAnimator(0, TYPING_ANIM_NEXT_ANIM_DELAY_MS);
        ObjectAnimator secondDotAnimator = getFadeInAnimator(1, TYPING_ANIM_NEXT_ANIM_DELAY_MS);
        ObjectAnimator thirdDotAnimator = getFadeInAnimator(2, TYPING_ANIM_NEXT_ANIM_DELAY_MS);
        typingAnimatorSet.addListener(new SimpleAnimationListener() {
            @Override public void onAnimationEnd(Animator animator) {
                if (cancelTypingAnimation) {
                    return;
                }
                for (View view : typingCircles) {
                    view.setVisibility(View.INVISIBLE);
                }
                // Update UI along with animation loop
                updateUI();
                typingAnimatorSet.start();
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
        typingAnimatorSet = null;
    }

    private ObjectAnimator getFadeInAnimator(int index, int delay) {
        final View view = typingCircles.get(index);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        animator.setDuration(TYPING_ANIM_FADE_IN_MS);
        animator.addListener(new SimpleAnimationListener() {
            @Override public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }
        });
        animator.setStartDelay(delay);
        return animator;
    }

    public void updateUsersTyping(List<ChatUser> typingUsers) {
        this.typingUsers = typingUsers;
        // update UI later along with animation loop
    }

    private void updateUI() {
        if (typingUsers == null || typingUsers.isEmpty()) {
            setVisibility(GONE);
            typingAnimatorSet.cancel();
            return;
        }

        setVisibility(VISIBLE);

        if (!typingAnimatorSet.isStarted()) {
            typingAnimatorSet.start();
        }

        for (int i = 0; i < avatarImageViews.size(); i++) {
            final ImageView imageView = avatarImageViews.get(i);
            if (i <= typingUsers.size() - 1) {
                if (imageView.getVisibility() != VISIBLE) {
                    imageView.setVisibility(VISIBLE);
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
                    fadeIn.start();
                }
                ChatUser user = typingUsers.get(i);
                Picasso.with(getContext()).load(user.getAvatarUrl())
                        .placeholder(android.R.drawable.ic_menu_compass)
                        .into(imageView);
            } else {
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);
                fadeOut.start();
                fadeOut.addListener(new SimpleAnimationListener() {
                    @Override public void onAnimationEnd(Animator animator) {
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

    private class SimpleAnimationListener implements Animator.AnimatorListener {
        @Override public void onAnimationStart(Animator animator) {
        }

        @Override public void onAnimationEnd(Animator animator) {
        }

        @Override public void onAnimationCancel(Animator animator) {
        }

        @Override public void onAnimationRepeat(Animator animator) {
        }
    }
}
