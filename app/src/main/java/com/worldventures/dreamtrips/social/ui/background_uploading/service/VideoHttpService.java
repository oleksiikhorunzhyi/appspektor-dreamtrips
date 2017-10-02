package com.worldventures.dreamtrips.social.ui.background_uploading.service;

import android.os.Build;
import android.support.annotation.Nullable;

import com.worldventures.core.model.Session;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.modules.auth.service.ReLoginInteractor;
import com.worldventures.core.service.AuthRetryPolicy;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.session.LoginHttpAction;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.video.http.BaseVideoHttpAction;

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

public class VideoHttpService extends ActionServiceWrapper {

   private final SessionHolder appSessionHolder;
   private final MapperyContext mapperyContext;
   private final AppVersionNameBuilder appVersionNameBuilder;
   private final ReLoginInteractor reLoginInteractor;
   private final Observable<Device> deviceSource;

   private final AuthRetryPolicy retryPolicy;
   private final Set<Object> retriedActions = new CopyOnWriteArraySet<>();

   public VideoHttpService(String baseUrl, HttpClient client, Converter converter, SessionHolder appSessionHolder,
         MapperyContext mapperyContext, AppVersionNameBuilder appVersionNameBuilder,
         ReLoginInteractor reLoginInteractor, Observable<Device> deviceSource) {
      super(new HttpActionService(baseUrl, client, converter));
      this.appSessionHolder = appSessionHolder;
      this.mapperyContext = mapperyContext;
      this.appVersionNameBuilder = appVersionNameBuilder;
      this.reLoginInteractor = reLoginInteractor;
      this.deviceSource = deviceSource;
      retryPolicy = new AuthRetryPolicy(this.appSessionHolder);
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
      A action = holder.action();
      if (action instanceof BaseVideoHttpAction) prepareNewHttpAction((BaseVideoHttpAction) action);
      return false;
   }

   private void prepareNewHttpAction(BaseVideoHttpAction action) {
      if (appSessionHolder.get().isPresent()) {
         UserSession userSession = appSessionHolder.get().get();
         action.setMemberId(userSession.getUsername());
         action.setSsoToken(userSession.getLegacyApiToken());
         action.setIdentifier("DTApp-Android-" + appVersionNameBuilder.getReleaseSemanticVersionName());
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
      if (holder.action() instanceof BaseVideoHttpAction && !retriedActions.remove(holder.action())) {
         BaseVideoHttpAction action = (BaseVideoHttpAction) holder.action();
         synchronized (this) {
            boolean shouldRetry = retryPolicy.handle(e, this::createSession);
            if (!action.getSsoToken().endsWith(appSessionHolder.get().get().getLegacyApiToken())) {
               prepareNewHttpAction(action);
               Timber.d("Action %s will be sent again because of invalid token", action);
               return true;
            }
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
      String username = userSession.getUsername();
      String userPassword = userSession.getUserPassword();
      Device device = deviceSource.toBlocking().first();
      LoginHttpAction loginAction = new LoginHttpAction(username, userPassword, device);

      loginAction.setAppVersionHeader(appVersionNameBuilder.getSemanticVersionName());
      loginAction.setAppLanguageHeader(LocaleHelper.getDefaultLocaleFormatted());
      loginAction.setApiVersionForAccept(BuildConfig.API_VERSION);
      loginAction.setAppPlatformHeader(String.format("android-%d", Build.VERSION.SDK_INT));

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
