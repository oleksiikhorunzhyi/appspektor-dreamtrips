package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.core.modules.settings.model.Setting;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.Feature;
import com.worldventures.dreamtrips.api.session.model.Session;

import org.jetbrains.annotations.NotNull;

import io.techery.mappery.MapperyContext;

public class SessionConverter implements Converter<Session, com.worldventures.core.model.Session> {

   @Override
   public Class<Session> sourceClass() {
      return Session.class;
   }

   @Override
   public Class<com.worldventures.core.model.Session> targetClass() {
      return com.worldventures.core.model.Session.class;
   }

   @Override
   public com.worldventures.core.model.Session convert(@NotNull MapperyContext mapperyContext, Session apiSession) {
      com.worldventures.core.model.Session session = new com.worldventures.core.model.Session();
      session.setUser(mapperyContext.convert(apiSession.user(), User.class));
      session.setToken(apiSession.token());
      session.setLocale(apiSession.locale());
      session.setPermissions(mapperyContext.convert(apiSession.permissions(), Feature.class));
      session.setSsoToken(apiSession.ssoToken());
      session.setSettings(mapperyContext.convert(apiSession.settings(), Setting.class));
      return session;
   }
}
