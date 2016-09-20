package com.worldventures.dreamtrips.social.auth.spec.login

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.techery.spares.session.SessionHolder
import com.techery.spares.storage.complex_objects.Optional
import com.worldventures.dreamtrips.AssertUtil.assertActionFail
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.core.session.UserSession
import com.worldventures.dreamtrips.core.session.acl.Feature
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor
import com.worldventures.dreamtrips.modules.common.model.Session
import com.worldventures.dreamtrips.modules.common.model.User
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import org.mockito.internal.verification.VerificationModeFactory
import rx.observers.TestSubscriber

class LoginInteractorSpec : BaseSpec({

   describe("Test login action") {

      setup { mockHttpServiceForLogin() }

      context("Perform login request with username and password") {
         whenever(sessionHolderMock.get()).thenReturn(Optional.absent())

         val testSubscribe = login(USERNAME, PASSWORD)

         assertActionSuccess(testSubscribe) {
            isUserSessionValid(it.result)
         }
      }

      context("Perform relogin request without username and password") {
         whenever(sessionHolderMock.get()).thenReturn(Optional.of(userSessionMock))
         whenever(userSessionMock.username).thenReturn(USERNAME)
         whenever(userSessionMock.userPassword).thenReturn(PASSWORD)

         val testSubscribe = relogin()

         assertActionSuccess(testSubscribe) {
            isUserSessionValid(it.result) &&
                  !it.result.apiToken.equals(userSessionMock.apiToken)
         }
      }

      context("Perform login with null parameters") {
         whenever(sessionHolderMock.get()).thenReturn(Optional.absent())

         val testSubscribe = login(null, null)

         assertActionFail(testSubscribe) {
            it.cause!!.message.equals("You have to set username and password")
         }
      }

      context("Verify session saved to storage") {
         verify(sessionHolderMock, VerificationModeFactory.times(2)).put(any())
      }

      context("Verify settings saved to DB") {
         verify(mockDB, VerificationModeFactory.times(2)).saveSettings(any(), any())
      }
   }
}) {
   companion object BaseCompanion {

      val API_TOKEN: String = "apiToken"
      val SSO_TOKEN: String = "ssoToken"
      val USER_ID: Int = 1488
      val FEATURES: List<Feature> = listOf(Feature("feature1"), Feature("feature2"))
      val LOCALE: String = "zh-rCN"
      val USERNAME: String = "username"
      val PASSWORD: String = "password"

      val sessionHolderMock: SessionHolder<UserSession> = mock()
      val userSessionMock: UserSession = mock()
      val mockDB: SnappyRepository = spy()

      lateinit var loginInteractor: LoginInteractor

      fun setup(httpService: () -> MockHttpActionService) {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService().wrapStub().wrapCache())
               .build()
         val sessionPiperCreator = SessionActionPipeCreator(janet)
         val authInteractor = AuthInteractor(sessionPiperCreator)

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(SessionHolder::class.java) { sessionHolderMock }
         daggerCommandActionService.registerProvider(LoginInteractor::class.java) { loginInteractor }
         daggerCommandActionService.registerProvider(AuthInteractor::class.java) { authInteractor }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDB }

         loginInteractor = LoginInteractor(sessionPiperCreator)

         whenever(sessionHolderMock.put(any())).then { }
      }

      fun login(username: String?, userPassword: String?): TestSubscriber <ActionState<LoginCommand>> {
         val testSubscriber = TestSubscriber <ActionState<LoginCommand>>()

         loginInteractor.loginActionPipe()
               .createObservable(LoginCommand(username, userPassword))
               .subscribe(testSubscriber)

         return testSubscriber
      }

      fun relogin(): TestSubscriber <ActionState<LoginCommand>> {
         val testSubscriber = TestSubscriber <ActionState<LoginCommand>>()

         loginInteractor.loginActionPipe()
               .createObservable(LoginCommand())
               .subscribe(testSubscriber)

         return testSubscriber
      }

      fun mockHttpServiceForLogin(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200).body(session())) {
                  request ->
                  request.url.contains("/api/sessions")
               }
               .build()
      }

      fun session(): Session {
         val session: Session = Session()
         session.locale = LOCALE
         session.ssoToken = SSO_TOKEN
         session.token = API_TOKEN
         session.user = User(USER_ID)
         session.permissions = FEATURES
         return session
      }

      fun isUserSessionValid(userSession: UserSession): Boolean {
         return userSession.apiToken.equals(API_TOKEN) &&
               userSession.username.equals(USERNAME) &&
               userSession.userPassword.equals(PASSWORD) &&
               userSession.locale.equals(LOCALE) &&
               userSession.legacyApiToken.equals(SSO_TOKEN) &&
               userSession.features.equals(FEATURES) &&
               userSession.user.id == USER_ID
      }
   }
}
