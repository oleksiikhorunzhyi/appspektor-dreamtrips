package com.worldventures.dreamtrips.wallet.service.nxt;

import android.content.Context;
import android.os.Build;

import com.techery.spares.module.Injector;
import com.techery.spares.session.NxtSessionHolder;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.api.smart_card.nxt.CreateNxtSessionHttpAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.NxtSession;

import java.net.HttpURLConnection;
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
import timber.log.Timber;

public class NxtHttpService extends ActionServiceWrapper {

   @Inject NxtSessionHolder nxtSessionHolder;
   @Inject AppVersionNameBuilder appVersionNameBuilder;
   @Inject SessionHolder<UserSession> appSessionHolder;
   @Inject MapperyContext mapperyContext;

   private NxtAuthRetryPolicy nxtAuthRetryPolicy;
   private ActionPipe<CreateNxtSessionHttpAction> createNxtSessionHttpActionPipe;
   private final Set<Object> retriedActions = new CopyOnWriteArraySet<>();

   public NxtHttpService(Context appContext, String baseUrl, HttpClient client, Converter converter) {
      super(new HttpActionService(baseUrl, client, converter));
      ((Injector) appContext).inject(this);
      createNxtSessionHttpActionPipe = new Janet.Builder().addService(new HttpActionService(BuildConfig.DreamTripsApi, client, converter))
            .build()
            .createPipe(CreateNxtSessionHttpAction.class);
      nxtAuthRetryPolicy = new NxtAuthRetryPolicy(nxtSessionHolder);
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
      return false;
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
      if (holder.action() instanceof MultifunctionNxtHttpAction && !retriedActions.remove(holder.action())) {
         MultifunctionNxtHttpAction action = (MultifunctionNxtHttpAction) holder.action();
         if (action.getStatusCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            synchronized (this) {
               if (!action.body.sessionToken().equals(nxtSessionHolder.get().get().token())) {
                  ((ActionHolder<MultifunctionNxtHttpAction>) holder).newAction(new MultifunctionNxtHttpAction(
                        ImmutableMultiRequestBody.builder()
                              .multiRequestElements(action.body.multiRequestElements())
                              .sessionToken(nxtSessionHolder.get().get().token())
                              .build()));
                  return true;
               }

               boolean shouldRetry = nxtAuthRetryPolicy.handle(action.getErrorResponse(), this::createSession);
               if (shouldRetry) {
                  retriedActions.add(action);
               }

               return shouldRetry;
            }
         }
      }
      return false;
   }

   private NxtSession createSession() {
      CreateNxtSessionHttpAction createNxtSessionHttpAction = new CreateNxtSessionHttpAction();
      prepareAction(createNxtSessionHttpAction);
      ActionState<CreateNxtSessionHttpAction> loginState = createNxtSessionHttpActionPipe.createObservable(createNxtSessionHttpAction)
            .toBlocking()
            .last();
      if (loginState.status == ActionState.Status.SUCCESS) {
         return mapperyContext.convert(loginState.action.response(), NxtSession.class);
      } else {
         Timber.w(loginState.exception, "Login error");
      }
      return null;
   }

   private void prepareAction(BaseHttpAction action) {
      action.setAppVersionHeader(appVersionNameBuilder.getSemanticVersionName());
      action.setAppLanguageHeader(LocaleHelper.getDefaultLocaleFormatted());
      action.setApiVersionForAccept(BuildConfig.API_VERSION);
      action.setAppPlatformHeader(String.format("android-%d", Build.VERSION.SDK_INT));
      //
      if (action instanceof AuthorizedHttpAction && appSessionHolder.get().isPresent()) {
         UserSession userSession = appSessionHolder.get().get();
         ((AuthorizedHttpAction) action).setAuthorizationHeader("Token token=" + userSession.getApiToken());
      }
   }
}