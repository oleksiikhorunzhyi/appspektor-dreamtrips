package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DeleteRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SecureMultipleRecordsCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SecureRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordOnNewDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordsCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.UpdateRecordCommand;

import java.util.concurrent.Executors;

import io.techery.janet.ActionPipe;
import rx.Scheduler;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public final class RecordInteractor {
   private final ActionPipe<RecordListCommand> cardsListPipe;
   private final ActionPipe<SecureRecordCommand> secureRecordPipe;
   private final ActionPipe<SecureMultipleRecordsCommand> secureMultipleRecordsPipe;
   private final ActionPipe<UpdateRecordCommand> updateRecordPipe;
   private final ActionPipe<SyncRecordsCommand> syncRecordsPipe;
   private final ActionPipe<AddRecordCommand> addRecordPipe;
   private final ActionPipe<DefaultRecordIdCommand> defaultRecordIdPipe;
   private final ActionPipe<SetDefaultCardOnDeviceCommand> setDefaultCardOnDeviceCommandPipe;
   private final ActionPipe<SetPaymentCardAction> setPaymentCardActionActionPipe;
   private final ActionPipe<DeleteRecordCommand> deleteRecordPipe;
   private final ActionPipe<CreateRecordCommand> recordIssuerInfoPipe;
   private final ActionPipe<SyncRecordOnNewDeviceCommand> syncRecordOnNewDevicePipe;
   private final ActionPipe<SyncRecordStatusCommand> syncRecordStatusPipe;

   public RecordInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this(sessionActionPipeCreator, RecordInteractor::singleThreadScheduler);
   }

   public RecordInteractor(SessionActionPipeCreator sessionActionPipeCreator, Func0<Scheduler> cacheSchedulerFactory) {
      cardsListPipe = sessionActionPipeCreator.createPipe(RecordListCommand.class, cacheSchedulerFactory.call());
      syncRecordsPipe = sessionActionPipeCreator.createPipe(SyncRecordsCommand.class, cacheSchedulerFactory.call());
      defaultRecordIdPipe = sessionActionPipeCreator.createPipe(DefaultRecordIdCommand.class, cacheSchedulerFactory.call());
      secureRecordPipe = sessionActionPipeCreator.createPipe(SecureRecordCommand.class, Schedulers.io());
      secureMultipleRecordsPipe = sessionActionPipeCreator.createPipe(SecureMultipleRecordsCommand.class, Schedulers.io());
      updateRecordPipe = sessionActionPipeCreator.createPipe(UpdateRecordCommand.class, Schedulers.io());
      addRecordPipe = sessionActionPipeCreator.createPipe(AddRecordCommand.class, Schedulers.io());
      setDefaultCardOnDeviceCommandPipe = sessionActionPipeCreator.createPipe(SetDefaultCardOnDeviceCommand.class, Schedulers
            .io());
      setPaymentCardActionActionPipe = sessionActionPipeCreator.createPipe(SetPaymentCardAction.class, Schedulers.io());
      deleteRecordPipe = sessionActionPipeCreator.createPipe(DeleteRecordCommand.class, Schedulers.io());
      recordIssuerInfoPipe = sessionActionPipeCreator.createPipe(CreateRecordCommand.class, Schedulers.io());
      syncRecordOnNewDevicePipe = sessionActionPipeCreator.createPipe(SyncRecordOnNewDeviceCommand.class, Schedulers.io());
      syncRecordStatusPipe = sessionActionPipeCreator.createPipe(SyncRecordStatusCommand.class, Schedulers.io());

   }

   private static Scheduler singleThreadScheduler() {
      return Schedulers.from(Executors.newSingleThreadExecutor());
   }

   public ActionPipe<RecordListCommand> cardsListPipe() {
      return cardsListPipe;
   }

   public ActionPipe<SecureRecordCommand> secureRecordPipe() {
      return secureRecordPipe;
   }

   public ActionPipe<SecureMultipleRecordsCommand> secureMultipleRecordsPipe() {
      return secureMultipleRecordsPipe;
   }

   public ActionPipe<UpdateRecordCommand> updateRecordPipe() {
      return updateRecordPipe;
   }

   public ActionPipe<SyncRecordsCommand> recordsSyncPipe() {
      return syncRecordsPipe;
   }

   public ActionPipe<DefaultRecordIdCommand> defaultRecordIdPipe() {
      return defaultRecordIdPipe;
   }

   public ActionPipe<DeleteRecordCommand> deleteRecordPipe() {
      return deleteRecordPipe;
   }

   public ActionPipe<AddRecordCommand> addRecordPipe() {
      return addRecordPipe;
   }

   public ActionPipe<SetDefaultCardOnDeviceCommand> setDefaultCardOnDeviceCommandPipe() {
      return setDefaultCardOnDeviceCommandPipe;
   }

   public ActionPipe<SetPaymentCardAction> setPaymentCardActionActionPipe() {
      return setPaymentCardActionActionPipe;
   }

   public ActionPipe<CreateRecordCommand> bankCardPipe() {
      return recordIssuerInfoPipe;
   }

   public ActionPipe<SyncRecordOnNewDeviceCommand> syncRecordOnNewDevicePipe() {
      return syncRecordOnNewDevicePipe;
   }

   public ActionPipe<SyncRecordStatusCommand> syncRecordStatusPipe() {
      return syncRecordStatusPipe;
   }
}
