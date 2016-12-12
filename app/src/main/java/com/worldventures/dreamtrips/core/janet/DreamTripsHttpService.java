package com.worldventures.dreamtrips.core.janet;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.session.LoginHttpAction;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.core.api.AuthRetryPolicy;
import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;
import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.Session;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionPipe;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.ActionState;
import io.techery.janet.HttpActionService;
import io.techery.janet.Janet;
import io.techery.janet.JanetException;
import io.techery.janet.converter.Converter;
import io.techery.janet.http.HttpClient;
import io.techery.mappery.MapperyContext;
import rx.Observable;
import timber.log.Timber;

/**
 * @deprecated Use {@link NewDreamTripsHttpService}
 */
@Deprecated
public class DreamTripsHttpService extends ActionServiceWrapper {

   @Inject SessionHolder<UserSession> appSessionHolder;
   @Inject AppVersionNameBuilder appVersionNameBuilder;
   @Inject SnappyRepository db;
   @Inject Observable<Device> deviceSource;
   @Inject MapperyContext mapperyContext;

   private final ActionPipe<LoginHttpAction> loginActionPipe;
   private final Set<Object> retriedActions = new CopyOnWriteArraySet<>();
   private final AuthRetryPolicy retryPolicy;

   public DreamTripsHttpService(Context appContext, String baseUrl, HttpClient client, Converter converter, Converter newConverter) {
      super(new HttpActionService(baseUrl, client, converter));
      ((Injector) appContext).inject(this);
      loginActionPipe = new Janet.Builder().addService(new HttpActionService(baseUrl, client, newConverter))
            .build()
            .createPipe(LoginHttpAction.class);
      retryPolicy = new AuthRetryPolicy(appSessionHolder);
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
      A action = holder.action();
      if ((action instanceof BaseHttpAction)) prepareHttpAction((BaseHttpAction) action);

      return false;
   }

   private void prepareHttpAction(BaseHttpAction action) {
      action.setAppVersionHeader(appVersionNameBuilder.getSemanticVersionName());
      action.setLanguageHeader(LocaleHelper.getDefaultLocaleFormatted());
      if (action instanceof AuthorizedHttpAction && appSessionHolder.get().isPresent()) {
         UserSession userSession = appSessionHolder.get().get();
         ((AuthorizedHttpAction) action).setAuthorizationHeader(NewDreamTripsHttpService.getAuthorizationHeader(userSession.getApiToken()));
      }
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
            if (!authHeader.endsWith(appSessionHolder.get().get().getApiToken())) {
               prepareHttpAction(action);
               Timber.d("Action %s will be sent again because of invalid token", action);
               return true;
            }
            boolean shouldRetry = retryPolicy.handle(e, this::createSession);
            if (shouldRetry) {
               Timber.d("Action %s will be sent again after relogining", action);
               prepareHttpAction(action);
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
      String username = userSession.getUsername();
      String userPassword = userSession.getUserPassword();
      Device device = deviceSource.toBlocking().first();
      LoginHttpAction loginAction = new LoginHttpAction(username, userPassword, device);
      loginAction.setAppVersionHeader(appVersionNameBuilder.getSemanticVersionName());
      loginAction.setAppLanguageHeader(localeHelper.getDefaultLocaleFormatted());
      loginAction.setApiVersionForAccept(BuildConfig.API_VERSION);
      loginAction.setAppPlatformHeader(String.format("android-%d", Build.VERSION.SDK_INT));

      ActionState<LoginHttpAction> loginState = loginActionPipe.createObservable(loginAction).toBlocking().last();
      if (loginState.status == ActionState.Status.SUCCESS) {
         return mapperyContext.convert(loginState.action.response(), Session.class);
      } else {
         Timber.w(loginState.exception, "Login error");
      }
      return null;
   }
}
