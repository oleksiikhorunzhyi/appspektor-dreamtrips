package com.worldventures.wallet.service.lostcard

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType
import com.worldventures.wallet.service.beacon.BeaconClient
import com.worldventures.wallet.service.beacon.BeaconEvent
import com.worldventures.wallet.service.beacon.StubWalletBeaconLogger
import com.worldventures.wallet.service.impl.MockWalletNetworkService
import com.worldventures.wallet.ui.common.BaseTest
import io.techery.janet.smartcard.action.support.ConnectAction
import io.techery.janet.smartcard.action.support.DisconnectAction
import io.techery.janet.smartcard.model.ImmutableConnectionParams
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.lang.kotlin.PublishSubject

class LostCardManagerTest : BaseTest() {

   private val connectActionPipe = MockReadActionPipe<ConnectAction>()
   private val disconnectedActionPipe = MockReadActionPipe<DisconnectAction>()
   private val beaconEventSubject = PublishSubject<BeaconEvent>()

   private lateinit var lostCardManager: LostCardManager
   private lateinit var lostCardEventReceiver: LostCardEventReceiver
   private lateinit var locationSyncManager: LocationSyncManager
   private lateinit var mockNetworkService: MockWalletNetworkService
   private lateinit var beaconClient: BeaconClient

   @Before
   fun setupLostCardManager() {
      lostCardEventReceiver = mock()
      locationSyncManager = mock()
      mockNetworkService = MockWalletNetworkService()
      beaconClient = mock()
      whenever(beaconClient.observeEvents()).thenReturn(beaconEventSubject.asObservable())
      lostCardManager = LostCardManagerImpl(
            { Observable.empty() },
            connectActionPipe, disconnectedActionPipe, lostCardEventReceiver,
            locationSyncManager, mockNetworkService,
            beaconClient, StubWalletBeaconLogger()
      )
   }

   @Test
   fun testSyncManagerInitializationWithoutInternet() {
      mockNetworkService.available = false
      lostCardManager.connect("")

      verify(locationSyncManager, times(1)).cancelSync()
      verify(locationSyncManager, never()).scheduleSync()
   }

   @Test
   fun testSyncManagerInitializationWithInternet() {
      lostCardManager.connect("")

      verify(locationSyncManager, times(1)).scheduleSync()
      verify(locationSyncManager, never()).cancelSync()
   }

   @Test
   fun testSyncManagerToggling() {
      lostCardManager.connect("")
      reset(locationSyncManager)

      mockNetworkService.pushNewConnectedState(false)
      verify(locationSyncManager, times(1)).cancelSync()
      verify(locationSyncManager, never()).scheduleSync()
      reset(locationSyncManager)

      mockNetworkService.pushNewConnectedState(true)
      verify(locationSyncManager, times(1)).scheduleSync()
      verify(locationSyncManager, never()).cancelSync()
   }

   /////////////////////////////////////////////////////////////////////

   @Test
   fun testSmartCardConnectionEvents() {
      lostCardManager.connect("")

      verify(lostCardEventReceiver, never()).receiveEvent(any())

      connectActionPipe.pushSuccessAction(ConnectAction(ImmutableConnectionParams.of(0)))
      verify(lostCardEventReceiver, times(1)).receiveEvent(WalletLocationType.CONNECT)
      verify(lostCardEventReceiver, never()).receiveEvent(WalletLocationType.DISCONNECT)
      reset(lostCardEventReceiver)

      disconnectedActionPipe.pushSuccessAction(DisconnectAction())
      verify(lostCardEventReceiver, times(1)).receiveEvent(WalletLocationType.DISCONNECT)
      verify(lostCardEventReceiver, never()).receiveEvent(WalletLocationType.CONNECT)
   }

   @Test
   fun testBeaconEvents() {
      lostCardManager.connect("")

      beaconEventSubject.onNext(BeaconEvent("", true))
      verify(lostCardEventReceiver, times(1)).receiveEvent(WalletLocationType.CONNECT)
      verify(lostCardEventReceiver, never()).receiveEvent(WalletLocationType.DISCONNECT)

      beaconEventSubject.onNext(BeaconEvent("", false))
      verify(lostCardEventReceiver, times(1)).receiveEvent(WalletLocationType.DISCONNECT)
      verify(lostCardEventReceiver, times(1)).receiveEvent(WalletLocationType.CONNECT)

      beaconEventSubject.onNext(BeaconEvent("", false))
      beaconEventSubject.onNext(BeaconEvent("", true))
      beaconEventSubject.onNext(BeaconEvent("", true))
      verify(lostCardEventReceiver, times(2)).receiveEvent(WalletLocationType.DISCONNECT)
      verify(lostCardEventReceiver, times(3)).receiveEvent(WalletLocationType.CONNECT)
   }
}
