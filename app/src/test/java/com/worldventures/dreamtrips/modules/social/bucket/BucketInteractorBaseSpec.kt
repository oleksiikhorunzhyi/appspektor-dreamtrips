package com.worldventures.dreamtrips.modules.social.bucket

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.techery.spares.session.SessionHolder
import com.techery.spares.storage.complex_objects.Optional
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.core.session.UserSession
import com.worldventures.dreamtrips.core.test.BaseSpec
import com.worldventures.dreamtrips.core.test.MockDaggerActionService
import com.worldventures.dreamtrips.core.test.StubServiceWrapper
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage
import com.worldventures.dreamtrips.modules.common.model.User
import io.techery.janet.Janet
import org.jetbrains.spek.api.DescribeBody
import java.util.*

abstract class BucketInteractorBaseSpec(speckBody: DescribeBody.() -> Unit) : BaseSpec(speckBody) {
    companion object BaseCompanion {
        val MOCK_USER_ID = 1

        val mockSessionHolder: SessionHolder<UserSession> = mock()

        lateinit var mockMemoryStorage: MemoryStorage<List<BucketItem>>

        lateinit var mockDb: SnappyRepository

        lateinit var janet: Janet

        lateinit var bucketInteractor: BucketInteractor

        val userSession: UserSession = mock()

        lateinit var httpStubWrapper: StubServiceWrapper

        lateinit var daggerCommandActionService: MockDaggerActionService

        fun setup() {
            janet = Janet.Builder()
                    .addService(daggerCommandActionService)
                    .addService(httpStubWrapper.wrapCache()).build()

            //TODO: migrate to literal function to build DI
            daggerCommandActionService.registerProvider(Janet::class.java) { janet }
            daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
            daggerCommandActionService.registerProvider(SessionHolder::class.java) { mockSessionHolder }
            daggerCommandActionService.registerProvider(BucketInteractor::class.java) { bucketInteractor }

            bucketInteractor = BucketInteractor(janet)

            val mockUser = mock<User>()

            whenever(mockUser.id).thenReturn(MOCK_USER_ID)
            whenever(userSession.user).thenReturn(mockUser)
            whenever(mockSessionHolder.get()).thenReturn(Optional.of(userSession))
        }

        fun CacheResultWrapper.bindStorageSet(storageSet: Set<ActionStorage<*>>): CacheResultWrapper {
            for (storage in storageSet) {
                bindStorage(storage.actionClass, storage)
            }

            return this
        }

        fun storageSet(): Set<ActionStorage<*>> {
            val storageSet = HashSet<ActionStorage<*>>()
            storageSet += BucketListDiskStorage(mockMemoryStorage, mockDb, mockSessionHolder)

            return storageSet
        }
    }
}