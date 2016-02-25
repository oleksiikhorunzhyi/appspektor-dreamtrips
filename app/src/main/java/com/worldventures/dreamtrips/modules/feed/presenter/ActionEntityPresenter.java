package com.worldventures.dreamtrips.modules.feed.presenter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.CachedPostEntity;

import icepick.State;

public abstract class ActionEntityPresenter<V extends ActionEntityPresenter.View> extends Presenter<V> {

    @State
    CachedPostEntity cachedPostEntity;

    @Override
    public void takeView(V view) {
        super.takeView(view);
        view.setName(getAccount().getFullName());
        view.setAvatar(getAccount().getAvatar().getThumb());
    }

    public void cancelClicked() {
        if (TextUtils.isEmpty(cachedPostEntity.getText()) &&
                cachedPostEntity.getUploadTask() == null) {
            view.cancel();
        } else {
            view.showCancelationDialog();
        }
    }

    public void postInputChanged(String input) {
        cachedPostEntity.setText(input);
        enablePostButton();
    }

    private void enablePostButton() {
        if ((!TextUtils.isEmpty(cachedPostEntity.getText()) && cachedPostEntity.getUploadTask() == null) ||
                (cachedPostEntity.getUploadTask() != null &&
                        cachedPostEntity.getUploadTask().getStatus().equals(UploadTask.Status.COMPLETED))) {
            view.enableButton();
        } else {
            view.disableButton();
        }
    }

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
    }

}
