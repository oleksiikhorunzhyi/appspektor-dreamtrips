package com.worldventures.dreamtrips.messenger.util;

import com.messenger.storage.MessengerDatabase;
import com.worldventures.dreamtrips.common.BaseTest;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MessengerDatabase.class)
public class MessengerBaseTest extends BaseTest {

    @Before
    public void initMocks() {
        PowerMockito.mockStatic(MessengerDatabase.class);
        when(MessengerDatabase.buildUri(any())).thenReturn(null);
    }
}
