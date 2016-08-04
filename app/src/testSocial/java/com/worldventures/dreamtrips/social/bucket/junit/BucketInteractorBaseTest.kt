package com.worldventures.dreamtrips.social.bucket.junit

import android.support.annotation.CallSuper
import com.techery.spares.session.SessionHolder
import com.techery.spares.storage.complex_objects.Optional
import com.worldventures.dreamtrips.BaseTest
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.core.session.UserSession
import com.worldventures.dreamtrips.janet.MockDaggerActionService
import com.worldventures.dreamtrips.janet.StubServiceWrapper
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketMemoryStorage
import com.worldventures.dreamtrips.modules.common.model.User
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import java.util.*

abstract class BucketInteractorBaseTest : BaseTest() {
    init {
        MockitoAnnotations.initMocks(this)
    }

    @Mock var mockSessionHolder: SessionHolder<UserSession>? = null
    @Mock var mockDb: SnappyRepository? = null
    @Mock var staticPageProvider: StaticPageProvider? = null

    @Spy var mockMemoryStorage: BucketMemoryStorage? = null

    var janet: Janet? = null

    val httpStubWrapper: StubServiceWrapper by lazy {
        StubServiceWrapper(mockHttpService())
    }

    var bucketInteractor: BucketInteractor? = null

    var userSession: UserSession? = null

    lateinit var daggerActionService: MockDaggerActionService

    @CallSuper
    @Before
    open fun setup() {
        val cacheResultWrapper = cachedService(CommandActionService())
        for (storage in storageSet()) {
            cacheResultWrapper.bindStorage(storage.actionClass, storage)
        }
        daggerActionService = MockDaggerActionService(cacheResultWrapper)
        daggerActionService.registerProvider(Janet::class.java) { janet }
        daggerActionService.registerProvider(SnappyRepository::class.java) { mockDb }
        daggerActionService.registerProvider(SessionHolder::class.java) { mockSessionHolder }
        daggerActionService.registerProvider(BucketInteractor::class.java) { bucketInteractor }
        daggerActionService.registerProvider(StaticPageProvider::class.java, { staticPageProvider })

        janet = Janet.Builder()
                .addService(daggerActionService)
                .addService(cachedService(httpStubWrapper))
                .build()

        bucketInteractor = BucketInteractor(janet)

        userSession = mock(UserSession::class.java)
        val mockUser = mock(User::class.java)

        `when`(mockUser.id).thenReturn(MOCK_USER_ID)
        `when`(userSession!!.user).thenReturn(mockUser)
        `when`(mockSessionHolder!!.get()).thenReturn(Optional.of(userSession))
        `when`(staticPageProvider!!.uploaderyUrl).thenReturn("http://test-uploadery")
    }

    protected abstract fun mockHttpService(): MockHttpActionService

    internal fun daggerActionService(): MockDaggerActionService {
        return daggerActionService
    }

    protected open fun storageSet(): Set<ActionStorage<*>> {
        val storageSet = HashSet<ActionStorage<*>>()
        storageSet.add(BucketListDiskStorage(mockMemoryStorage, mockDb))

        return storageSet
    }

    companion object {
        val MOCK_USER_ID = 1
    }
}
