package com.messenger.messengerservers.xmpp.loaders;

import android.support.annotation.Nullable;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.packet.RosterPacket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.loaders.AsyncLoader;

public class XmppContactLoader extends AsyncLoader<User> {
    private final XMPPConnection connection;

    public XmppContactLoader(XMPPConnection connection, @Nullable ExecutorService executorService) {
        super(executorService);
        this.connection = connection;
    }

    @Override
    protected List<User> loadEntities() {
        Roster roster = Roster.getInstanceFor(connection);

        if (!roster.isLoaded()) {
            try {
                roster.reload();
            } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException e) {
                Log.w("XmppContactLoader", "reload failed", e);
            }
        }
        Collection<RosterEntry> entries = roster.getEntries();
        ArrayList<User> users = new ArrayList<>(entries.size());

        for (RosterEntry entry : entries) {
            if (entry.getType() != RosterPacket.ItemType.both) {
                continue;
            }
            String name = entry.getName();
            users.add(new User(name != null ? name : entry.getUser()));
        }

        return users;
    }
}
