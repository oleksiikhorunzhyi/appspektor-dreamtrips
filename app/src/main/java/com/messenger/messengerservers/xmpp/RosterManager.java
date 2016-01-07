package com.messenger.messengerservers.xmpp;

import android.content.Context;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.delegate.UserProcessor;
import com.messenger.messengerservers.ContactManager;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Table;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.builder.ConditionQueryBuilder;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;
import java.util.List;

import rx.subjects.PublishSubject;
import timber.log.Timber;

public class RosterManager implements ContactManager {

    private Roster roster;
    private final PublishSubject<List<User>> usersProvider;
    private final Context context;

    public RosterManager(Context context, UserProcessor userProcessor) {
        this.context = context;
        usersProvider = PublishSubject.create();
        userProcessor.connectToUserProvider(usersProvider.asObservable());
    }

    public void init(AbstractXMPPConnection connection) {
        roster = Roster.getInstanceFor(connection);
        roster.addRosterListener(rosterListener);
    }

    public void release() {
        if (roster == null) return;
        //
        roster.removeRosterListener(rosterListener);
        roster = null;
    }

    private final RosterListener rosterListener = new RosterListener() {

        @Override
        public void entriesAdded(Collection<String> addresses) {
            Timber.i("Roster add: %s", addresses);
            List<String> ids = convertJidsToIds(addresses);
            List<User> newUsers = Queryable.from(ids)
                    .map((userId) -> {
                        User user = new User(userId);
                        user.setFriend(true);
                        return user;
                    }).toList();
            usersProvider.onNext(newUsers);
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            Timber.i("Roster updated: %s", addresses);
            // Ignored for now
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            Timber.i("Roster deleted: %s", addresses);
            List<String> ids = convertJidsToIds(addresses);
            updateAsFriends(ids, false);
        }

        @Override
        public void presenceChanged(Presence presence) {
            Timber.i("Roster presence from %s is %s", presence.getFrom(), presence.getType());
            String id = presence.getFrom().split("@")[0];
            User user = new User(id);
            user.setOnline(presence.getType().equals(Presence.Type.available));
            //
            User cachedUser = new Select().from(User.class).byIds(user.getId()).querySingle();
            if (cachedUser == null) user.save();
            else {
                cachedUser.setOnline(user.isOnline());
                cachedUser.save();
            }
        }
    };

    private void updateAsFriends(List<String> ids, boolean isFriend) {
        if (ids.isEmpty()) return;
        //
        String first = ids.get(0);
        String[] other = Queryable.from(ids).skip(1).toArray(String.class);
        new Update<>(User.class)
                .set(Condition.column(User$Table.FRIEND).eq(isFriend))
                .where(new ConditionQueryBuilder<>(User.class, Condition.column(User$Table._ID).in(first, other)))
                .queryClose();
        context.getContentResolver().notifyChange(User.CONTENT_URI, null);
    }

    private List<String> convertJidsToIds(Collection<String> jids) {
        return Queryable.from(jids).map(JidCreatorHelper::obtainId).toList();
    }
}
