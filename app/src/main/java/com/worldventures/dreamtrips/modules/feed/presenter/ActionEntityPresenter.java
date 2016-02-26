package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.CachedPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import icepick.State;

public abstract class ActionEntityPresenter<V extends ActionEntityPresenter.View> extends Presenter<V> {

    @State
    CachedPostEntity cachedPostEntity;

    @Override
    public void takeView(V view) {
        super.takeView(view);
        if (cachedPostEntity == null) {
            cachedPostEntity = new CachedPostEntity();
        }

        updateUi();
    }

    protected void updateUi() {
        view.setName(getAccount().getFullName());
        view.setAvatar(getAccount().getAvatar().getThumb());
        view.setText(cachedPostEntity.getText());
    }

    public void cancelClicked() {
        if (isChanged()) {
            view.showCancelationDialog();
        } else {
            view.cancel();
        }
    }

    protected abstract boolean isChanged();

    public void postInputChanged(String input) {
        cachedPostEntity.setText(input);
        enablePostButton();
    }

    protected abstract void enablePostButton();

    public abstract void post();

    public interface View extends RxView {

        void setName(String userName);

        void setAvatar(String avatarUrl);

        void setText(String text);

        void cancel();

        void showCancelationDialog();

        void enableButton();

        void disableButton();

        void onPostError();

        void attachPhoto(Uri uri);
    }

}
