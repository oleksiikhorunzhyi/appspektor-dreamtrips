package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.view.adapter.HeaderLayoutManagerFixed;
import com.worldventures.dreamtrips.modules.feed.view.adapter.ParallaxRecyclerAdapter;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

public abstract class CommentsFragment<PM extends BaseCommentPresenter, HEADER extends FeedHeaderCell> extends BaseFragment<PM> implements BaseCommentPresenter.View<HEADER> {

    @InjectView(R.id.commentsList)
    RecyclerView commentsList;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    @InjectView(R.id.input)
    EditText input;
    @InjectView(R.id.btnPost)
    Button btnPost;

    ParallaxRecyclerAdapter adapter;
    private HeaderLayoutManagerFixed layoutManagerFixed;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapter = new ParallaxRecyclerAdapter<>(getActivity(), injectorProvider);
        adapter.registerCell(Comment.class, CommentCell.class);
        adapter.setShouldClipView(false);
        layoutManagerFixed = new HeaderLayoutManagerFixed(rootView.getContext());
        commentsList.setLayoutManager(layoutManagerFixed);
        commentsList.setAdapter(adapter);
    }

    public void setData(List<Comment> commentList) {
        adapter.addItems(commentList);
    }

    @Override
    public void setHeader(HEADER header) {
        //TODO setup header
        layoutManagerFixed.setHeaderIncrementFixer(header.itemView);
        adapter.setParallaxHeader(header.itemView, commentsList);
    }

}
