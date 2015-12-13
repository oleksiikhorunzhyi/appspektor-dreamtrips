package com.messenger.messengerservers.xmpp.util;


import com.messenger.messengerservers.ChatState;

public final class XmppUtils {

    private XmppUtils() {
    }

    public static org.jivesoftware.smackx.chatstates.ChatState convertState(ChatState state) {
        switch (state) {
            case Active:
                return org.jivesoftware.smackx.chatstates.ChatState.active;
            case Inactive:
                return org.jivesoftware.smackx.chatstates.ChatState.inactive;
            case Gone:
                return org.jivesoftware.smackx.chatstates.ChatState.gone;
            case Composing:
                return org.jivesoftware.smackx.chatstates.ChatState.composing;
            case Paused:
                return org.jivesoftware.smackx.chatstates.ChatState.paused;
            default:
                throw new Error();
        }
    }

    public static ChatState convertState(org.jivesoftware.smackx.chatstates.ChatState state) {
        switch (state) {
            case active:
                return ChatState.Active;
            case inactive:
                return ChatState.Inactive;
            case gone:
                return ChatState.Gone;
            case composing:
                return ChatState.Composing;
            case paused:
                return ChatState.Paused;
            default:
                throw new Error();
        }
    }

}
