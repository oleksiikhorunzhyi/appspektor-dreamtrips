package com.messenger.messengerservers;

import com.messenger.messengerservers.entities.User;

public interface InvitationListener {

    void receiveInvite(User inviter, String roomId, String password);
}
