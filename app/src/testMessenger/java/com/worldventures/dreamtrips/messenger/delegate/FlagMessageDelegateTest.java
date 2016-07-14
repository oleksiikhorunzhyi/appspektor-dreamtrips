package com.worldventures.dreamtrips.messenger.delegate;

import com.messenger.delegate.chat.flagging.FlagMessageCommand;
import com.messenger.delegate.chat.flagging.FlagMessageDTO;
import com.messenger.delegate.chat.flagging.FlagMessageDelegate;
import com.messenger.delegate.chat.flagging.FlagMessageException;
import com.messenger.delegate.chat.flagging.ImmutableFlagMessageDTO;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.loaders.FlagMessageLoader;
import com.messenger.storage.MessengerDatabase;
import com.worldventures.dreamtrips.messenger.util.BaseTest;
import com.worldventures.dreamtrips.messenger.util.janet.BaseCommandActionServiceWrapper;
import com.worldventures.dreamtrips.messenger.util.serverfacade.BaseLoaderManager;
import com.worldventures.dreamtrips.messenger.util.serverfacade.MockFlagMessageLoader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.JanetException;
import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;


@RunWith(PowerMockRunner.class)
@PrepareForTest(MessengerDatabase.class)
public class FlagMessageDelegateTest extends BaseTest {

    private MessengerServerFacade messengerServerFacade;
    private FlagMessageDelegate flagMessageDelegate;

    @Before
    public void setup() {
        messengerServerFacade = Mockito.mock(MessengerServerFacade.class);
        flagMessageDelegate = new FlagMessageDelegate(mockJanet());
    }

    @Test
    public void flagMessage_TestSuccess() {
        FlagMessageDTO flagMessageDTO = ImmutableFlagMessageDTO.builder()
                .messageId("5523453245")
                .groupId("fsadfsadfsd@wordlventrues.com")
                .reasonDescription("Harassment")
                .reasonId("123414f13214")
                .build();

        mockFlaggingLoader(provideSuccessResponse(flagMessageDTO));

        flagMessageDelegate.flagMessage(flagMessageDTO);

        flagMessageDelegate
                .observeOngoingFlagging()
                .subscribe(state -> {
                    switch (state.status) {
                        case SUCCESS:
                            assertEquals(state.action.getResult().result(), "success");
                            break;
                        case FAIL:
                            fail();
                            break;
                    }
                });
    }

    @Test
    public void flagMessage_TestError() {
        FlagMessageDTO flagMessageDTO = ImmutableFlagMessageDTO.builder()
                .messageId("5523453245")
                .reasonDescription("Harassment")
                .reasonId("123414f13214")
                .groupId("fsadfsadfsd@wordlventrues.com")
                .build();

        mockFlaggingLoader(provideErrorResponse(flagMessageDTO));

        flagMessageDelegate.flagMessage(flagMessageDTO);

        flagMessageDelegate
                .observeOngoingFlagging()
                .subscribe(state -> {
                            switch (state.status) {
                                case SUCCESS:
                                    fail();
                                    break;
                                case FAIL:
                                    assertTrue(state.exception.getCause() instanceof FlagMessageException);
                                    FlagMessageException xmmpError = (FlagMessageException) state.exception.getCause();
                                    assertEquals(xmmpError.getMessageId(), flagMessageDTO.messageId());
                                    break;
                            }
                        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    private void mockFlaggingLoader(Observable.OnSubscribe<FlagMessageDTO> onSubscribe) {
        Mockito.doReturn(new BaseLoaderManager() {
            @Override
            public FlagMessageLoader createFlaggingLoader() {
                return new MockFlagMessageLoader(onSubscribe);
            }
        }).when(messengerServerFacade).getLoaderManager();
    }

    private Observable.OnSubscribe<FlagMessageDTO> provideSuccessResponse(FlagMessageDTO flagMessageDTO) {
        return subscriber -> subscriber.onNext(ImmutableFlagMessageDTO
                .copyOf(flagMessageDTO)
                .withResult("success"));
    }

    private Observable.OnSubscribe<FlagMessageDTO> provideErrorResponse(FlagMessageDTO flagMessageDTO) {
        return subscriber -> subscriber
                .onError(new FlagMessageException(flagMessageDTO.messageId(), "not_exists"));
    }

    private Janet mockJanet() {
        return new Janet.Builder()
                .addService(new BaseCommandActionServiceWrapper() {
                    @Override
                    protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
                        ((FlagMessageCommand) holder.action()).setMessengerServerFacade(messengerServerFacade);
                        return false;
                    }
                })
                .build();
    }
}
