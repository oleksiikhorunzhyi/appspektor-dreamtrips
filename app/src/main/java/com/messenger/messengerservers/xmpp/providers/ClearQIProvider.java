package com.messenger.messengerservers.xmpp.providers;

import com.messenger.messengerservers.xmpp.stanzas.BaseClearChatIQ;
import com.messenger.messengerservers.xmpp.stanzas.ClearChatIQ;
import com.messenger.messengerservers.xmpp.stanzas.RevertClearChatIQ;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ClearQIProvider extends IQProvider<BaseClearChatIQ> {
    public static final String ELEMENT_QUERY = "query";
    public static final String NAME_SPACE = "worldventures.com#user-clear-chat";

    @Override
    public BaseClearChatIQ parse(XmlPullParser parser, int initialDepth)
            throws XmlPullParserException, IOException, SmackException {
        String conversationId = null;
        long time = 0;

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String elementName = parser.getName();
                    if (ClearChatIQ.ELEMENT_CONVERSATION_ID.equals(elementName)) {
                        parser.next();
                        conversationId = parser.getText();
                    } else if (ClearChatIQ.ELEMENT_CLEAR_DATE.equals(elementName)) {
                        parser.next();
                        time = Long.parseLong(parser.getText());
                    } else if (RevertClearChatIQ.ELEMENT_REMOVE_CLEARING.equals(elementName)) {
                        time = -1;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (ELEMENT_QUERY.equals(parser.getName())) {
                        done = true;
                    }

            }
        }

        if (time > -1) {
            return new ClearChatIQ(conversationId, null, time);
        } else {
            return new RevertClearChatIQ(conversationId, null);
        }
    }


}
