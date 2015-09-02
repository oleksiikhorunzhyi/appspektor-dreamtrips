package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import butterknife.InjectView;

public class CommentCellHelper {

    @InjectView(R.id.user_photo)
    SimpleDraweeView userPhoto;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.date)
    TextView date;
    @InjectView(R.id.text)
    TextView text;

    private Comment comment;

    public void set(@NonNull Context context, @NonNull Comment comment) {
        this.comment = comment;
        User owner = comment.getOwner();
        userPhoto.setImageURI(Uri.parse(owner.getAvatar().getThumb()));
        userName.setText(owner.getUsernameWithCompany(context));
        text.setText(comment.getMessage());
        CharSequence relativeTimeSpanString = DateTimeUtils.getRelativeTimeSpanString(context.getResources(),
                comment.getCreatedAt().getTime());
        date.setText(relativeTimeSpanString);
    }

    public Comment getComment() {
        return comment;
    }
}
