package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.app.Dialog;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.event.DeleteCommentEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditCommentEvent;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;

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
    @Optional
    @InjectView(R.id.edit)
    ImageView edit;
    @Optional
    @InjectView(R.id.reply)
    ImageView reply;
    @InjectView(R.id.edited)
    ImageView edited;

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
        CharSequence relativeTimeSpanString = DateTimeUtils.getRelativeTimeSpanString(itemView.getResources(),
                getModelObject().getCreatedAt().getTime());
        date.setText(relativeTimeSpanString);

        if (edit != null)
            if (appSessionHolder.get().get().getUser().getId() == owner.getId()) {
                edit.setVisibility(View.VISIBLE);
            } else {
                edit.setVisibility(View.GONE);
            }

        if (getModelObject().isUpdate()) {
            edited.setVisibility(View.VISIBLE);
        } else {
            edited.setVisibility(View.INVISIBLE);
        }
    }

    @Optional
    @OnClick(R.id.edit)
    void onEditClicked() {
        PopupMenu popup = new PopupMenu(itemView.getContext(), edit);
        popup.inflate(R.menu.menu_comment_edit);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    Dialog dialog = new SweetAlertDialog(itemView.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(itemView.getResources().getString(R.string.comment_delete))
                            .setContentText(itemView.getResources().getString(R.string.comment_delete_caption))
                            .setConfirmText(itemView.getResources().getString(R.string.comment_delete_confirm))
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismissWithAnimation();
                                getEventBus().post(new DeleteCommentEvent(getModelObject()));
                            });
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case R.id.action_edit:
                    getEventBus().post(new EditCommentEvent(getModelObject()));
                    break;
            }

            return true;
        });
        popup.show();
    }

    @Override
    public void prepareForReuse() {

    }
}
