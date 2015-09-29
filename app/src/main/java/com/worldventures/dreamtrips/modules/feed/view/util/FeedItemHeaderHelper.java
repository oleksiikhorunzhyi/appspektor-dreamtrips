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
import com.seppius.i18n.plurals.PluralResources;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
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
    @InjectView(R.id.user_who_liked)
    TextView usersWhoLiked;

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

    public void set(FeedItem feedModel, Context context) {
        try {
            User user = feedModel.getLinks().getUsers().get(0);
            avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
            Resources res = context.getResources();
            text.setText(Html.fromHtml(feedModel.infoText(res)));

            if (TextUtils.isEmpty(feedModel.getItem().place())) {
                location.setVisibility(View.GONE);
            } else {
                location.setVisibility(View.VISIBLE);
                location.setText(feedModel.getItem().place());
            }


            date.setText(DateTimeUtils.convertDateToString(feedModel.getCreatedAt(),
                    DateTimeUtils.FEED_DATE_FORMAT));

            int likesCount = feedModel.getItem().getLikesCount();
            int commentsCount = feedModel.getItem().getCommentsCount();
            if (likesCount > 0) {
                if (tvLikesCount != null) {
                    tvLikesCount.setVisibility(View.VISIBLE);
                    Spanned text = Html.fromHtml(res.getQuantityString(R.plurals.likes_count, likesCount, likesCount));
                    tvLikesCount.setText(text);
                }

                if (usersWhoLiked != null) {
                    usersWhoLiked.setVisibility(View.VISIBLE);
                    String firstUser = feedModel.getItem().getFirstUserLikedItem();
                    if (!TextUtils.isEmpty(firstUser)) {
                        Spanned text = Html.fromHtml(new PluralResources(res)
                                .getQuantityString(R.plurals.users_who_liked_with_name, likesCount - 1, firstUser, likesCount - 1));
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
                likes.setImageResource(feedModel.getItem().isLiked() ?
                        R.drawable.ic_feed_thumb_up_blue :
                        R.drawable.ic_feed_thumb_up);
            }

            if (comments != null) {
                comments.setEnabled(true);
            }

        } catch (Exception e) {
            Timber.e(e, "Feed header error");
        }
    }

}
