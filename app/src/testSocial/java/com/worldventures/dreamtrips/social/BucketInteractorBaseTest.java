package com.worldventures.dreamtrips.social;

import android.support.annotation.CallSuper;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.test.BaseTest;
import com.worldventures.dreamtrips.core.test.MockDaggerActionService;
import com.worldventures.dreamtrips.core.test.MockHttpActionService;
import com.worldventures.dreamtrips.core.test.StubServiceWrapper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage;
import com.worldventures.dreamtrips.modules.common.model.User;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BucketInteractorBaseTest extends BaseTest {
    static final int MOCK_USER_ID = 1;

    @Mock
    SessionHolder<UserSession> mockSessionHolder;
    @Spy
    MemoryStorage<List<BucketItem>> mockMemoryStorage;

    @Mock
    SnappyRepository mockDb;

    Janet janet;

    StubServiceWrapper httpStubWrapper;

    BucketInteractor bucketInteractor;

    UserSession userSession;

    private MockDaggerActionService daggerActionService;

    @CallSuper
    @Before
    public void setup() throws URISyntaxException, IOException {
        MockitoAnnotations.initMocks(this);

        CacheResultWrapper cacheResultWrapper = cachedService(
                daggerActionService = new MockDaggerActionService(new CommandActionService()));
        for (ActionStorage storage : storageSet()) {
            cacheResultWrapper.bindStorage(storage.getActionClass(), storage);
        }

        janet = new Janet.Builder()
                .addService(cacheResultWrapper)
                .addService(cachedService(
                        httpStubWrapper = new StubServiceWrapper(mockHttpService()))
                )
                .build();

        daggerActionService.registerProvider(Janet.class, () -> janet);
        daggerActionService.registerProvider(SnappyRepository.class, () -> mockDb);
        daggerActionService.registerProvider(SessionHolder.class, () -> mockSessionHolder);
        daggerActionService.registerProvider(BucketInteractor.class, () -> bucketInteractor);

        bucketInteractor = new BucketInteractor(janet);

        userSession = mock(UserSession.class);
        User mockUser = mock(User.class);

        when(mockUser.getId()).thenReturn(MOCK_USER_ID);
        when(userSession.getUser()).thenReturn(mockUser);
        when(mockSessionHolder.get()).thenReturn(Optional.of(userSession));
    }

    protected abstract MockHttpActionService mockHttpService();

    MockDaggerActionService daggerActionService() {
        return daggerActionService;
    }

    protected Set<ActionStorage> storageSet() {
        Set<ActionStorage> storageSet = new HashSet<>();
        storageSet.add(new BucketListDiskStorage(mockMemoryStorage, mockDb, mockSessionHolder));

        return storageSet;
    }
}