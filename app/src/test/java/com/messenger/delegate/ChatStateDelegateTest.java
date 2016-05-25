package com.messenger.delegate;

import com.messenger.delegate.chat.typing.ChatStateDelegate;
import com.messenger.messengerservers.chat.ChatState;
import com.messenger.storage.MessengerDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.Observable;
import rx.Subscriber;

import static junit.framework.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MessengerDatabase.class)
public class ChatStateDelegateTest extends BaseChatActionDelegateTest {

    private ChatStateDelegate chatStateDelegate;

    @Before
    public void setup() {
        chatStateDelegate = new ChatStateDelegate(mockJanet());
    }

    @Test
    public void testChatDelegate_TypingStarted() {
        chatStateDelegate.init("");
        chatStateDelegate.connectTypingStopAction(Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("h");
                subscriber.onNext("hi");
            }
        }))
        .subscribe(state -> {
            assertEquals(state, ChatState.COMPOSING);
        });
    }

    @Test
    public void testChatDelegate_TypingStopped() {
        chatStateDelegate.init("");
        chatStateDelegate.connectTypingStopAction(Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("h");
                subscriber.onNext("hi");
                try {
                    Thread.sleep(ChatStateDelegate.STOP_TYPING_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }))
        .subscribe(state -> {
            assertEquals(state, ChatState.PAUSE);
        });
    }

}
