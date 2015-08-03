package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_comment)
public class CommentCell extends AbstractCell<Comment> {

    @InjectView(R.id.user_photo)
    SimpleDraweeView userPhoto;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.date)
    TextView date;
    @InjectView(R.id.text)
    TextView text;
    @InjectView(R.id.edit)
    ImageView edit;
    @InjectView(R.id.reply)
    ImageView reply;

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    public CommentCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        User owner = getModelObject().getOwner();
        userPhoto.setImageURI(Uri.parse(owner.getAvatar().getThumb()));
        userName.setText(owner.getFullName());
        text.setText(getModelObject().getMessage());
        CharSequence relativeTimeSpanString = DateUtils.getRelativeTimeSpanString(getModelObject().getCreatedAt().getTime());
        date.setText(relativeTimeSpanString);
        if (appSessionHolder.get().get().getUser().getId() == owner.getId()) {
            edit.setVisibility(View.VISIBLE);
        } else {
            edit.setVisibility(View.GONE);
        }
    }

    @Override
    public void prepareForReuse() {

    }
}
