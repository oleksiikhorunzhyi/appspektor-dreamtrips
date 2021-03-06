package com.worldventures.dreamtrips.social.ui.feed.view.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.User;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.profile.view.widgets.SmartAvatarView;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class FeedItemCommonDataHelper {

   private final Context context;

   @InjectView(R.id.feed_header_avatar) SmartAvatarView avatar;
   @InjectView(R.id.feed_header_text) TextView text;
   @InjectView(R.id.feed_header_location) TextView location;
   @InjectView(R.id.feed_header_date) TextView date;

   public FeedItemCommonDataHelper(Context context) {
      this.context = context;
   }

   public void attachView(View view) {
      ButterKnife.inject(this, view);
   }

   public void set(FeedItem feedItem, int accountId, Injector injector) {
      Resources res = context.getResources();
      FeedEntity entity = feedItem.getItem();
      try {
         User user = (!feedItem.getLinks().hasUsers()) ? entity.getOwner() : feedItem.getLinks().getUsers().get(0);
         if (user != null) {
            avatar.setImageURI(user.getAvatar() == null ? null : Uri.parse(user.getAvatar().getThumb()));
            avatar.setup(user, injector);
         }
         text.setText(Html.fromHtml(feedItem.infoText(res, accountId)));
         text.setVisibility(TextUtils.isEmpty(text.getText()) ? View.GONE : View.VISIBLE);

         if (TextUtils.isEmpty(entity.place())) {
            location.setVisibility(View.GONE);
         } else {
            location.setVisibility(View.VISIBLE);
            location.setText(entity.place());
         }

         date.setText(DateTimeUtils.convertDateToString(feedItem.getCreatedAt(), DateTimeUtils.FEED_DATE_FORMAT));
      } catch (Exception e) {
         Timber.e(e, "Feed header error");
      }
   }
}
