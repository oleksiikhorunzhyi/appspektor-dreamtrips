package com.worldventures.core.service;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.core.model.Session;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.modules.auth.service.ReLoginInteractor;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.api.session.LoginHttpAction;
import com.worldventures.dreamtrips.api.session.model.Device;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.ActionState;
import io.techery.janet.HttpActionService;
import io.techery.janet.JanetException;
import io.techery.janet.converter.Converter;
import io.techery.janet.http.HttpClient;
import io.techery.mappery.MapperyContext;
import rx.Observable;
import timber.log.Timber;

public class NewDreamTripsHttpService extends ActionServiceWrapper {

   private final SessionHolder appSessionHolder;
   private final AppVersionNameBuilder appVersionNameBuilder;
   private final MapperyContext mapperyContext;
   private final ReLoginInteractor reLoginInteractor;
   private final Observable<Device> deviceSource;
   private final Set<Object> retriedActions = new CopyOnWriteArraySet<>();
   private final AuthRetryPolicy retryPolicy;
   private final String apiVersion;

   public NewDreamTripsHttpService(SessionHolder appSessionHolder, AppVersionNameBuilder appVersionNameBuilder,
         MapperyContext mapperyContext, AuthRetryPolicy retryPolicy, ReLoginInteractor reLoginInteractor, Observable<Device> deviceSource,
         String baseUrl, HttpClient client, Converter converter, String apiVersion) {
      super(new HttpActionService(baseUrl, client, converter));
      this.appSessionHolder = appSessionHolder;
      this.appVersionNameBuilder = appVersionNameBuilder;
      this.mapperyContext = mapperyContext;
      this.reLoginInteractor = reLoginInteractor;
      this.deviceSource = deviceSource;
      this.retryPolicy = retryPolicy;
      this.apiVersion = apiVersion;
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
      A action = holder.action();
      if (action instanceof BaseHttpAction) prepareNewHttpAction((BaseHttpAction) action);

      return false;
   }

   private void prepareNewHttpAction(BaseHttpAction action) {
      action.setAppVersionHeader(appVersionNameBuilder.getSemanticVersionName());
      action.setAppLanguageHeader(LocaleHelper.getDefaultLocaleFormatted());
      action.setApiVersionForAccept(apiVersion);
      action.setAppPlatformHeader(String.format("android-%d", Build.VERSION.SDK_INT));

      if (action instanceof AuthorizedHttpAction && appSessionHolder.get().isPresent()) {
         UserSession userSession = appSessionHolder.get().get();
         ((AuthorizedHttpAction) action).setAuthorizationHeader(getAuthorizationHeader(userSession.apiToken()));
      }
   }

   @NonNull
   public static String getAuthorizationHeader(String apiToken) {
      return "Token token=" + apiToken;
   }


   @Override
   protected <A> void onInterceptCancel(ActionHolder<A> holder) {
   }

   @Override
   protected <A> void onInterceptStart(ActionHolder<A> holder) {
   }

   @Override
   protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {
   }

   @Override
   protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
      retriedActions.remove(holder.action());
   }

   @Override
   protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
      //checking with retry-login policy
      if (holder.action() instanceof AuthorizedHttpAction && !retriedActions.remove(holder.action())) {
         AuthorizedHttpAction action = (AuthorizedHttpAction) holder.action();
         String authHeader = action.getAuthorizationHeader();
         synchronized (this) {
            if (!authHeader.endsWith(appSessionHolder.get().get().apiToken())) {
               prepareNewHttpAction(action);
               Timber.d("Action %s will be sent again because of invalid token", action);
               return true;
            }
            boolean shouldRetry = retryPolicy.handle(e, this::createSession);
            if (shouldRetry) {
               Timber.d("Action %s will be sent again after relogining", action);
               prepareNewHttpAction(action);
               retriedActions.add(action);
            }
            return shouldRetry;
         }
      }
      return false;
   }

   @Nullable
   private Session createSession() {
      UserSession userSession = appSessionHolder.get().get();
      String username = userSession.username();
      String userPassword = userSession.userPassword();
      Device device = deviceSource.toBlocking().first();
      LoginHttpAction loginAction = new LoginHttpAction(username, userPassword, device);
      prepareNewHttpAction(loginAction);
      ActionState<LoginHttpAction> loginState = reLoginInteractor.loginHttpActionPipe()
            .createObservable(loginAction).toBlocking().last();
      if (loginState.status == ActionState.Status.SUCCESS) {
         return mapperyContext.convert(loginState.action.response(), Session.class);
      } else {
         Timber.w(loginState.exception, "Login error");
      }
      return null;
   }
}
