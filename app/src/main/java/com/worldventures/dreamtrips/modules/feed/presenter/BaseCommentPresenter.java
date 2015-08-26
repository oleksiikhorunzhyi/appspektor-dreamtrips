package com.worldventures.dreamtrips.modules.feed.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.DeleteCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.GetCommentsQuery;
import com.worldventures.dreamtrips.modules.feed.event.CommentChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeleteCommentRequestEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditCommentRequestEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedObjectChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadMoreEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.ArrayList;
import java.util.List;

import icepick.Icicle;

public class BaseCommentPresenter extends Presenter<BaseCommentPresenter.View> {
    BaseEventModel feedModel;
    IFeedObject feedEntity;

    private int page = 1;
    private int commentsCount = 0;

    @Icicle
    String comment;

    public BaseCommentPresenter(BaseEventModel feedModel) {
        this.feedModel = feedModel;
        feedEntity = feedModel.getItem();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);

        setHeader();
        loadComments();

        view.setComment(comment);
    }

    private void loadComments() {
        view.setLoading(true);
        doRequest(new GetCommentsQuery(feedEntity.getUid(), page), this::onCommentsLoaded);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    boolean posting = false;

    public void post() {
        posting = true;
        doRequest(new CreateCommentCommand(feedEntity.getUid(), comment), this::onCommentPosted);
    }

    public void onEvent(LoadMoreEvent event) {
        loadComments();
    }

    public void onEvent(DeleteCommentRequestEvent event) {
        doRequest(new DeleteCommentCommand(event.getComment().getUid()), jsonObject -> {
            view.removeComment(event.getComment());
            feedEntity.getComments().remove(event.getComment());
            feedEntity.setCommentsCount(feedEntity.getCommentsCount() - 1);
            eventBus.post(new FeedObjectChangedEvent(feedModel));

        });
    }

    public void onEvent(EditCommentRequestEvent event) {
        EditCommentPresenter editCommentPresenter = new EditCommentPresenter(feedModel, event.getComment());
        view.editComment(editCommentPresenter);
    }


    public void onEvent(CommentChangedEvent event) {
        view.updateComment(event.getComment());
        feedEntity.getComments().set(feedEntity.getComments().indexOf(event.getComment()), event.getComment());
        eventBus.post(new FeedObjectChangedEvent(feedModel));

    }

    private void onCommentPosted(Comment comment) {
        posting = false;
        view.addComment(comment);
        feedEntity.getComments().add(0, comment);
        feedEntity.setCommentsCount(feedEntity.getCommentsCount() + 1);
        eventBus.post(new FeedObjectChangedEvent(feedModel));
    }

    private void onCommentsLoaded(ArrayList<Comment> comments) {
        if (comments.size() > 0) {
            page++;
            commentsCount += comments.size();
            view.setLoading(false);
            view.addComments(comments);

            if (commentsCount == feedEntity.getCommentsCount()) view.hideViewMore();

        } else {
            view.hideViewMore();
        }
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        posting = false;
        view.setLoading(false);
    }


    private void setHeader() {
        view.setHeader(feedModel);
    }

    public interface View extends Presenter.View {
        void addComments(List<Comment> commentList);

        void addComment(Comment comment);

        void removeComment(Comment comment);

        void updateComment(Comment comment);

        void setComment(String comment);

        void setLoading(boolean loading);

        void setHeader(BaseEventModel header);

        void editComment(EditCommentPresenter presenter);

        void hideViewMore();
    }
}
