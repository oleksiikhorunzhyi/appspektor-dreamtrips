package com.worldventures.dreamtrips.social.ui.feed.view.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.utils.TimeUtils;
import com.worldventures.dreamtrips.modules.common.utils.UserUtils;
import com.worldventures.dreamtrips.social.ui.profile.view.widgets.SmartAvatarView;
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class CommentCellHelper {

   @Optional @InjectView(R.id.comment_container) View commentContainer;
   @InjectView(R.id.user_photo) SmartAvatarView userPhoto;
   @InjectView(R.id.user_name) TextView userName;
   @InjectView(R.id.date) TextView date;
   @InjectView(R.id.text) TextView text;

   private Context context;
   private Comment comment;

   public CommentCellHelper(Context context) {
      this.context = context;
   }

   public void attachView(View view) {
      ButterKnife.inject(this, view);
   }

   public void set(@NonNull Comment comment, Injector injector) {
      this.comment = comment;
      //
      User owner = comment.getOwner();
      userPhoto.setImageURI(Uri.parse(owner.getAvatar().getThumb()));
      userPhoto.setup(comment.getOwner(), injector);
      userName.setText(UserUtils.getUsernameWithCompany(context, owner));
      text.setText(comment.getMessage());
      CharSequence relativeTimeSpanString = TimeUtils.getRelativeTimeSpanString(context.getResources(), comment.getCreatedAt()
            .getTime());
      date.setText(relativeTimeSpanString);
   }

   public Comment getComment() {
      return comment;
   }

   public void showContainer() {
      commentContainer.setVisibility(View.VISIBLE);
   }

   public void hideContainer() {
      commentContainer.setVisibility(View.GONE);
   }
}
