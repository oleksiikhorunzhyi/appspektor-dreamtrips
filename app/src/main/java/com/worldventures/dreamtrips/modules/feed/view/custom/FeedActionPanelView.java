package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seppius.i18n.plurals.PluralResources;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public class FeedActionPanelView extends LinearLayout {

    @Optional
    @InjectView(R.id.comments_count)
    TextView tvCommentsCount;
    @Optional
    @InjectView(R.id.comments)
    ImageView comments;
    @Optional
    @InjectView(R.id.likes_count)
    TextView tvLikesCount;
    @Optional
    @InjectView(R.id.likes)
    ImageView likes;

    @InjectView(R.id.feed_share)
    ImageView share;
    @Optional
    @InjectView(R.id.user_who_liked)
    TextView usersWhoLiked;

    OnViewClickListener onCommentIconClickListener;
    OnViewClickListener onLikeIconClickListener;
    OnViewClickListener onLikersClickListener;
    OnViewClickListener onShareClickListener;

    private FeedItem feedItem;

    public FeedActionPanelView(Context context) {
        this(context, null);
    }

    public FeedActionPanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FeedActionPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.adapter_item_feed_comment_footer, this, true);
        ButterKnife.inject(this);
        setOrientation(VERTICAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @OnClick(R.id.likes)
    public void onLikeIconClick() {
        if (onLikeIconClickListener != null) {
            onLikeIconClickListener.onClick(feedItem);
        }
    }

    @OnClick({R.id.comments, R.id.comments_count})
    public void onCommentIconClick() {
        if (onCommentIconClickListener != null) {
            onCommentIconClickListener.onClick(feedItem);
        }
    }

    @OnClick({R.id.user_who_liked, R.id.likes_count})
    public void onLikersClick() {
        if (onLikersClickListener != null) {
            onLikersClickListener.onClick(feedItem);
        }
    }

    @OnClick(R.id.feed_share)
    public void onShareClick() {
        if (onShareClickListener != null) {
            onShareClickListener.onClick(feedItem);
        }
    }

    public void setState(FeedItem feedItem) {

        this.feedItem = feedItem;
        FeedEntity feedEntity = feedItem.getItem();
        try {
            Resources res = getResources();
            FeedEntity item = feedEntity;

            int likesCount = item.getLikesCount();
            int commentsCount = item.getCommentsCount();
            if (likesCount > 0) {
                if (tvLikesCount != null) {
                    tvLikesCount.setVisibility(View.VISIBLE);
                    Spanned text = Html.fromHtml(res.getQuantityString(R.plurals.likes_count, likesCount, likesCount));
                    tvLikesCount.setText(text);
                }

                if (usersWhoLiked != null) {
                    usersWhoLiked.setVisibility(View.VISIBLE);
                    String firstUserName = item.getFirstUserLikedItem();
                    if (firstUserName != null && !TextUtils.isEmpty(firstUserName)) {
                        int stringRes = R.plurals.users_who_liked_with_name;
                        String appeal = firstUserName;
                        if (item.isLiked()) {
                            stringRes = R.plurals.account_who_liked_item;
                            appeal = res.getString(R.string.you);
                        }
                        Spanned text = null;
                        text = Html.fromHtml(new PluralResources(res)
                                .getQuantityString(stringRes, likesCount - 1, appeal, likesCount - 1));

                        usersWhoLiked.setText(text);
                    }
                }

            } else {
                tvLikesCount.setVisibility(View.GONE);
                usersWhoLiked.setVisibility(View.GONE);
            }

            if (tvCommentsCount != null) {
                if (commentsCount > 0) {
                    tvCommentsCount.setVisibility(View.VISIBLE);
                    Spanned text = Html.fromHtml(res.getQuantityString(R.plurals.comments_count, commentsCount, commentsCount));
                    tvCommentsCount.setText(text);
                } else tvCommentsCount.setVisibility(View.GONE);
            }

            if (likes != null) {
                likes.setEnabled(true);
                likes.setImageResource(item.isLiked() ?
                        R.drawable.ic_feed_thumb_up_blue :
                        R.drawable.ic_feed_thumb_up);
            }

            if (comments != null) {
                comments.setEnabled(true);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public void setOnCommentIconClickListener(OnViewClickListener onCommentIconClickListener) {
        this.onCommentIconClickListener = onCommentIconClickListener;
    }

    public void setOnLikeIconClickListener(OnViewClickListener onLikeIconClickListener) {
        this.onLikeIconClickListener = onLikeIconClickListener;
    }

    public void setOnLikersClickListener(OnViewClickListener onLikersClickListener) {
        this.onLikersClickListener = onLikersClickListener;
    }

    public void setOnShareClickListener(OnViewClickListener onShareClickListener) {
        this.onShareClickListener = onShareClickListener;
    }

    public interface OnViewClickListener {
        void onClick(FeedItem feedItem);
    }


}
