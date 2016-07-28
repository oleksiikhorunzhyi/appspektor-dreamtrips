package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.SingleCommentBundle;
import com.worldventures.dreamtrips.modules.feed.event.CommentIconClickedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.model.comment.LoadMore;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoadMoreCell;
import com.worldventures.dreamtrips.modules.feed.view.util.LikersPanelHelper;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import timber.log.Timber;

@Layout(R.layout.fragment_comments)
public class CommentableFragment<T extends BaseCommentPresenter, P extends CommentsBundle> extends RxBaseFragmentWithArgs<T, P>
        implements BaseCommentPresenter.View {

    @InjectView(R.id.list) protected EmptyRecyclerView recyclerView;
    @InjectView(R.id.input) protected EditText input;
    @InjectView(R.id.post) protected Button post;
    @InjectView(R.id.input_container) View inputContainer;
    @Optional @InjectView(R.id.likers_panel) protected TextView likersPanel;
    @Optional @InjectView(R.id.title) protected TextView header;

    @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator;

    protected LoadMore loadMore;
    protected RecyclerViewStateDelegate stateDelegate;
    protected BaseDelegateAdapter adapter;
    protected LinearLayoutManager layout;
    //
    private LikersPanelHelper likersPanelHelper;
    private NavigationWrapper likersNavigationWrapper;

    private TextWatcherAdapter inputWatcher = new TextWatcherAdapter() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            String text = s.toString().trim();
            getPresenter().setDraftComment(text);
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
        //
        likersPanelHelper = new LikersPanelHelper();
        likersNavigationWrapper = new NavigationWrapperFactory().componentOrDialogNavigationWrapper(
                router, getFragmentManager(), tabletAnalytic
        );
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

        adapter = new BaseDelegateAdapter(getActivity(), this);
        adapter.registerCell(Comment.class, CommentCell.class);
        adapter.registerCell(LoadMore.class, LoadMoreCell.class);
        adapter.registerDelegate(Comment.class, new CommentCell.CommentCellDelegate() {
            @Override
            public void onEditComment(Comment comment) {
                getPresenter().editComment(comment);
            }

            @Override
            public void onDeleteComment(Comment comment) {
                getPresenter().deleteComment(comment);
            }

            @Override
            public void onTranslateComment(Comment comment) {
                getPresenter().translateComment(comment);
            }

            @Override
            public void onFlagClicked(Flaggable flaggableView) {
                getPresenter().loadFlags(flaggableView);
            }

            @Override
            public void onFlagChosen(Comment comment, int flagReasonId, String reason) {
                getPresenter().flagItem(comment.getUid(), flagReasonId, reason);
            }

            @Override
            public void onCellClicked(Comment model) {

            }
        });

        loadMore = new LoadMore();
        loadMore.setVisible(false);
        adapter.addItem(0, loadMore);

        layout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (getArgs().isOpenKeyboard() && getArgs().getFeedEntity().getCommentsCount() == 0) {
            showKeyboard();
        }
        showHeaderIfNeeded();
    }

    private void showHeaderIfNeeded() {
        if (header != null && isTabletLandscape()) {
            header.setVisibility(View.VISIBLE);
            header.getBackground().mutate().setAlpha(255);
        }
    }

    private void showKeyboard() {
        recyclerView.postDelayed(() -> {
            if (recyclerView == null || inputContainer == null || input == null) return;
            //
            recyclerView.scrollBy(0, Integer.MAX_VALUE);
            inputContainer.setVisibility(View.VISIBLE);
            input.requestFocus();
            SoftInputUtil.showSoftInputMethod(input);
        }, 500);
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
        SoftInputUtil.hideSoftInputMethod(getActivity());
    }

    @Override
    public void setLikePanel(FeedEntity entity) {
        if (likersPanel == null || !getArgs().showLikersPanel()) return;
        likersPanelHelper.setup(likersPanel, entity);
        likersPanel.setOnClickListener(v -> likersNavigationWrapper.navigate(Route.USERS_LIKED_CONTENT, new UsersLikedEntityBundle(entity.getUid(), entity.getLikesCount())));
    }

    @Override
    public void back() {
        router.back();
    }

    @Override
    public void addComments(List<Comment> commentList) {
        boolean commentsEmpty = layout.getItemCount() <= getAdditionalItemsCount();
        adapter.addItems(getAdditionalItemsCount(), commentList);
        if (commentsEmpty && getArgs().isOpenKeyboard()) {
            showKeyboard();
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
        recyclerView.smoothScrollToPosition(layout.getItemCount());
        SoftInputUtil.hideSoftInputMethod(input);
    }

    @Override
    public void removeComment(Comment comment) {
        int index = adapter.getItems().indexOf(comment);
        if (index != -1) {
            adapter.remove(index);
            adapter.notifyItemRemoved(index);
        }
    }

    @Override
    public void updateComment(Comment comment) {
        int index = adapter.getItems().indexOf(comment);
        if (index != -1) {
            adapter.replaceItem(index, comment);
            adapter.notifyItemChanged(index);
        }
    }

    @Override
    public void editComment(FeedEntity feedEntity, Comment comment) {
        router.moveTo(Route.EDIT_COMMENT, NavigationConfigBuilder.forDialog()
                .fragmentManager(getChildFragmentManager())
                .gravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP)
                .data(new SingleCommentBundle(feedEntity, comment)).build());
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
    public void showEdit(BucketBundle bucketBundle) {
        @IdRes int containerId = R.id.container_details_floating;
        bucketBundle.setLock(true);
        try {
            bucketBundle.setOwnerId(getArgs().getFeedEntity().getOwner().getId());
        } catch (Exception e) {
            Timber.e(e, "");
        }
        if (isTabletLandscape()) {
            router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forFragment()
                    .backStackEnabled(true)
                    .containerId(containerId)
                    .fragmentManager(getActivity().getSupportFragmentManager())
                    .data(bucketBundle)
                    .build());
            showContainer(containerId);
        } else {
            router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forActivity()
                    .data(bucketBundle)
                    .build());
        }
    }

    private void showContainer(@IdRes int containerId) {
        View container = ButterKnife.findById(getActivity(), containerId);
        if (container != null) container.setVisibility(View.VISIBLE);
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
    public void setDraftComment(String comment) {
        input.setText(comment);
    }

    @Override
    public void setLoading(boolean loading) {
        loadMore.setLoading(loading);
        adapter.notifyItemChanged(getLoadMorePosition());
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void onEvent(CommentIconClickedEvent event) {
        if (isVisibleOnScreen()) SoftInputUtil.showSoftInputMethod(input);
    }

    protected int getLoadMorePosition() {
        return 0;
    }

    protected int getAdditionalItemsCount() {
        return 1;
    }

    @Override
    public void flagSentSuccess() {
        informUser(R.string.flag_sent_success_msg);
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        return false;
    }

    @Override
    public void onApiCallFailed() {
    }
}