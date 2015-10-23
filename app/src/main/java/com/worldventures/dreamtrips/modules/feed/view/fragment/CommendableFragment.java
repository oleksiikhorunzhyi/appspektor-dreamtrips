package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.dialogplus.DialogPlus;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.model.comment.LoadMore;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoadMoreCell;
import com.worldventures.dreamtrips.modules.feed.view.custom.EditCommentViewHolder;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_comments)
public class CommendableFragment<T extends BaseCommentPresenter, P extends CommentsBundle> extends BaseFragmentWithArgs<T, P> implements BaseCommentPresenter.View {

    public static final int LOAD_MORE_ITEMS_COUNT = 1;
    @InjectView(R.id.list)
    protected EmptyRecyclerView recyclerView;
    @InjectView(R.id.input)
    protected EditText input;
    @InjectView(R.id.post)
    protected Button post;
    @InjectView(R.id.input_container)
    View inputContainer;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    protected LoadMore loadMore;
    protected RecyclerViewStateDelegate stateDelegate;
    protected BaseArrayListAdapter adapter;
    protected LinearLayoutManager layout;

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
    protected T createPresenter(Bundle savedInstanceState) {
        return (T) new BaseCommentPresenter(getArgs().getFeedEntity());
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
        stateDelegate.setRecyclerView(recyclerView);

        adapter = new BaseArrayListAdapter<>(getActivity(), this);
        adapter.registerCell(Comment.class, CommentCell.class);
        adapter.registerCell(LoadMore.class, LoadMoreCell.class);

        loadMore = new LoadMore();
        loadMore.setVisible(false);
        adapter.addItem(0, loadMore);

        layout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        recyclerView.getItemAnimator().setSupportsChangeAnimations(false);

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
    public void addComments(List<Comment> commentList) {
        adapter.addItems(getAdditionalItemsCount(), commentList);
    }

    @Override
    public void addComment(Comment comment) {
        post.setEnabled(true);
        input.setFocusable(true);
        input.setFocusableInTouchMode(true);
        input.setText(null);
        adapter.addItem(comment);
        adapter.notifyItemInserted(adapter.getItemCount());
        recyclerView.smoothScrollToPosition(layout.getItemCount());
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
        inject(presenter);
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
    public void showViewMore() {
        loadMore.setVisible(true);
        adapter.notifyItemChanged(getLoadMorePosition());
    }

    @Override
    public void writeComment() {
        SoftInputUtil.showSoftInputMethod(input);
    }

    @Override
    public void hideViewMore() {
        loadMore.setVisible(false);
        adapter.notifyItemChanged(getLoadMorePosition());
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
        adapter.notifyItemChanged(getLoadMorePosition());
    }

    protected int getLoadMorePosition() {
        return 0;
    }

    protected int getAdditionalItemsCount() {
        return LOAD_MORE_ITEMS_COUNT;
    }

}
