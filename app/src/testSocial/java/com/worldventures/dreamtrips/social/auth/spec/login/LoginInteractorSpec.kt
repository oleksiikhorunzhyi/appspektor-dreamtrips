package com.worldventures.dreamtrips.social.auth.spec.login

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.Session
import com.worldventures.core.model.User
import com.worldventures.core.model.session.Feature
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.modules.auth.api.command.LoginCommand
import com.worldventures.core.modules.auth.service.AuthInteractor
import com.worldventures.core.modules.settings.storage.SettingsStorage
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.core.test.AssertUtil.assertActionFail
import com.worldventures.core.test.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.session.model.Device
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.Mockito.`when`
import org.mockito.internal.verification.VerificationModeFactory
import rx.Observable
import rx.observers.TestSubscriber

class LoginInteractorSpec : BaseSpec({

   describe("Test login action") {

      it("Perform login request with username and password") {
         whenever(sessionHolderMock.get()).thenReturn(Optional.absent())

         val testSubscribe = login(USERNAME, PASSWORD)

         assertActionSuccess(testSubscribe) {
            isUserSessionValid(it.result)
         }
      }

      it("Perform relogin request without username and password") {
         whenever(sessionHolderMock.get()).thenReturn(Optional.of(userSessionMock))
         whenever(userSessionMock.username()).thenReturn(USERNAME)
         whenever(userSessionMock.userPassword()).thenReturn(PASSWORD)

         val testSubscribe = relogin()

         assertActionSuccess(testSubscribe) {
            isUserSessionValid(it.result) &&
                  !it.result.apiToken().equals(userSessionMock.apiToken())
         }
      }

      it("Perform login with null parameters") {
         whenever(sessionHolderMock.get()).thenReturn(Optional.absent())

         val testSubscribe = login(null, null)

         assertActionFail(testSubscribe) {
            it.cause!!.message.equals("You have to set username and password")
         }
      }

      it("Verify session saved to storage") {
         verify(sessionHolderMock, VerificationModeFactory.times(2)).put(any())
      }

      it("Verify settings saved to DB") {
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

      val sessionHolderMock: SessionHolder = mock()
      val userSessionMock: UserSession = mock()
      val apiSession: com.worldventures.dreamtrips.api.session.model.Session = mock()
      val mapperyContext: MapperyContext = mock()
      val mockDB: SettingsStorage = spy()
      val deviceObservable: Observable<Device> = Observable.just(null)

      lateinit var authInteractor: AuthInteractor

      init {
         setup { mockHttpServiceForLogin() }
      }

      fun setup(httpService: () -> MockHttpActionService) {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService().wrapStub().wrapCache())
               .build()
         val sessionPiperCreator = SessionActionPipeCreator(janet)

         authInteractor = AuthInteractor(sessionPiperCreator)

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(SessionHolder::class.java) { sessionHolderMock }
         daggerCommandActionService.registerProvider(AuthInteractor::class.java) { authInteractor }
         daggerCommandActionService.registerProvider(SettingsStorage::class.java) { mockDB }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mapperyContext }
         daggerCommandActionService.registerProvider(Observable::class.java) { deviceObservable }

         `when`(mapperyContext.convert(apiSession, Session::class.java)).thenReturn(session())

         whenever(sessionHolderMock.put(any())).then { }
      }

      fun login(username: String?, userPassword: String?): TestSubscriber <ActionState<LoginCommand>> {
         val testSubscriber = TestSubscriber <ActionState<LoginCommand>>()

         authInteractor.loginActionPipe()
               .createObservable(LoginCommand(username, userPassword))
               .subscribe(testSubscriber)

         return testSubscriber
      }

      fun relogin(): TestSubscriber <ActionState<LoginCommand>> {
         val testSubscriber = TestSubscriber <ActionState<LoginCommand>>()

         authInteractor.loginActionPipe()
               .createObservable(LoginCommand())
               .subscribe(testSubscriber)

         return testSubscriber
      }

      fun mockHttpServiceForLogin(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200).body(apiSession)) {
                  request ->
                  request.url.contains("/api/sessions")
               }
               .build()
      }

      fun session(): Session {
         val session = Session()
         session.locale = LOCALE
         session.ssoToken = SSO_TOKEN
         session.token = API_TOKEN
         session.user = User(USER_ID)
         session.permissions = FEATURES
         return session
      }

      fun isUserSessionValid(userSession: UserSession): Boolean {
         return userSession.apiToken().equals(API_TOKEN) &&
               userSession.username().equals(USERNAME) &&
               userSession.userPassword().equals(PASSWORD) &&
               userSession.locale().equals(LOCALE) &&
               userSession.legacyApiToken().equals(SSO_TOKEN) &&
               userSession.permissions()!! == FEATURES &&
               userSession.user()!!.id == USER_ID
      }
   }
}
