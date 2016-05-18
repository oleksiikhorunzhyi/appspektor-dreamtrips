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
    public Observable<List<MessengerUser>> getContactsObservable() {
        return rosterObservable.flatMap(RosterObservable::create);
    }

    private static class RosterObservable implements Observable.OnSubscribe<List<MessengerUser>> {
        private final Roster roster;

        public static Observable<List<MessengerUser>> create(Roster roster) {
            return Observable.create(new RosterObservable(roster));
        }
        private RosterObservable(Roster roster) {
            this.roster = roster;
        }

        @Override
        public void call(Subscriber<? super List<MessengerUser>> subscriber) {
            // TODO encapsulate roster and use proxy instead
            if (!roster.isLoaded()) {
                try {
                    roster.reloadAndWait();
                } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException e) {
                    subscriber.onError(new ConnectionException(e));
                } catch (InterruptedException e) {
                    subscriber.onError(e);
                }
            }
            Collection<RosterEntry> entries = roster.getEntries();
            ArrayList<MessengerUser> messengerUsers = new ArrayList<>(entries.size());

            for (RosterEntry entry : entries) {
                if (entry.getType() != RosterPacket.ItemType.both) {
                    continue;
                }

                String userName = entry.getUser();
                MessengerUser messengerUser = new MessengerUser(JidCreatorHelper.obtainId(userName));
                boolean online = roster.getPresence(userName).getType().equals(Presence.Type.available);
                messengerUser.setOnline(online);
                messengerUser.setType(UserType.FRIEND);
                messengerUsers.add(messengerUser);
            }
            subscriber.onNext(messengerUsers);
            subscriber.onCompleted();
        }
    }
}
