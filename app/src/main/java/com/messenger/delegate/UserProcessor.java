package com.messenger.delegate;

import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.api.GetShortProfilesQuery;
import com.messenger.entities.DataUser;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.utils.TextUtils;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;

public class UserProcessor {
    private Subscription subscription;
    private final UsersDAO usersDAO;
    private final DreamSpiceManager requester;

    public UserProcessor(UsersDAO usersDAO, DreamSpiceManager requester) {
        this.usersDAO = usersDAO;
        this.requester = requester;
    }

    public Observable<List<DataUser>> connectToUserProvider(Observable<List<com.messenger.messengerservers.model.User>> provider) {
        ConnectableObservable<List<DataUser>> observable = provider
                .compose(updateWithSocialData())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.trampoline())
                .publish();
        observable.subscribe(aVoid -> Timber.i("Users processed"), t -> Timber.w(t, "Can't process users"));
        observable.connect();
        return observable.asObservable();
    }

    private Observable.Transformer<List<com.messenger.messengerservers.model.User>, List<DataUser>> updateWithSocialData() {
        return listObservable -> listObservable.flatMap(messengerUsers -> {
            if (messengerUsers.isEmpty()) return Observable.just(Collections.emptyList());
            //
            List<String> usernames = from(messengerUsers).map(com.messenger.messengerservers.model.User::getName).toList();
            return Observable.create(subscriber -> {
                // SpiceManager post result on MainThread
                requester.execute(new GetShortProfilesQuery(usernames), userz -> {
                    syncCashedUser(messengerUsers, userz, users -> {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(users);
                            subscriber.onCompleted();
                        }
                    });
                }, spiceException -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(spiceException);
                    }
                });
            });
        });
    }

    private void syncCashedUser(List<com.messenger.messengerservers.model.User> messengerUsers,
                                List<com.worldventures.dreamtrips.modules.common.model.User> socialUser,
                                Action1<List<DataUser>> resultAction) {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();

        subscription = Observable.just(messengerUsers)
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .map(user -> new Pair<>(user, UsersDAO.getUser(user.getName())))
                .toList()
                .map(pairs -> {
                    Collections.sort(pairs, (lhs, rhs) -> lhs.first.getName().compareTo(rhs.first.getName()));
                    Collections.sort(socialUser, (lhs, rhs) -> lhs.getUsername().compareTo(rhs.getUsername()));
                    List<String> socialUsernames = Queryable.from(socialUser).map(z -> z.getUsername()).toList();

                    List<DataUser> result = from(pairs)
                            .filter(pair -> socialUsernames.contains(pair.first.getName()))
                            .zip(socialUser, (pair, z) -> {
                                DataUser storedUser = pair.second;
                                com.messenger.messengerservers.model.User loadedUser = pair.first;
                                DataUser u = new DataUser();
                                u.setId(loadedUser.getName());
                                u.setSocialId(z.getId());
                                u.setName(TextUtils.join(" ", z.getFirstName(), z.getLastName()));
                                u.setOnline(loadedUser.isOnline());
                                u.setFriend(loadedUser.getType() != null ? true : null);

                                if (storedUser != null) {
                                    if (storedUser.isFriendSet() && !u.isFriendSet()) {
                                        u.setFriend(storedUser.isFriend());
                                    }
                                    if (storedUser.isOnline() && !u.isOnline()) {
                                        u.setOnline(true);
                                    }
                                }
                                u.setAvatarUrl(z.getAvatar() == null ? null : z.getAvatar().getThumb());
                                return u;
                            })
                            .toList();
                    return result;
                })
                .doOnNext(usersDAO::save)
                .subscribe(resultAction, throwable -> {
                    Timber.e(throwable, "User processor ERROR");
                });
    }
}