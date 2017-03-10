package com.worldventures.dreamtrips.wallet.service.nxt;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class NxtInteractor {

   private final ActionPipe<TokenizeRecordCommand> tokenizeRecordPipe;
   private final ActionPipe<TokenizeMultipleRecordsCommand> tokenizeMultipleRecordsPipe;
   private final ActionPipe<DetokenizeRecordCommand> detokenizeRecordPipe;

   public NxtInteractor(Janet janet) {
      tokenizeRecordPipe = janet.createPipe(TokenizeRecordCommand.class, Schedulers.io());
      tokenizeMultipleRecordsPipe = janet.createPipe(TokenizeMultipleRecordsCommand.class, Schedulers.io());
      detokenizeRecordPipe = janet.createPipe(DetokenizeRecordCommand.class, Schedulers.io());
   }

   public ActionPipe<TokenizeRecordCommand> tokenizeRecordPipe() {
      return tokenizeRecordPipe;
   }

   public ActionPipe<TokenizeMultipleRecordsCommand> tokenizeMultipleRecordsPipe() {
      return tokenizeMultipleRecordsPipe;
   }

   public ActionPipe<DetokenizeRecordCommand> detokenizeRecordPipe() {
      return detokenizeRecordPipe;
   }

}