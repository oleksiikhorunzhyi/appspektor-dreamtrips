package com.messenger.messengerservers.xmpp.providers;

import android.text.TextUtils;

import com.messenger.messengerservers.xmpp.stanzas.FlagMessageIQ;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class FlaggingProvider extends IQProvider<FlagMessageIQ> {

   @Override
   public FlagMessageIQ parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
      FlagMessageIQ flagMessageIQ = new FlagMessageIQ();
      boolean done = false;
      while (!done) {
         int eventType = parser.next();
         switch (eventType) {
            case XmlPullParser.START_TAG:
               String elementName = parser.getName();
               if (TextUtils.equals(FlagMessageIQ.MESSAGE_ELEMENT_NAME, elementName)) {
                  String messageId = parser.getAttributeValue("", FlagMessageIQ.MESSAGE_ID_ATTRIBUTE);
                  String result = parser.getAttributeValue("", FlagMessageIQ.RESULT_ATTRIBUTE);
                  flagMessageIQ.setMessageId(messageId);
                  flagMessageIQ.setResult(result);
                  done = true;
               }
               break;

         }
      }
      return flagMessageIQ;
   }
}
