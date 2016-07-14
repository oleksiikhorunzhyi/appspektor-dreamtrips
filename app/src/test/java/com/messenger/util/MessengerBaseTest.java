package com.messenger.util;

import android.support.annotation.CallSuper;

import com.messenger.storage.MessengerDatabase;
import com.worldventures.dreamtrips.core.test.BaseTest;

import org.junit.Before;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@PrepareForTest(MessengerDatabase.class)
public abstract class MessengerBaseTest extends BaseTest {

    @CallSuper
    @Before
    public void setUp(){
        PowerMockito.mockStatic(MessengerDatabase.class);
        when(MessengerDatabase.buildUri(any())).thenReturn(null);
    }

}
