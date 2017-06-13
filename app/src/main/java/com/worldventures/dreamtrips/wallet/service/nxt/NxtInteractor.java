package com.worldventures.dreamtrips.wallet.service.nxt;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class NxtInteractor {

   private final ActionPipe<TokenizeRecordCommand> tokenizeRecordPipe;
   private final ActionPipe<TokenizeMultipleRecordsCommand> tokenizeMultipleRecordsPipe;
   private final ActionPipe<DetokenizeRecordCommand> detokenizeRecordPipe;
   private final ActionPipe<DetokenizeMultipleRecordsCommand> detokenizeMultipleRecordsPipe;

   public NxtInteractor(SessionActionPipeCreator pipeCreator) {
      tokenizeRecordPipe = pipeCreator.createPipe(TokenizeRecordCommand.class, Schedulers.io());
      tokenizeMultipleRecordsPipe = pipeCreator.createPipe(TokenizeMultipleRecordsCommand.class, Schedulers.io());
      detokenizeRecordPipe = pipeCreator.createPipe(DetokenizeRecordCommand.class, Schedulers.io());
      detokenizeMultipleRecordsPipe = pipeCreator.createPipe(DetokenizeMultipleRecordsCommand.class, Schedulers.io());
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

   public ActionPipe<DetokenizeMultipleRecordsCommand> detokenizeMultipleRecordsPipe() {
      return detokenizeMultipleRecordsPipe;
   }
}