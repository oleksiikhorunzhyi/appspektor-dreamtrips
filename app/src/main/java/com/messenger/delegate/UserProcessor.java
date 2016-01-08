package com.messenger.delegate;

import com.messenger.api.GetShortProfilesQuery;
import com.messenger.messengerservers.entities.User;
import com.messenger.storage.dao.UsersDAO;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.utils.TextUtils;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;

public class UserProcessor {

    private DreamSpiceManager requester;

    public UserProcessor(DreamSpiceManager requester) {
        this.requester = requester;
    }

    public Observable<List<User>> connectToUserProvider(Observable<List<User>> provider) {
        ConnectableObservable<List<User>> observable = provider
                .compose(updateWithSocialData())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.trampoline())
                .publish();
        observable.subscribe(aVoid -> Timber.i("Users processed"), t -> Timber.w(t, "Can't process users"));
        observable.connect();
        return observable.asObservable();
    }

    private Observable.Transformer<List<User>, List<User>> updateWithSocialData() {
        return listObservable -> listObservable.flatMap(users -> {
            if (users.isEmpty()) return Observable.just(Collections.emptyList());
            //
            List<String> usernames = from(users).map(User::getId).toList();
            return Observable.create(subscriber -> {
                requester.execute(new GetShortProfilesQuery(usernames), userz -> {
                    Collections.sort(users, (lhs, rhs) -> lhs.getId().compareTo(rhs.getId()));
                    Collections.sort(userz, (lhs, rhs) -> lhs.getUsername().compareTo(rhs.getUsername()));
                    List<User> result = from(users).zip(userz, (u, z) -> {
                        u.setSocialId(z.getId());
                        u.setName(TextUtils.join(" ", z.getFirstName(), z.getLastName()));
                        User storedUser = UsersDAO.getUser(u.getId());
                        // TODO: 1/5/16 when social API will return relationship, use: u.setFriend(z.getRelationship() == Relationship.FRIEND);
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
                    }).toList();
                    TransactionManager.getInstance().addTransaction(new SaveModelTransaction<>(ProcessModelInfo.withModels(result)));
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                }, spiceException -> {
                    Timber.w(spiceException, "Can't get users by usernames");
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(spiceException);
                    }
                });
            });
        });
    }
}
