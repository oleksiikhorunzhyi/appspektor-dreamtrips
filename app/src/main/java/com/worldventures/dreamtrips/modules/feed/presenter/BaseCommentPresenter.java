package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.GetCommentsQuery;
import com.worldventures.dreamtrips.modules.feed.event.CommentsPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemStickyEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadMoreEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.model.comment.LoadMore;

import java.util.ArrayList;
import java.util.List;

public class BaseCommentPresenter extends Presenter<BaseCommentPresenter.View> {

    BaseFeedModel feedModel;

    private int page = 1;

    @Override
    public void takeView(View view) {
        super.takeView(view);

        FeedItemStickyEvent event = eventBus.getStickyEvent(FeedItemStickyEvent.class);
        feedModel = event.getModel();

        setHeader();
        loadComments();
    }

    private void loadComments() {
        view.setLoading(true);
        doRequest(new GetCommentsQuery(feedModel.getId(), page), this::onCommentsLoaded);
    }

    public void post(String constraint) {
        doRequest(new CreateCommentCommand(feedModel.getId(), constraint), view::addComment);
    }

    public void onEvent(LoadMoreEvent event) {
        loadComments();
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

        void setLoading(boolean loading);

        void setHeader(BaseFeedModel header);
    }
}
