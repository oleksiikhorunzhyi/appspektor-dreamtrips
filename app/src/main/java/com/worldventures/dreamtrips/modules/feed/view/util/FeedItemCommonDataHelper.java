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
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

public class FeedItemCommonDataHelper {

    Context context;

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

    public FeedItemCommonDataHelper(Context context) {
        this.context = context;
    }

    public void attachView(View view) {
        ButterKnife.inject(this, view);
    }

    public void set(FeedItem feedItem, int accountId, boolean forDetails) {
        Resources res = context.getResources();
        FeedEntity entity = feedItem.getItem();
        try {
            User user = (forDetails || !feedItem.getLinks().hasUsers()) ? entity.getOwner() : feedItem.getLinks().getUsers().get(0);
            if (user != null) {
                avatar.setImageURI(user.getAvatar() == null ? null : Uri.parse(user.getAvatar().getThumb()));
            }
            text.setText(Html.fromHtml(forDetails ? feedItem.detailsText(res) : feedItem.infoText(res, accountId)));
            text.setVisibility(TextUtils.isEmpty(text.getText()) ? View.GONE : View.VISIBLE);

            if (TextUtils.isEmpty(entity.place())) {
                location.setVisibility(View.GONE);
            } else {
                location.setVisibility(View.VISIBLE);
                location.setText(entity.place());
            }


            date.setText(DateTimeUtils.convertDateToString(feedItem.getCreatedAt(),
                    DateTimeUtils.FEED_DATE_FORMAT));

            int likesCount = entity.getLikesCount();
            int commentsCount = entity.getCommentsCount();
            if (likesCount > 0) {
                if (tvLikesCount != null) {
                    tvLikesCount.setVisibility(View.VISIBLE);
                    Spanned text = Html.fromHtml(String.format(res.getString(
                            QuantityHelper.chooseResource(likesCount, R.string.likes_count_one, R.string.likes_count_other)), likesCount));
                    tvLikesCount.setText(text);
                }


            } else {
                tvLikesCount.setVisibility(View.GONE);
            }

            if (tvCommentsCount != null) {
                if (commentsCount > 0) {
                    tvCommentsCount.setVisibility(View.VISIBLE);
                    Spanned text = Html.fromHtml(String.format(res.getString(
                            QuantityHelper.chooseResource(commentsCount, R.string.comments_count_one, R.string.comments_count_other)), commentsCount));
                    tvCommentsCount.setText(text);
                } else tvCommentsCount.setVisibility(View.GONE);
            }

            if (likes != null) {
                likes.setEnabled(true);
                likes.setImageResource(entity.isLiked() ?
                        R.drawable.ic_feed_thumb_up_blue :
                        R.drawable.ic_feed_thumb_up);
            }

            if (comments != null) {
                comments.setEnabled(true);
            }

            boolean isCurrentUser = entity.getOwner() != null && entity.getOwner().getId() == accountId;
            boolean isEditableItem = feedItem.getType() == FeedEntityHolder.Type.POST
                    || feedItem.getType() == FeedEntityHolder.Type.BUCKET_LIST_ITEM
                    || feedItem.getType() == FeedEntityHolder.Type.PHOTO;
            editFeedItem.setVisibility(isCurrentUser && isEditableItem ? View.VISIBLE : View.GONE);
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
