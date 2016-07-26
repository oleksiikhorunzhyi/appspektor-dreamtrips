package com.worldventures.dreamtrips.messenger.delegate;

import com.messenger.api.TranslationInteractor;
import com.messenger.delegate.MessageTranslationDelegate;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.storage.MessengerDatabase;
import com.messenger.storage.dao.TranslationsDAO;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.api.messenger.model.response.TranslatedText;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.messenger.util.MessengerBaseTest;
import com.worldventures.dreamtrips.modules.common.model.User;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;

import io.techery.janet.Janet;
import io.techery.janet.http.test.MockHttpActionService;
import rx.Observable;
import rx.observers.TestSubscriber;

import static com.worldventures.dreamtrips.AssertUtil.assertSubscriberWithoutErrorAndValues;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// There are test methods names like translationStatus_typeMessage,
// where Status - status which we receive from server side success/failed
// type - status of Translation notTranslated/Translating/Error/Reverted

@PrepareForTest({MessengerDatabase.class, TrackingHelper.class})
public class MessageTranslationDelegateTest extends MessengerBaseTest {

    User testUser;
    TranslationsDAO translationsDAO;
    SessionHolder<UserSession> userSessionHolder;
    LocaleHelper localeHelper;
    Janet janet;

    DataMessage testMessage;
    private ArrayList<DataTranslation> localeCache;

