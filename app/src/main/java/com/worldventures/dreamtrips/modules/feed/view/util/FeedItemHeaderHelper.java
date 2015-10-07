package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

public class FeedItemHeaderHelper {
    @InjectView(R.id.feed_header_avatar)
    SimpleDraweeView avatar;
    @InjectView(R.id.feed_header_text)
    TextView text;
    @InjectView(R.id.feed_header_location)
    TextView location;
    @InjectView(R.id.feed_header_date)
    TextView date;

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

    @InjectView(R.id.edit_feed_item)
    ImageView editFeedItem;

    public FeedItemHeaderHelper() {
    }

    public void set(FeedItem feedItem, Context context, int accountId) {
        try {
            User user = feedItem.getItem().getUser();
            avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
            Resources res = context.getResources();
            text.setText(Html.fromHtml(feedItem.infoText(res, accountId)));

            if (TextUtils.isEmpty(feedItem.getItem().place())) {
                location.setVisibility(View.GONE);
            } else {
                location.setVisibility(View.VISIBLE);
                location.setText(feedItem.getItem().place());
            }


            date.setText(DateTimeUtils.convertDateToString(feedItem.getCreatedAt(),
                    DateTimeUtils.FEED_DATE_FORMAT));

            int likesCount = feedItem.getItem().getLikesCount();
            int commentsCount = feedItem.getItem().getCommentsCount();
            if (likesCount > 0) {
                if (tvLikesCount != null) {
                    tvLikesCount.setVisibility(View.VISIBLE);
                    Spanned text = Html.fromHtml(res.getQuantityString(R.plurals.likes_count, likesCount, likesCount));
                    tvLikesCount.setText(text);
                }


            } else {
                tvLikesCount.setVisibility(View.GONE);
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
                likes.setImageResource(feedItem.getItem().isLiked() ?
                        R.drawable.ic_feed_thumb_up_blue :
                        R.drawable.ic_feed_thumb_up);
            }

            if (comments != null) {
                comments.setEnabled(true);
            }

            boolean isCurrentUser = feedItem.getItem().getUser() != null && feedItem.getItem().getUser().getId() == accountId;
            boolean isEditableType = feedItem.getType() == FeedEntityHolder.Type.POST || feedItem.getType() == FeedEntityHolder.Type.BUCKET_LIST_ITEM;
            editFeedItem.setVisibility(isCurrentUser && isEditableType ? View.VISIBLE : View.GONE);

        } catch (Exception e) {
            Timber.e(e, "Feed header error");
        }
    }

    public void setOnEditClickListener(View.OnClickListener onEditClickListener) {
        if (editFeedItem != null) {
            editFeedItem.setOnClickListener(onEditClickListener);
        }
    }
}
