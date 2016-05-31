package com.messenger.messengerservers.xmpp.loaders;

import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.constant.UserType;
import com.messenger.messengerservers.loaders.ContactsLoader;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.packet.RosterPacket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class XmppContactLoader implements ContactsLoader {
    private final Observable<Roster> rosterObservable;

    public XmppContactLoader(XmppServerFacade facade) {
        this.rosterObservable = facade.getConnectionObservable().map(Roster::getInstanceFor);
    }

    @Override
    public Observable<List<MessengerUser>> load() {
        return rosterObservable.flatMap(RosterObservable::create);
    }

    private static class RosterObservable implements Observable.OnSubscribe<List<MessengerUser>> {

        private final Roster roster;

        private RosterObservable(Roster roster) {
            this.roster = roster;
        }

        public static Observable<List<MessengerUser>> create(Roster roster) {
            return Observable.create(new RosterObservable(roster));
        }

        @Override
        public void call(Subscriber<? super List<MessengerUser>> subscriber) {
            // TODO encapsulate roster and use proxy instead
            try {
                loadRosterIfNeed();
            } catch (Exception exception) {
                subscriber.onError(exception);
            }

            if (subscriber.isUnsubscribed()) return;
            subscriber.onNext(obtainUsersFromRoster());
            subscriber.onCompleted();
        }

        private void loadRosterIfNeed() throws ConnectionException, InterruptedException {
            if (roster.isLoaded()) return;
            try {
                roster.reloadAndWait();
            } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException e) {
                throw new ConnectionException(e);
            }
        }

        private List<MessengerUser> obtainUsersFromRoster() {
            Collection<RosterEntry> entries = roster.getEntries();
            ArrayList<MessengerUser> messengerUsers = new ArrayList<>(entries.size());

            for (RosterEntry entry : entries) {
                if (entry.getType() != RosterPacket.ItemType.both) {
                    continue;
                }

                String userName = entry.getUser();
                boolean online = roster.getPresence(userName).getType().equals(Presence.Type.available);

                MessengerUser messengerUser = new MessengerUser(JidCreatorHelper.obtainId(userName));
                messengerUser.setOnline(online);
                messengerUser.setType(UserType.FRIEND);
                messengerUsers.add(messengerUser);
            }

            return messengerUsers;
        }

    }
}
