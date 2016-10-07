package com.worldventures.dreamtrips.modules.infopages.service;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;

import java.util.ArrayList;
import java.util.List;

import icepick.Icepick;
import icepick.State;
import rx.Observable;
import rx.subjects.PublishSubject;

public class FeedbackAttachmentsManager {

   @State ArrayList<EntityStateHolder<FeedbackImageAttachment>> statefulImageAttachments = new ArrayList<>();

   private PublishSubject<EntityStateHolder<FeedbackImageAttachment>> statefulAttachmentsObservable =
         PublishSubject.create();

   public void restoreInstanceState(Bundle savedState) {
      Icepick.restoreInstanceState(this, savedState);
   }

   public void saveInstanceState(Bundle outState) {
      Icepick.saveInstanceState(this, outState);
   }

   public List<EntityStateHolder<FeedbackImageAttachment>> getAttachments() {
      return new ArrayList<>(statefulImageAttachments);
   }

   public int getFailedOrPendingAttachmentsCount() {
      return Queryable.from(statefulImageAttachments)
            .count(attachment -> attachment.state() == EntityStateHolder.State.PROGRESS
                  || attachment.state() == EntityStateHolder.State.FAIL);
   }

   public void update(EntityStateHolder<FeedbackImageAttachment> updatedHolder) {
      EntityStateHolder<FeedbackImageAttachment> oldHolder =
            Queryable.from(statefulImageAttachments).filter(holder
                  -> holder.entity().getId().equals(updatedHolder.entity().getId())).firstOrDefault();
      if (oldHolder != null) {
         statefulImageAttachments.remove(updatedHolder);
      }
      statefulImageAttachments.add(updatedHolder);
      statefulAttachmentsObservable.onNext(updatedHolder);
   }

   public void remove(EntityStateHolder<FeedbackImageAttachment> holder) {
      statefulImageAttachments.remove(holder);
      statefulAttachmentsObservable.onNext(holder);
   }

   public void removeAll() {
      for(EntityStateHolder holder : statefulImageAttachments) {
         statefulAttachmentsObservable.onNext(holder);
      }
      statefulImageAttachments.clear();
   }

   public Observable<EntityStateHolder<FeedbackImageAttachment>> getAttachmentsObservable() {
      return statefulAttachmentsObservable;
   }
}
