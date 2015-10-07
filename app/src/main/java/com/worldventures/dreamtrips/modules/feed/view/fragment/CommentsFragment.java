package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.model.comment.LoadMore;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoadMoreCell;
import com.worldventures.dreamtrips.modules.feed.view.custom.EditCommentViewHolder;
import com.worldventures.dreamtrips.modules.feed.view.util.LikersPanelHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_comments)
public class CommentsFragment<T extends BaseCommentPresenter> extends BaseFragmentWithArgs<T, CommentsBundle> implements BaseCommentPresenter.View {

    @InjectView(R.id.commentsList)
    protected RecyclerView commentsList;
    @Inject
    @ForActivity
    protected Provider<Injector> injectorProvider;
    @InjectView(R.id.input)
    protected EditText input;
    @InjectView(R.id.post)
    protected Button post;
    @InjectView(R.id.likers_panel)
    TextView likersPanel;

    protected LoadMore loadMore;
    protected RecyclerViewStateDelegate stateDelegate;
    protected BaseArrayListAdapter adapter;
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

    private LikersPanelHelper likersPanelHelper;

    @Override
    protected T createPresenter(Bundle savedInstanceState) {
        return (T) new BaseCommentPresenter(getArgs().getFeedEntity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
        likersPanelHelper = new LikersPanelHelper();
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

        linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        commentsList.setLayoutManager(linearLayoutManager);
        commentsList.setAdapter(adapter);

        if (getArgs().isOpenKeyboard()) {
            SoftInputUtil.showSoftInputMethod(input);
        }
        restorePostIfNeeded();
    }

    private void restorePostIfNeeded() {
        fragmentCompass.setContainerId(R.id.container_details_floating);
        BaseFragment baseFragment = fragmentCompass.getCurrentFragment();
        if (baseFragment instanceof PostFragment) {
            fragmentCompass.showContainer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        input.addTextChangedListener(inputWatcher);
    }

    @Override
    public void onPause() {
        super.onPause();
        input.removeTextChangedListener(inputWatcher);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void setEntity(FeedEntity entity) {
        likersPanelHelper.setup(likersPanel, entity);
    }

    @Override
    public void addComments(List<Comment> commentList) {
        boolean scrollToBottom = adapter.getItems().size() <= getHeaderCount() && getArgs().isOpenKeyboard();
        adapter.addItems(getHeaderCount(), commentList);
        stateDelegate.restoreStateIfNeeded();

        if (scrollToBottom) {
            commentsList.smoothScrollToPosition(linearLayoutManager.getItemCount());
        }
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

    protected int getHeaderCount() {
        return 0;
    }

}
