package com.messenger.delegate;

import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.PhotoDAO;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MarkMessageDelegate {
   private final PhotoDAO photoDAO;
   private final MessageDAO messageDAO;

   @Inject
   MarkMessageDelegate(PhotoDAO photoDAO, MessageDAO messageDAO) {
      this.photoDAO = photoDAO;
      this.messageDAO = messageDAO;
   }

   public void removeSendingMessagesMessage() {
      Observable.just(null).doOnNext(o -> {
         photoDAO.markSendingAsFailed();
         messageDAO.markSendingAsFailed();
      }).subscribeOn(Schedulers.io()).subscribe(o -> {
      }, e -> Timber.d(e, ""));
   }
}
