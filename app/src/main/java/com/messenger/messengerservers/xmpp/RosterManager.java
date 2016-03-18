package com.messenger.messengerservers.xmpp;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.delegate.UserProcessor;
import com.messenger.messengerservers.ContactManager;
import com.messenger.messengerservers.constant.UserType;
import com.messenger.messengerservers.model.User;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

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
    private XmppGlobalEventEmitter emitter;

    public RosterManager(UserProcessor userProcessor, XmppGlobalEventEmitter emitter) {
        this.emitter = emitter;
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
            // TODO: 1/28/16  remove usersProvider from this class
            List<String> ids = convertJidsToIds(addresses);
            List<User> newUsers = Queryable.from(ids)
                    .map((userId) -> {
                        User user = new User(userId);
                        user.setType(UserType.FRIEND);
                        return user;
                    }).toList();
            usersProvider.onNext(newUsers);
//            emitter.notifyOnFriendsChangedListener(ids, true);
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
            emitter.notifyOnFriendsChangedListener(ids, false);
        }

        @Override
        public void presenceChanged(Presence presence) {
            Timber.i("Roster presence from %s is %s", presence.getFrom(), presence.getType());
            String id = JidCreatorHelper.obtainId(presence.getFrom());
            boolean online = presence.getType().equals(Presence.Type.available);
            emitter.notifyOnUserStatusChangedListener(id, online);
        }
    };

    private List<String> convertJidsToIds(Collection<String> jids) {
        return Queryable.from(jids).map(JidCreatorHelper::obtainId).toList();
    }
}
