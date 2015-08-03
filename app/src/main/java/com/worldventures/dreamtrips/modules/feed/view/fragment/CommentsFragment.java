package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.model.comment.LoadMore;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedBucketEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPhotoEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedTripEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoadMoreCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_comments)
public class CommentsFragment extends BaseFragment<BaseCommentPresenter> implements BaseCommentPresenter.View {

    @InjectView(R.id.commentsList)
    RecyclerView commentsList;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    @InjectView(R.id.input)
    EditText input;
    @InjectView(R.id.btnPost)
    Button btnPost;

    LoadMore loadMore;

    BaseArrayListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected BaseCommentPresenter createPresenter(Bundle savedInstanceState) {
        return new BaseCommentPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        loadMore = new LoadMore();

        adapter = new BaseArrayListAdapter<>(getActivity(), injectorProvider);

        adapter.registerCell(Comment.class, CommentCell.class);
        adapter.registerCell(LoadMore.class, LoadMoreCell.class);
        adapter.registerCell(FeedPhotoEventModel.class, FeedPhotoEventCell.class);
        adapter.registerCell(FeedTripEventModel.class, FeedTripEventCell.class);
        adapter.registerCell(FeedBucketEventModel.class, FeedBucketEventCell.class);

        linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        commentsList.setLayoutManager(linearLayoutManager);
        commentsList.setAdapter(adapter);
    }

    @Override
    public void addComments(List<Comment> commentList) {
        adapter.addItems(2, commentList);
    }

    @Override
    public void setHeader(BaseFeedModel baseFeedModel) {
        adapter.addItem(0, baseFeedModel);
        adapter.addItem(1, loadMore);
    }

    @Override
    public void setLoading(boolean loading) {
        loadMore.setLoading(loading);
        adapter.notifyItemChanged(1);
    }
}
