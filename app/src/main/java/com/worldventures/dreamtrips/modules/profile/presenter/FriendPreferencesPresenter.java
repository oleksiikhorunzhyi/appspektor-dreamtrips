package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Handler;
import android.os.SystemClock;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.model.Friend;
import com.worldventures.dreamtrips.modules.profile.FriendGroupRelation;
import com.worldventures.dreamtrips.modules.profile.api.AddFriendToGroupCommand;
import com.worldventures.dreamtrips.modules.profile.api.DeleteFriendFromGroupCommand;
import com.worldventures.dreamtrips.modules.profile.event.FriendGroupRelationChangedEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FriendPreferencesPresenter extends Presenter<FriendPreferencesPresenter.View> {

    Handler handler = new Handler();

    @Inject
    SnappyRepository db;

    Friend friend;

    public FriendPreferencesPresenter(Friend friend) {
        this.friend = friend;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        List<Circle> circles = db.getCircles();
        List<FriendGroupRelation> friendGroupRelations = Queryable.from(circles).map(element -> {
            return new FriendGroupRelation(element, friend);
        }).toList();
        view.addItems(friendGroupRelations);
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
                    friend.getCircleIds().add(groupId);
                    break;
                case REMOVED:
                    doRequest(new DeleteFriendFromGroupCommand(groupId, userIds), callback);
                    friend.getCircleIds().remove(groupId);
                    break;
            }
        }, event.getCircle().getId(), SystemClock.uptimeMillis() + 300);
    }

    public interface View extends Presenter.View {
        void addItems(List<FriendGroupRelation> circles);
    }
}
