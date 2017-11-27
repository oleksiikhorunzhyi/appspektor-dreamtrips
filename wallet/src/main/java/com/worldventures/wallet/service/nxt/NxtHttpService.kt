package com.worldventures.wallet.service.nxt

import android.os.Build
import com.worldventures.core.utils.AppVersionNameBuilder
import com.worldventures.core.utils.LocaleHelper
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction
import com.worldventures.dreamtrips.api.smart_card.nxt.CreateNxtSessionHttpAction
import com.worldventures.wallet.domain.session.NxtSessionHolder
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.service.nxt.model.MultiRequestBody
import com.worldventures.wallet.service.nxt.model.NxtSession
import io.techery.janet.ActionHolder
import io.techery.janet.ActionPipe
import io.techery.janet.ActionServiceWrapper
import io.techery.janet.ActionState
import io.techery.janet.HttpActionService
import io.techery.janet.Janet
import io.techery.janet.JanetException
import io.techery.janet.converter.Converter
import io.techery.janet.http.HttpClient
import io.techery.mappery.MapperyContext
import timber.log.Timber
import java.net.HttpURLConnection
import java.util.concurrent.CopyOnWriteArraySet

@Suppress("UnsafeCallOnNullableType")
class NxtHttpService(private val nxtSessionHolder: NxtSessionHolder, private val appVersionNameBuilder: AppVersionNameBuilder,
                     private val socialInfoProvider: WalletSocialInfoProvider, private val mapperyContext: MapperyContext, baseUrl: String,
                     client: HttpClient, converter: Converter, private val nxtIdConfigsProvider: NxtIdConfigsProvider) : ActionServiceWrapper(HttpActionService(baseUrl, client, converter)) {
   private val nxtAuthRetryPolicy: NxtAuthRetryPolicy = NxtAuthRetryPolicy(nxtSessionHolder)
   private val createNxtSessionHttpActionPipe: ActionPipe<CreateNxtSessionHttpAction> = Janet.Builder()
         .addService(HttpActionService(nxtIdConfigsProvider.nxtidSessionApi(), client, converter))
         .build() // inject janet via constructor
         .createPipe(CreateNxtSessionHttpAction::class.java)
   private val retriedActions = CopyOnWriteArraySet<Any>()

   @Suppress("UnsafeCast")
   override fun <A> onInterceptSend(holder: ActionHolder<A>): Boolean {
      if (holder.action() is MultifunctionNxtHttpAction) {
         nxtAuthRetryPolicy.handle { this.createSession() }

         val action = holder.action() as MultifunctionNxtHttpAction
         action.body = action.body.copy(sessionToken = pleaseGetSessionToken())
      }
      return false
   }

   override fun <A> onInterceptCancel(holder: ActionHolder<A>) {
      //do nothing
   }

   override fun <A> onInterceptStart(holder: ActionHolder<A>) {
      //do nothing
   }

   override fun <A> onInterceptProgress(holder: ActionHolder<A>, progress: Int) {
      //do nothing
   }

   override fun <A> onInterceptSuccess(holder: ActionHolder<A>) {
      retriedActions.remove(holder.action())
   }

   @Suppress("UNCHECKED_CAST", "UnsafeCast")
   override fun <A> onInterceptFail(holder: ActionHolder<A>, e: JanetException): Boolean {
      if (holder.action() is MultifunctionNxtHttpAction && !retriedActions.remove(holder.action())) {
         val action = holder.action() as MultifunctionNxtHttpAction
         if (action.getStatusCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            synchronized(this) {
               val currentSessionToken = pleaseGetSessionToken()
               if (!action.body.sessionToken.equals(currentSessionToken)) {
                  (holder as ActionHolder<MultifunctionNxtHttpAction>).newAction(copyActionWithFreshToken(action))
                  return true
               }

               val shouldRetry = nxtAuthRetryPolicy.handle(action.getErrorResponse()) { this.createSession() }
               if (shouldRetry) {
                  val freshTokenAction = copyActionWithFreshToken(action)
                  (holder as ActionHolder<MultifunctionNxtHttpAction>).newAction(freshTokenAction)
                  retriedActions.add(freshTokenAction)
               }
               return shouldRetry
            }
         }
      }
      return false
   }

   private fun copyActionWithFreshToken(action: MultifunctionNxtHttpAction): MultifunctionNxtHttpAction {
      return MultifunctionNxtHttpAction(MultiRequestBody(
            multiRequestElements = action.body.multiRequestElements,
            sessionToken = pleaseGetSessionToken()))
   }

   private fun pleaseGetSessionToken(): String? {
      val nxtSessionOptional = nxtSessionHolder.get()
      return if (nxtSessionOptional.isPresent) nxtSessionOptional.get().token else null
   }

   private fun createSession(): NxtSession? {
      val createNxtSessionHttpAction = CreateNxtSessionHttpAction()
      prepareAction(createNxtSessionHttpAction)
      val loginState = createNxtSessionHttpActionPipe.createObservable(createNxtSessionHttpAction)
            .toBlocking()
            .last()
      if (loginState.status == ActionState.Status.SUCCESS) {
         return mapperyContext.convert(loginState.action.response(), NxtSession::class.java)
      } else {
         Timber.w(loginState.exception, "Login error")
      }
      return null
   }

   private fun prepareAction(action: BaseHttpAction) {
      action.appVersionHeader = appVersionNameBuilder.semanticVersionName
      action.appLanguageHeader = LocaleHelper.getDefaultLocaleFormatted()
      action.setApiVersionForAccept(nxtIdConfigsProvider.apiVersion())
      action.appPlatformHeader = String.format("android-%d", Build.VERSION.SDK_INT)
      //
      if (action is AuthorizedHttpAction && socialInfoProvider.hasUser()) {
         action.authorizationHeader = "Token token=" + socialInfoProvider.apiToken()!!
      }
   }
}
