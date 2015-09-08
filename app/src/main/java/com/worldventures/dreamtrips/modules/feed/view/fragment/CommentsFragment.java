package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.dialogplus.DialogPlus;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPostEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.model.comment.LoadMore;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPostCommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoadMoreCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.comment.FeedBucketCommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.comment.FeedPhotoCommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.comment.FeedTripCommentCell;
import com.worldventures.dreamtrips.modules.feed.view.custom.EditCommentViewHolder;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_comments)
public class CommentsFragment extends BaseFragment<BaseCommentPresenter> implements BaseCommentPresenter.View {
    public static final String EXTRA_FEED_ITEM = "item";
    public static final String EXTRA_OPEN_COMMENT_KEYBOARD = "EXTRA_OPEN_COMMENT_KEYBOARD";
    public static final int HEADER_COUNT = 2;

    @InjectView(R.id.commentsList)
    RecyclerView commentsList;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    @InjectView(R.id.input)
    EditText input;
    @InjectView(R.id.post)
    Button post;

    LoadMore loadMore;

    RecyclerViewStateDelegate stateDelegate;

    BaseArrayListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private TextWatcherAdapter inputWatcher = new TextWatcherAdapter() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            String text = s.toString().trim();
            getPresenter().setComment(text);
            post.setEnabled(text.length() > 0);
        }
    };

    @Override
    protected BaseCommentPresenter createPresenter(Bundle savedInstanceState) {
        return new BaseCommentPresenter((BaseEventModel) getArguments().
                getSerializable(EXTRA_FEED_ITEM));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stateDelegate.saveStateIfNeeded(outState);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        stateDelegate.setRecyclerView(commentsList);
        loadMore = new LoadMore();
        loadMore.setVisible(true);

        adapter = new BaseArrayListAdapter<>(getActivity(), injectorProvider);

        adapter.registerCell(Comment.class, CommentCell.class);
        adapter.registerCell(LoadMore.class, LoadMoreCell.class);
        adapter.registerCell(FeedPhotoEventModel.class, FeedPhotoCommentCell.class);
        adapter.registerCell(FeedTripEventModel.class, FeedTripCommentCell.class);
        adapter.registerCell(FeedBucketEventModel.class, FeedBucketCommentCell.class);
        adapter.registerCell(FeedPostEventModel.class, FeedPostCommentCell.class);

        linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        commentsList.setLayoutManager(linearLayoutManager);
        commentsList.setAdapter(adapter);

        input.addTextChangedListener(inputWatcher);

        if (getArguments().getBoolean(EXTRA_OPEN_COMMENT_KEYBOARD, false)) {
            SoftInputUtil.showSoftInputMethod(input);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        input.removeTextChangedListener(inputWatcher);
    }

    @Override
    public void addComments(List<Comment> commentList) {
        boolean scrollToBottom = adapter.getItems().size() <= HEADER_COUNT
                && getArguments().getBoolean(EXTRA_OPEN_COMMENT_KEYBOARD, false);
        adapter.addItems(HEADER_COUNT, commentList);
        stateDelegate.restoreStateIfNeeded();

        if (scrollToBottom) {
            commentsList.smoothScrollToPosition(linearLayoutManager.getItemCount());
        }
    }

    @Override
    public void setHeader(BaseEventModel baseFeedModel) {
        adapter.addItem(0, baseFeedModel);
        adapter.addItem(1, loadMore);
    }

    @Override
    public void addComment(Comment comment) {
        post.setEnabled(true);
        input.setFocusable(true);
        input.setFocusableInTouchMode(true);
        input.setText(null);
        adapter.addItem(comment);
        adapter.notifyItemInserted(adapter.getItemCount());
        commentsList.smoothScrollToPosition(linearLayoutManager.getItemCount());
        SoftInputUtil.hideSoftInputMethod(input);
    }

    @Override
    public void removeComment(Comment comment) {
        int index = adapter.getItems().indexOf(comment);
        adapter.remove(index);
        adapter.notifyItemRemoved(index);
    }

    @Override
    public void updateComment(Comment comment) {
        int index = adapter.getItems().indexOf(comment);
        adapter.replaceItem(index, comment);
        adapter.notifyItemChanged(index);
    }

    @Override
    public void editComment(EditCommentPresenter presenter) {
        EditCommentViewHolder editCommentViewHolder = new EditCommentViewHolder();
        injectorProvider.get().inject(presenter);
        editCommentViewHolder.setPresenter(presenter);

        DialogPlus editDialog = DialogPlus.newDialog(getActivity())
                .setContentHolder(editCommentViewHolder)
                .setCancelable(true)
                .setOnCancelListener(dialog -> SoftInputUtil.hideSoftInputMethod(getActivity()))
                .setGravity(Gravity.TOP)
                .create();

        editCommentViewHolder.setDialog(editDialog);

        editDialog.show();
    }

    @Override
    public void onPostError() {
        post.setEnabled(true);
        input.setFocusable(true);
        input.setFocusableInTouchMode(true);
    }

    @Override
    public void hideViewMore() {
        loadMore.setVisible(false);
        adapter.notifyItemChanged(1);
    }

    @OnClick(R.id.post)
    void onPost() {
        getPresenter().post();
        post.setEnabled(false);
        input.setFocusable(false);
    }

    @Override
    public void setComment(String comment) {
        input.setText(comment);
    }

    @Override
    public void setLoading(boolean loading) {
        loadMore.setLoading(loading);
        adapter.notifyItemChanged(1);
    }
}
