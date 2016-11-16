package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.session.model.Session;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.settings.model.Setting;

import org.jetbrains.annotations.NotNull;

import io.techery.mappery.MapperyContext;

public class SessionConverter implements Converter<Session, com.worldventures.dreamtrips.modules.common.model.Session> {

   @Override
   public Class<Session> sourceClass() {
      return Session.class;
   }

   @Override
   public Class<com.worldventures.dreamtrips.modules.common.model.Session> targetClass() {
      return com.worldventures.dreamtrips.modules.common.model.Session.class;
   }

   @Override
   public com.worldventures.dreamtrips.modules.common.model.Session convert(@NotNull MapperyContext mapperyContext, Session apiSession) {
      com.worldventures.dreamtrips.modules.common.model.Session session = new com.worldventures.dreamtrips.modules.common.model.Session();
      session.setUser(mapperyContext.convert(apiSession.user(), User.class));
      session.setToken(apiSession.token());
      session.setLocale(apiSession.locale());
      session.setPermissions(mapperyContext.convert(apiSession.permissions(), Feature.class));
      session.setSsoToken(apiSession.ssoToken());
      session.setSettings(mapperyContext.convert(apiSession.settings(), Setting.class));
      return session;
   }
}