    @Before
    public void setup() {
        testUser = obtainMockUser();
        userSessionHolder = obtainMockUserSession(testUser);
        localeHelper = obtainMockLocaleHelper();

        testMessage = new DataMessage.Builder().id("21").text("Привет мир").build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////// Cases when we receive success from server
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void translationSuccess_notTranslatedMessage() {
        translateAndCheckSuccess(null);
    }

    @Test
    public void translationSuccess_translatingMessage() {
        mockJanetWithResponse(createSuccessResponse());

        DataTranslation translationFromDВ = new DataTranslation(testMessage.getId(), null, TranslationStatus.TRANSLATING);
        TestSubscriber<DataTranslation> testSubscriber = new TestSubscriber<>();
        mockTranslationDAO(translationFromDВ, testMessage.getId(), testSubscriber);

        translateMessage();

        verify(translationsDAO, times(0)).save(any(DataTranslation.class));
        verify(translationsDAO, times(1)).getTranslation(testMessage.getId());
        verify(localeHelper, times(1)).getAccountLocaleFormatted(testUser);

        PowerMockito.verifyStatic(times(0));
        TrackingHelper.translateMessage(anyString());

        assertSubscriberWithoutErrorAndValues(testSubscriber);
    }

    @Test
    public void translationSuccess_errorMessage() {
        translateAndCheckSuccess(new DataTranslation(testMessage.getId(), null, TranslationStatus.ERROR));
    }

    private void translateAndCheckSuccess(DataTranslation translationFromDB) {
        mockJanetWithResponse(createSuccessResponse());
        TestSubscriber<DataTranslation> testSubscriber = new TestSubscriber<>();
        mockTranslationDAO(translationFromDB, testMessage.getId(), testSubscriber);

        translateMessage();

        assertEquals(localeCache.size(), 2);
        checkTranslationWithTranslatingStatus(localeCache.get(0));
        checkTranslationWithTranslatedStatus(localeCache.get(1));

        verify(translationsDAO, times(2)).save(any(DataTranslation.class));
        verify(translationsDAO, times(1)).getTranslation(testMessage.getId());
        verify(localeHelper, times(1)).getAccountLocaleFormatted(testUser);

        PowerMockito.verifyStatic();
        TrackingHelper.translateMessage(anyString());

        assertSubscriberWithoutErrorAndValues(testSubscriber);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////// Case when we shouldn't send a translation request
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void translationSuccess_revertedMessage() {
        mockJanetWithResponse(createSuccessResponse());

        DataTranslation translationFromDB = new DataTranslation(testMessage.getId(), "Hello world", TranslationStatus.REVERTED);
        TestSubscriber<DataTranslation> testSubscriber = new TestSubscriber<>();
        mockTranslationDAO(translationFromDB, testMessage.getId(), testSubscriber);

        translateMessage();

        assertEquals(localeCache.size(), 1);
        checkTranslationWithTranslatedStatus(localeCache.get(0));

        verify(translationsDAO, times(1)).save(any(DataTranslation.class));
        verify(translationsDAO, times(1)).getTranslation(testMessage.getId());
        verify(localeHelper, times(1)).getAccountLocaleFormatted(testUser);

        PowerMockito.verifyStatic();
        TrackingHelper.translateMessage(anyString());

        assertSubscriberWithoutErrorAndValues(testSubscriber);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////// Cases when we receive error from server
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void translationFailed_notTranslatedMessage() {
        translateAndCheckError(null);
    }

    @Test
    public void translationFailed_errorMessage() {
        translateAndCheckError(new DataTranslation(testMessage.getId(), null, TranslationStatus.ERROR));
    }

    private void translateAndCheckError(DataTranslation translationFromDB) {
        mockJanetWithResponse(createErrorResponse());
        TestSubscriber<DataTranslation> testSubscriber = new TestSubscriber<>();
        mockTranslationDAO(translationFromDB, testMessage.getId(), testSubscriber);

        translateMessage();

        assertEquals(localeCache.size(), 2);
        checkTranslationWithTranslatingStatus(localeCache.get(0));
        checkTranslationWithErrorStatus(localeCache.get(1));

        verify(translationsDAO, times(2)).save(any(DataTranslation.class));
        verify(translationsDAO, times(1)).getTranslation(testMessage.getId());
        verify(localeHelper, times(1)).getAccountLocaleFormatted(testUser);

        PowerMockito.verifyStatic(times(0));
        TrackingHelper.translateMessage(anyString());

        assertSubscriberWithoutErrorAndValues(testSubscriber);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////// Helper methods
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void translateMessage() {
        MessageTranslationDelegate delegate = new MessageTranslationDelegate(new TranslationInteractor(janet),
                translationsDAO, localeHelper, userSessionHolder);
        delegate.translateMessage(testMessage);
    }

    private void mockJanetWithResponse(MockHttpActionService.Response response) {
        MockHttpActionService httpActionService = new MockHttpActionService.Builder()
                .bind(response, request -> request.getUrl().endsWith("/api/translate"))
                .build();
        janet = new Janet.Builder().addService(httpActionService).build();
    }

    private MockHttpActionService.Response createSuccessResponse() {
        TranslatedText mockTranslatedText = mock(TranslatedText.class);
        doReturn("Hello world").when(mockTranslatedText).text();
        return new MockHttpActionService.Response(200).body(mockTranslatedText);
    }

    private MockHttpActionService.Response createErrorResponse() {
        return new MockHttpActionService.Response(500).reason("Internal Server Error");
    }

    private void mockTranslationDAO(DataTranslation translationFromDB, String messageId, TestSubscriber<DataTranslation> testSubscriber) {
        localeCache = new ArrayList<>();
        translationsDAO = mock(TranslationsDAO.class);
        doAnswer(invocation -> {
            DataTranslation dataTranslation = (DataTranslation) invocation.getArguments()[0];
            localeCache.add(copyDataTranslation(dataTranslation));
            return null;
        }).when(translationsDAO).save(any(DataTranslation.class));

        Observable<DataTranslation> translationObservable = Observable.create(subscriber -> {
            subscriber.onNext(translationFromDB);
            subscriber.add(testSubscriber);
            subscriber.onCompleted();
        });

        doReturn(translationObservable).when(translationsDAO).getTranslation(messageId);
    }

    private DataTranslation copyDataTranslation(DataTranslation dataTranslation) {
        return new DataTranslation(dataTranslation.getId(), dataTranslation.getTranslation(), dataTranslation.getTranslateStatus());
    }

    private void checkTranslationWithTranslatingStatus(DataTranslation dataTranslation) {
        assertEquals(dataTranslation.getId(), testMessage.getId());
        assertEquals(dataTranslation.getTranslateStatus(), TranslationStatus.TRANSLATING);
        assertNull(dataTranslation.getTranslation());
    }

    private void checkTranslationWithTranslatedStatus(DataTranslation dataTranslation) {
        assertEquals(dataTranslation.getId(), testMessage.getId());
        assertEquals(dataTranslation.getTranslateStatus(), TranslationStatus.TRANSLATED);
        assertNotNull(dataTranslation.getTranslation());
    }

    private void checkTranslationWithErrorStatus(DataTranslation dataTranslation) {
        assertEquals(dataTranslation.getId(), testMessage.getId());
        assertEquals(dataTranslation.getTranslateStatus(), TranslationStatus.ERROR);
        assertNull(dataTranslation.getTranslation());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////// Mock Objects
    //////////////////////////////////////////////////////////////////////////////////////////////

    private User obtainMockUser() {
        User testUser = mock(User.class);
        doReturn("en-us").when(testUser).getLocale();

        return testUser;
    }

    private SessionHolder<UserSession> obtainMockUserSession(User user) {
        UserSession userSession = mock(UserSession.class);
        doReturn(user).when(userSession).getUser();

        Optional<UserSession> optionalMock = mock(Optional.class);
        doReturn(true).when(optionalMock).isPresent();
        doReturn(userSession).when(optionalMock).get();

        SessionHolder<UserSession> userHolderMock = mock(SessionHolder.class);
        doReturn(optionalMock).when(userHolderMock).get();

        return userHolderMock;
    }

    private LocaleHelper obtainMockLocaleHelper() {
        LocaleHelper localeHelperMock = mock(LocaleHelper.class);
        doReturn("en-us").when(localeHelperMock).getAccountLocaleFormatted(testUser);
        return localeHelperMock;
    }

}
