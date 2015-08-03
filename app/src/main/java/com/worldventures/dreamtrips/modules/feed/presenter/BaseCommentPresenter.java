package com.worldventures.dreamtrips.modules.feed.presenter;

import android.view.LayoutInflater;

import com.techery.spares.adapter.AdapterHelper;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPhotoEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseCommentPresenter extends Presenter<BaseCommentPresenter.View> {


    @Override
    public void takeView(View view) {
        super.takeView(view);

        ArrayList<Comment> commentList = new ArrayList<>();
        Comment object = new Comment();
        object.setOwner(appSessionHolder.get().get().getUser());
        object.setCreatedAt(new Date());
        object.setMessage("Amazing photo");
        commentList.add(object);
        commentList.add(object);
        commentList.add(object);
        commentList.add(object);
        commentList.add(object);
        commentList.add(object);
        view.setData(commentList);

        AdapterHelper adapterHelper = new AdapterHelper(LayoutInflater.from(context));
        AbstractCell abstractCell = adapterHelper.buildCell(FeedPhotoEventCell.class, null);
        view.setHeader((FeedPhotoEventCell) abstractCell);
    }

    public interface View<HEADER extends FeedHeaderCell> extends Presenter.View {
        void setData(List<Comment> commentList);

        void setHeader(HEADER header);
    }
}
