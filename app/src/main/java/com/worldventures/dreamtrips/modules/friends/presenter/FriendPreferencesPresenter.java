package com.worldventures.dreamtrips.modules.friends.presenter;

import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.StringRes;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.modules.common.api.janet.command.CirclesCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.model.FriendGroupRelation;
import com.worldventures.dreamtrips.modules.profile.api.AddFriendToGroupCommand;
import com.worldventures.dreamtrips.modules.profile.api.DeleteFriendFromGroupCommand;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.event.FriendGroupRelationChangedEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class FriendPreferencesPresenter extends Presenter<FriendPreferencesPresenter.View> {

    @Inject CirclesInteractor circlesInteractor;

    Handler handler = new Handler();
    User friend;

    public FriendPreferencesPresenter(UserBundle userBundle) {
        friend = userBundle.getUser();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        subscribeCircles();
        updateCircles();
    }

    private void updateCircles() {
        circlesInteractor.pipe().send(new CirclesCommand());
    }

    private void subscribeCircles() {
        circlesInteractor.pipe()
                .observe()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .subscribe(new ActionStateSubscriber<CirclesCommand>()
                        .onStart(circlesCommand -> onCirclesStart())
                        .onSuccess(circlesCommand -> onCirclesSuccess(circlesCommand.getResult()))
                        .onFail((circlesCommand, throwable) -> onCirclesError(circlesCommand.getErrorMessage())));
    }

    private void onCirclesStart() {
        view.showBlockingProgress();
    }

    private void onCirclesSuccess(List<Circle> resultCircles) {
        List<FriendGroupRelation> friendGroupRelations = Queryable.from(resultCircles).map(element -> {
            return new FriendGroupRelation(element, friend);
        }).toList();
        view.addItems(friendGroupRelations);
        view.hideBlockingProgress();
    }

    private void onCirclesError(@StringRes String messageId) {
        view.hideBlockingProgress();
        view.informUser(messageId);
    }

    public void onEvent(FriendGroupRelationChangedEvent event) {
        handler.removeCallbacksAndMessages(event.getCircle().getId());
        handler.postAtTime(() -> {
            List<String> userIds = new ArrayList<>();
            userIds.add(String.valueOf(friend.getId()));
            String groupId = event.getCircle().getId();
            DreamSpiceManager.SuccessListener<Void> callback = aVoid -> {

            };
            switch (event.getState()) {
                case ADDED:
                    doRequest(new AddFriendToGroupCommand(groupId, userIds), callback);
                    friend.getCircles().add(event.getCircle());
                    break;
                case REMOVED:
                    doRequest(new DeleteFriendFromGroupCommand(groupId, userIds), callback);
                    friend.getCircles().remove(event.getCircle());
                    break;
            }
        }, event.getCircle().getId(), SystemClock.uptimeMillis() + 300);
    }

    public interface View extends Presenter.View, BlockingProgressView {
        void addItems(List<FriendGroupRelation> circles);
    }
}
