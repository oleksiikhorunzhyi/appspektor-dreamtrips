package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.DeleteCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.GetCommentsQuery;
import com.worldventures.dreamtrips.modules.feed.event.CommentUpdatedEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeleteCommentEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditCommentEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemStickyEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadMoreEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.ArrayList;
import java.util.List;

import icepick.Icicle;

public class BaseCommentPresenter extends Presenter<BaseCommentPresenter.View> {

    BaseFeedModel feedModel;

    private int page = 1;

    @Icicle
    String comment;

    @Override
    public void takeView(View view) {
        super.takeView(view);

        FeedItemStickyEvent event = eventBus.getStickyEvent(FeedItemStickyEvent.class);
        feedModel = event.getModel();

        setHeader();
        loadComments();

        view.setComment(comment);
    }

    private void loadComments() {
        view.setLoading(true);
        doRequest(new GetCommentsQuery(feedModel.getId(), page), this::onCommentsLoaded);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void post() {
        doRequest(new CreateCommentCommand(feedModel.getId(), comment), view::addComment);
    }

    public void onEvent(LoadMoreEvent event) {
        loadComments();
    }

    public void onEvent(DeleteCommentEvent event) {
        doRequest(new DeleteCommentCommand(event.getComment().getId()), jsonObject -> {
            view.removeComment(event.getComment());
        });
    }

    public void onEvent(CommentUpdatedEvent event) {
        view.updateComment(event.getComment());
    }

    public void onEvent(EditCommentEvent event) {
        EditCommentPresenter editCommentPresenter = new EditCommentPresenter(event.getComment());
        view.editComment(editCommentPresenter);
    }

    private void onCommentsLoaded(ArrayList<Comment> comments) {
        page++;
        view.setLoading(false);
        view.addComments(comments);
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

        void setHeader(BaseFeedModel header);

        void editComment(EditCommentPresenter presenter);
    }
}
