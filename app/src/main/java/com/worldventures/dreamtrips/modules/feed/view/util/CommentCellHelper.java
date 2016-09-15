package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

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
      userName.setText(owner.getUsernameWithCompany(context));
      text.setText(comment.getMessage());
      CharSequence relativeTimeSpanString = DateTimeUtils.getRelativeTimeSpanString(context.getResources(), comment.getCreatedAt()
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
