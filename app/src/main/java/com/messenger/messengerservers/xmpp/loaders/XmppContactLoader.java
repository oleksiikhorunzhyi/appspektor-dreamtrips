package com.messenger.messengerservers.xmpp.loaders;

import android.support.annotation.Nullable;

import com.messenger.messengerservers.constant.UserType;
import com.messenger.messengerservers.loaders.AsyncLoader;
import com.messenger.messengerservers.model.User;
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
import java.util.concurrent.ExecutorService;

import timber.log.Timber;

public class XmppContactLoader extends AsyncLoader<User> {
    private final XmppServerFacade facade;

    public XmppContactLoader(XmppServerFacade facade, @Nullable ExecutorService executorService) {
        super(executorService);
        this.facade = facade;
    }

    @Override
    protected List<User> loadEntities() {
        // TODO encapsulate roster and use proxy instead
        Roster roster = Roster.getInstanceFor(facade.getConnection());
        if (!roster.isLoaded()) {
            try {
                roster.reloadAndWait();
            } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException | InterruptedException e) {
                Timber.w(e, getClass().getSimpleName());
            }
        }
        Collection<RosterEntry> entries = roster.getEntries();
        ArrayList<User> users = new ArrayList<>(entries.size());

        for (RosterEntry entry : entries) {
            if (entry.getType() != RosterPacket.ItemType.both) {
                continue;
            }

            String userName = entry.getUser();
            User user = new User(JidCreatorHelper.obtainId(userName));
            boolean online = roster.getPresence(userName).getType().equals(Presence.Type.available);
            user.setOnline(online);
            user.setType(UserType.FRIEND);
            users.add(user);
        }

        return users;
    }
}
