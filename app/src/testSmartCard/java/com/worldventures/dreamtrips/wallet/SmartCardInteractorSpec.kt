package com.worldventures.dreamtrips.wallet

import android.content.Context
import android.test.mock.MockContext
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.AssertUtil.assertSingleProgressAction
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableProvision
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultBankCardStorage
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.SmartCardActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.janet.smartcard.action.lock.GetLockDeviceStatusAction
import io.techery.janet.smartcard.action.records.SetRecordAsDefaultAction
import io.techery.janet.smartcard.action.support.ConnectAction
import io.techery.janet.smartcard.mock.client.MockSmartCardClient
import rx.observers.TestSubscriber

class SmartCardInteractorSpec : BaseSpec({

    describe("SmartCard SDK actions") {

        beforeEach {
            mockDb = createMockDb()
            defaultBankCardStorage = createDefaultBankCardStorage()
            janet = createJanet()
            smartCardInteractor = createInteractor(janet)

            janet.connectToSmartCardSdk()
        }

        context("Default card id is fetching") {

            it("should fetch default card id from storage") {
                val testSmartCardId: String = "4"

                whenever(mockDb.readWalletDefaultCardId()).thenReturn(testSmartCardId)
                assertSingleProgressAction(loadDefaultCardId(), { it -> testSmartCardId == it.cachedResult })
            }

            it("should get default card id from sdk") {
                val testSmartCardId: String = "4"

                janet.createPipe(SetRecordAsDefaultAction::class.java)
                        .createObservableResult(SetRecordAsDefaultAction(testSmartCardId.toInt()))
                        .subscribe()
                assertActionSuccess(loadDefaultCardId(), { it -> testSmartCardId == it.result })
            }
        }

        context("Lock state is fetching") {

            fun getLockState(): TestSubscriber<ActionState<GetLockDeviceStatusAction>> {
                val testSubscriber = TestSubscriber<ActionState<GetLockDeviceStatusAction>>()
                janet.createPipe(GetLockDeviceStatusAction::class.java).createObservable(GetLockDeviceStatusAction())
                        .subscribe(testSubscriber)
                return testSubscriber
            }

            it("should get lock state") {
                smartCardInteractor.lockPipe().createObservableResult(SetLockStateCommand(true)).subscribe()
                assertActionSuccess(getLockState(), { it -> true == it.locked })
            }
        }
    }
}) {
    private companion object {

        lateinit var mockDb: SnappyRepository
        lateinit var defaultBankCardStorage: DefaultBankCardStorage
        lateinit var janet: Janet
        lateinit var smartCardInteractor: SmartCardInteractor

        fun createInteractor(janet: Janet) = SmartCardInteractor(janet)

        fun createJanet(): Janet {
            val daggerCommandActionService = CommandActionService()
                    .wrapCache()
                    .bindStorageSet(setOf(defaultBankCardStorage))
                    .wrapDagger()

            janet = Janet.Builder()
                    .addService(daggerCommandActionService)
                    .addService(mockHttpService().wrapStub().wrapCache())
                    .addService(SmartCardActionService.createDefault(MockSmartCardClient()))
                    .build()

            daggerCommandActionService.registerProvider(Janet::class.java) { janet }
            daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
            daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
            daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })

            return janet
        }

        fun createMockDb(): SnappyRepository = spy()

        fun createDefaultBankCardStorage(): DefaultBankCardStorage = DefaultBankCardStorage(mockDb)

        fun loadDefaultCardId(): TestSubscriber<ActionState<FetchDefaultCardCommand>> {
            val testSubscriber = TestSubscriber<ActionState<FetchDefaultCardCommand>>()

            smartCardInteractor.fetchDefaultCardCommandActionPipe()
                    .createObservable(FetchDefaultCardCommand())
                    .subscribe(testSubscriber)
            return testSubscriber
        }

        fun mockHttpService(): MockHttpActionService {
            return MockHttpActionService.Builder()
                    .bind(MockHttpActionService.Response(200).body(
                            ImmutableProvision.builder().memberId("1").userSecret("test").build())) {
                        it.url.endsWith("create_card")
                    }
                    .build()
        }

        fun CacheResultWrapper.bindStorageSet(storageSet: Set<ActionStorage<*>>): CacheResultWrapper {
            storageSet.forEach {
                bindStorage(it.actionClass, it)
            }

            return this
        }

        fun Janet.connectToSmartCardSdk() {
            this.createPipe(ConnectAction::class.java).createObservableResult(ConnectAction("any", "any")).subscribe()
        }
    }
}


