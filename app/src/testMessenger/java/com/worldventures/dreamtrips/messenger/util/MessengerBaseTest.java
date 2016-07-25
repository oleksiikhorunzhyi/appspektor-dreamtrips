package com.worldventures.dreamtrips.messenger.util;

import com.messenger.storage.MessengerDatabase;
import com.worldventures.dreamtrips.BaseTest;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@PrepareForTest({MessengerDatabase.class, TrackingHelper.class})
public abstract class MessengerBaseTest extends BaseTest {

    @Before
    public void initMocks() {
        PowerMockito.mockStatic(MessengerDatabase.class);
        when(MessengerDatabase.buildUri(any())).thenReturn(null);

        PowerMockito.mockStatic(TrackingHelper.class);
    }
}
