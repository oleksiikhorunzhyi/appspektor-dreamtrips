package com.messenger.delegate.user;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.api.GetShortProfileAction;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Observable;

import static com.innahema.collections.query.queriables.Queryable.from;

class UserDataFetcher {
    private static final String HOST_BADGE = "DreamTrips Host";

    private final ActionPipe<GetShortProfileAction> shortProfilesPipe;
    private final UsersDAO usersDAO;

    @Inject
    public UserDataFetcher(Janet janet, UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
        this.shortProfilesPipe = janet.createPipe(GetShortProfileAction.class);
    }

    public Observable<List<DataUser>> fetchUserData(List<MessengerUser> messengerUsers) {
        if (messengerUsers.isEmpty()) return Observable.just(Collections.emptyList());

        List<String> userNames = from(messengerUsers)
                .map(MessengerUser::getName)
                .toList();

        return shortProfilesPipe
                .createObservableResult(new GetShortProfileAction(userNames))
                .map(action -> composeDataUsers(messengerUsers, action.getShortUsers()));
    }

    private List<DataUser> composeDataUsers(List<MessengerUser> messengerUsers,
                                                        List<User> socialUsers) {
        return Queryable.from(messengerUsers)
                .map(messengerUser -> pairUserProfiles(messengerUser, socialUsers))
                .filter(pair -> pair.second != null)
                .map(this::prepareDataUser)
                .toList();
    }

    private Pair<MessengerUser, User> pairUserProfiles(MessengerUser messengerUser, List<User> socialUsers) {
        String messengerName = messengerUser.getName();
        User user = from(socialUsers)
                .firstOrDefault(temp -> TextUtils.equals(temp.getUsername().toLowerCase(), messengerName));
        return new Pair<>(messengerUser, user);
    }

    private DataUser prepareDataUser(Pair<MessengerUser, User> pair) {
        MessengerUser messengerUser = pair.first;
        User socialUser = pair.second;
        DataUser cachedUser = usersDAO.getUserById(messengerUser.getName()).toBlocking().first();

        DataUser user = new DataUser();
        user.setId(messengerUser.getName().toLowerCase());
        user.setSocialId(socialUser.getId());
        user.setFirstName(socialUser.getFirstName());
        user.setLastName(socialUser.getLastName());
        user.setHost(hasHostBadge(socialUser.getBadges()));
        user.setOnline(messengerUser.isOnline());
        user.setFriend(messengerUser.getType() != null ? true : null);

        if (cachedUser != null) {
            if (cachedUser.isFriendSet() && !user.isFriendSet()) {
                user.setFriend(cachedUser.isFriend());
            }
            if (cachedUser.isOnline() && !user.isOnline()) {
                user.setOnline(true);
            }
        }
        user.setAvatarUrl(socialUser.getAvatar() == null ? null : socialUser.getAvatar().getThumb());
        return user;
    }

    private boolean hasHostBadge(@Nullable List<String> badges) {
        if (badges == null || badges.isEmpty()) return false;
        for (String badge : badges) {
            if (TextUtils.equals(badge, HOST_BADGE)) return true;
        }
        return false;
    }
}
