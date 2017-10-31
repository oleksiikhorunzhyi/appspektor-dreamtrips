package com.worldventures.wallet.service.lostcard.command

import com.worldventures.dreamtrips.api.smart_card.location.CreateSmartCardLocationHttpAction
import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardLocationBody
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationBody
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.command.ActiveSmartCardCommand
import com.worldventures.wallet.service.lostcard.LostCardRepository
import com.worldventures.wallet.util.WalletLocationsUtil
import io.techery.janet.CancelException
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import rx.Observable
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import java.util.Calendar
import java.util.Collections
import javax.inject.Inject
import kotlin.Comparator

@CommandAction
class PostLocationCommand : Command<Void>(), InjectableAction {

   private val commandPublishSubject: PublishSubject<Void> = PublishSubject.create()

   @Inject lateinit var janet: Janet
   @Inject lateinit var locationRepository: LostCardRepository
   @Inject lateinit var smartCardInteractor: SmartCardInteractor
   @Inject lateinit var mapperyContext: MapperyContext

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      val savedLocations = Collections.unmodifiableList(locationRepository.walletLocations)
      val walletLocation = WalletLocationsUtil.getLatestLocation(savedLocations)
      if (walletLocation == null || walletLocation.postedAt != null) {
         callback.onSuccess(null)
         return
      }
      Observable.merge(postLocations(savedLocations), commandPublishSubject)
            .flatMap { wipeRedundantLocations(savedLocations) }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun postLocations(locations: List<WalletLocation>): Observable<Void> {
      return observeActiveSmartCard()
            .flatMap { smartCard -> observeLocationsPost(locations, smartCard) }
            .map { null as Void? }
   }

   private fun observeActiveSmartCard(): Observable<SmartCard> {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(ActiveSmartCardCommand())
            .map({ it.result })
   }

   private fun observeLocationsPost(locations: List<WalletLocation>, smartCard: SmartCard): Observable<CreateSmartCardLocationHttpAction> {
      return janet.createPipe(CreateSmartCardLocationHttpAction::class.java, Schedulers.io())
            .createObservableResult(CreateSmartCardLocationHttpAction(java.lang.Long.parseLong(smartCard.smartCardId()),
                  prepareRequestBody(locations)))
   }

   private fun wipeRedundantLocations(locations: List<WalletLocation>): Observable<Void> {
      val lastLocation = locations.sortedWith(Comparator({location1, location2 -> location1.createdAt.compareTo(location2.createdAt)})).last()
      val postedLocation = lastLocation.copy(postedAt = Calendar.getInstance().time)
      locationRepository.saveWalletLocations(listOf(postedLocation))
      return Observable.just(null)
   }

   private fun prepareRequestBody(locations: List<WalletLocation>): SmartCardLocationBody {
      return ImmutableSmartCardLocationBody.builder()
            .locations(mapperyContext.convert(locations, SmartCardLocation::class.java))
            .build()
   }

   override fun cancel() {
      commandPublishSubject.onError(CancelException())
   }
}
