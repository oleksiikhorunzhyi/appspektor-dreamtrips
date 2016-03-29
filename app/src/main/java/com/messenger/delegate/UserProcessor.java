package com.messenger.delegate;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.messenger.api.GetShortProfilesQuery;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;

public class UserProcessor {
    private static final String HOST_BADGE = "DreamTrips Host";
    private final UsersDAO usersDAO;
    private final DreamSpiceManager requester;

    public UserProcessor(UsersDAO usersDAO, DreamSpiceManager requester) {
        this.usersDAO = usersDAO;
        this.requester = requester;
    }

    public Observable<List<DataUser>> connectToUserProvider(Observable<List<MessengerUser>> provider) {
        ConnectableObservable<List<DataUser>> observable = provider
                .compose(updateWithSocialData())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.trampoline())
                .publish();
        observable.subscribe(aVoid -> Timber.i("Users processed"), t -> Timber.w(t, "Can't process users"));
        observable.connect();
        return observable.asObservable();
    }

    private Observable.Transformer<List<MessengerUser>, List<DataUser>> updateWithSocialData() {
        return listObservable -> listObservable.flatMap(messengerUsers -> {
            if (messengerUsers.isEmpty()) return Observable.just(Collections.emptyList());
            //
            List<String> usernames = from(messengerUsers).map(MessengerUser::getName).toList();
            return Observable.<List<User>>create(subscriber -> {
                // SpiceManager post result on MainThread
                requester.execute(new GetShortProfilesQuery(usernames), userz -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(userz);
                        subscriber.onCompleted();
                    }
                }, spiceException -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(spiceException);
                    }
                });
            }).flatMap(users -> syncCashedUser(messengerUsers, users));
        });
    }


    private Observable<List<DataUser>> syncCashedUser(List<MessengerUser> messengerUsers,
                                                       List<User> socialUsers) {
        return Observable.from(messengerUsers)
                .map(messengerUser -> pairUserProfiles(messengerUser, socialUsers))
                .filter(pair -> pair.second != null)
                .map(this::prepareDataUser)
                .toList()
                .doOnNext(usersDAO::save);
    }

    private DataUser prepareDataUser(Pair<MessengerUser, User> pair) {
        MessengerUser messengerUser = pair.first;
        User socialUser = pair.second;
        DataUser cachedUser = usersDAO.getUserById(messengerUser.getName()).toBlocking().first();

        DataUser user = new DataUser();
        user.setId(messengerUser.getName());
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

    private Pair<MessengerUser, User> pairUserProfiles(MessengerUser messengerUser, List<User> socialUsers) {
        String messengerName = messengerUser.getName();
        User user = from(socialUsers)
                .firstOrDefault(temp -> TextUtils.equals(temp.getUsername(), messengerName));
        return new Pair<>(messengerUser, user);
    }


    private boolean hasHostBadge(@Nullable List<String> badges) {
        if (badges == null || badges.isEmpty()) return false;
        for (String badge : badges) {
            if (TextUtils.equals(badge, HOST_BADGE)) return true;
        }
        return false;
    }
}
