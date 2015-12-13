package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.ContactManager;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.AuthorizeListener;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;

public class RosterManager extends ContactManager {
    private final XmppServerFacade facade;
    private final AuthorizeListener authListener = new AuthorizeListener() {
        @Override
        public void onSuccess() {
            super.onSuccess();
            Roster roster = Roster.getInstanceFor(facade.getConnection());
            roster.addRosterListener(rosterListener);
            facade.removeAuthorizationListener(this);
        }
    };

    private final RosterListener rosterListener = new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> addresses) {
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
        }

        @Override
        public void presenceChanged(Presence presence) {
            // TODO: 12/9/15 change type
            User user = new User();
            user.setOnline(presence.getType().equals(Presence.Type.available));
            user.setName(presence.getFrom().split("@")[0]);
            if (userPersister != null) {
                userPersister.save(user);
            }
        }
    };

    public RosterManager(XmppServerFacade facade) {
        facade.addAuthorizationListener(authListener);
        this.facade = facade;
    }
}
