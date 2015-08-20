package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.EditCommentCommand;
import com.worldventures.dreamtrips.modules.feed.event.CommentChangedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

public class EditCommentPresenter extends Presenter<EditCommentPresenter.View> {

    BaseEventModel feedObject;
    private Comment comment;

    public EditCommentPresenter(BaseEventModel feedObject, Comment comment) {
        this.feedObject = feedObject;
        this.comment = comment;
    }

    @Override
    public void takeView(EditCommentPresenter.View view) {
        super.takeView(view);
        view.setText(comment.getMessage());
        User owner = comment.getOwner();
        view.setImageURI(Uri.parse(owner.getAvatar().getThumb()));
        view.setUsername(owner.getFullName());
    }

    public void onSave() {
        comment.setMessage(view.getText());
        doRequest(new EditCommentCommand(comment), result -> {
            eventBus.post(new CommentChangedEvent(result));
            view.close();
        });
    }

    public interface View extends Presenter.View {
        void setText(String text);

        void setUsername(String name);

        void setImageURI(Uri uri);

        void close();

        String getText();
    }
}
