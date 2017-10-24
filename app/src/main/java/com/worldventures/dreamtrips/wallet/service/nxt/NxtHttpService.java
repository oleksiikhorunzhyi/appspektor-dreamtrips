package com.worldventures.dreamtrips.wallet.service.nxt;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.techery.spares.session.NxtSessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.api.smart_card.nxt.CreateNxtSessionHttpAction;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
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
   @Inject WalletSocialInfoProvider socialInfoProvider;
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
      if (holder.action() instanceof MultifunctionNxtHttpAction) {
         nxtAuthRetryPolicy.handle(this::createSession);

         final MultifunctionNxtHttpAction action = (MultifunctionNxtHttpAction) holder.action();
         action.body = ((ImmutableMultiRequestBody) action.body).withSessionToken(pleaseGetSessionToken());
      }
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
               String currentSessionToken = pleaseGetSessionToken();
               if (!action.body.sessionToken().equals(currentSessionToken)) {
                  ((ActionHolder<MultifunctionNxtHttpAction>) holder).newAction(copyActionWithFreshToken(action));
                  return true;
               }

               boolean shouldRetry = nxtAuthRetryPolicy.handle(action.getErrorResponse(), this::createSession);
               if (shouldRetry) {
                  MultifunctionNxtHttpAction freshTokenAction = copyActionWithFreshToken(action);
                  ((ActionHolder<MultifunctionNxtHttpAction>) holder).newAction(freshTokenAction);
                  retriedActions.add(freshTokenAction);
               }
               return shouldRetry;
            }
         }
      }
      return false;
   }

   @NonNull
   private MultifunctionNxtHttpAction copyActionWithFreshToken(MultifunctionNxtHttpAction action) {
      return new MultifunctionNxtHttpAction(ImmutableMultiRequestBody.builder()
            .multiRequestElements(action.body.multiRequestElements())
            .sessionToken(pleaseGetSessionToken())
            .build());
   }

   @Nullable
   private String pleaseGetSessionToken() {
      Optional<NxtSession> nxtSessionOptional = nxtSessionHolder.get();
      return (nxtSessionOptional.isPresent()) ? nxtSessionOptional.get().token() : null;
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
      if (action instanceof AuthorizedHttpAction && socialInfoProvider.hasUser()) {
         ((AuthorizedHttpAction) action).setAuthorizationHeader("Token token=" + socialInfoProvider.apiToken());
      }
   }
}