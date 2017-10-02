package com.worldventures.core.service;

import com.worldventures.core.model.Session;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.Feature;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.storage.complex_objects.Optional;

import java.util.List;

import io.techery.janet.http.exception.HttpException;
import io.techery.janet.http.exception.HttpServiceException;
import rx.functions.Func0;
import timber.log.Timber;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class AuthRetryPolicy {

   private final SessionHolder appSessionHolder;

   public AuthRetryPolicy(SessionHolder appSessionHolder) {
      this.appSessionHolder = appSessionHolder;
   }

   public boolean handle(Throwable apiError, Func0<Session> loginCall) {
      if (shouldRetry(apiError)) {
         Session session = loginCall.call();
         if (session != null) {
            handleSession(session);
            return true;
         }
      }
      return false;
   }

   private boolean shouldRetry(Throwable error) {
      Timber.d("Check retry");
      boolean wrapperException = error instanceof HttpServiceException && error.getCause() != null;
      return isLoginError(wrapperException? error.getCause() : error) && isCredentialExist(appSessionHolder);
   }

   private void handleSession(Session session) {
      Timber.d("Handling user session");

      User sessionUser = session.getUser();
      UserSession userSession = new UserSession();
      if (appSessionHolder.get().isPresent()) {
         userSession = appSessionHolder.get().get();
      }
      userSession.setLocale(session.getLocale());
      userSession.setUser(sessionUser);
      userSession.setApiToken(session.getToken());
      userSession.setLegacyApiToken(session.getSsoToken());

      userSession.setLastUpdate(System.currentTimeMillis());

      List<Feature> features = session.getPermissions();
      userSession.setFeatures(features);

      appSessionHolder.put(userSession);
   }


   public static boolean isLoginError(Throwable error) {
      if (error instanceof SessionAbsentException) {
         return true;
      } else if (error instanceof HttpException) { // for janet-http
         HttpException cause = (HttpException) error;
         return cause.getResponse() != null && cause.getResponse().getStatus() == HTTP_UNAUTHORIZED;
      } else if (error.getCause() != null) {
         return isLoginError(error.getCause());
      }
      return false;
   }

   public static boolean isCredentialExist(SessionHolder appSessionHolder) {
      Optional<UserSession> userSessionOptional = appSessionHolder.get();
      if (userSessionOptional.isPresent()) {
         UserSession userSession = appSessionHolder.get().get();
         return userSession.getUsername() != null && userSession.getUserPassword() != null;
      } else {
         return false;

      }
   }
}
