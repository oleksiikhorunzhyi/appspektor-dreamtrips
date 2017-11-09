package com.worldventures.wallet.service.nxt;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.api.smart_card.nxt.CreateNxtSessionHttpAction;
import com.worldventures.wallet.domain.session.NxtSessionHolder;
import com.worldventures.wallet.service.WalletSocialInfoProvider;
import com.worldventures.wallet.service.nxt.model.ImmutableMultiRequestBody;
import com.worldventures.wallet.service.nxt.model.NxtSession;

import java.net.HttpURLConnection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

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

   private final NxtSessionHolder nxtSessionHolder;
   private final AppVersionNameBuilder appVersionNameBuilder;
   private final WalletSocialInfoProvider socialInfoProvider;
   private final MapperyContext mapperyContext;
   private final NxtAuthRetryPolicy nxtAuthRetryPolicy;
   private final ActionPipe<CreateNxtSessionHttpAction> createNxtSessionHttpActionPipe;
   private final Set<Object> retriedActions = new CopyOnWriteArraySet<>();
   private final NxtIdConfigsProvider nxtIdConfigsProvider;

   public NxtHttpService(NxtSessionHolder nxtSessionHolder, AppVersionNameBuilder appVersionNameBuilder,
         WalletSocialInfoProvider socialInfoProvider, MapperyContext mapperyContext, String baseUrl,
         HttpClient client, Converter converter, NxtIdConfigsProvider nxtIdConfigsProvider) {
      super(new HttpActionService(baseUrl, client, converter));
      this.nxtSessionHolder = nxtSessionHolder;
      this.appVersionNameBuilder = appVersionNameBuilder;
      this.socialInfoProvider = socialInfoProvider;
      this.mapperyContext = mapperyContext;
      this.createNxtSessionHttpActionPipe = new Janet.Builder()
            .addService(new HttpActionService(nxtIdConfigsProvider.nxtidSessionApi(), client, converter))
            .build()
            .createPipe(CreateNxtSessionHttpAction.class);
      this.nxtAuthRetryPolicy = new NxtAuthRetryPolicy(nxtSessionHolder);
      this.nxtIdConfigsProvider = nxtIdConfigsProvider;
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
      //do nothing
   }

   @Override
   protected <A> void onInterceptStart(ActionHolder<A> holder) {
      //do nothing
   }

   @Override
   protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {
      //do nothing
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
      action.setApiVersionForAccept(nxtIdConfigsProvider.apiVersion());
      action.setAppPlatformHeader(String.format("android-%d", Build.VERSION.SDK_INT));
      //
      if (action instanceof AuthorizedHttpAction && socialInfoProvider.hasUser()) {
         ((AuthorizedHttpAction) action).setAuthorizationHeader("Token token=" + socialInfoProvider.apiToken());
      }
   }
}
