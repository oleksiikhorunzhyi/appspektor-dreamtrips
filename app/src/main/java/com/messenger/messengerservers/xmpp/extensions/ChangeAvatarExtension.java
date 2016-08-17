package com.messenger.messengerservers.xmpp.extensions;

import android.text.TextUtils;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.xmlpull.v1.XmlPullParser;

import timber.log.Timber;

public class ChangeAvatarExtension implements ExtensionElement {
   public static final String NAMESPACE = "jabber:icon";
   public static final String ELEMENT = "icon";

   private String avatarUrl;

   public ChangeAvatarExtension(String avatarUrl) {
      this.avatarUrl = avatarUrl;
   }

   @Override
   public String getElementName() {
      return ELEMENT;
   }

   @Override
   public String getNamespace() {
      return NAMESPACE;
   }

   public String getAvatarUrl() {
      return avatarUrl;
   }

   @Override
   public XmlStringBuilder toXML() {
      XmlStringBuilder xml = new XmlStringBuilder(this);
      xml.rightAngleBracket();
      if (!TextUtils.isEmpty(avatarUrl)) {
         xml.append(avatarUrl);
      }
      xml.closeElement(ELEMENT);
      return xml;
   }

   public static final ExtensionElementProvider<ChangeAvatarExtension> PROVIDER = new ExtensionElementProvider<ChangeAvatarExtension>() {
      @Override
      public ChangeAvatarExtension parse(XmlPullParser parser, int initialDepth) {
         String avatar = "";
         try {
            while (!(XmlPullParser.END_TAG == parser.next() && TextUtils.equals(ELEMENT, parser.getName()))) {
               avatar = parser.getText();
            }
         } catch (Exception ex) {
            Timber.e(ex, "ChangeAvatarExtension parsing xml");
         }
         return new ChangeAvatarExtension(avatar);
      }
   };
}
