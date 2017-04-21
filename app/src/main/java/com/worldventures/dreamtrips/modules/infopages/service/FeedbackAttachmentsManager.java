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

   public void update(EntityStateHolder<FeedbackImageAttachment> newHolder) {
      boolean existingHolderRefreshed = false;
      for (int i = 0; i < statefulImageAttachments.size(); i++) {
         EntityStateHolder<FeedbackImageAttachment> existingHolder = statefulImageAttachments.get(i);
         if (existingHolder.entity().equals(newHolder.entity())) {
            statefulImageAttachments.set(i, newHolder);
            existingHolderRefreshed = true;
            break;
         }
      }
      if (!existingHolderRefreshed) statefulImageAttachments.add(newHolder);

      statefulAttachmentsObservable.onNext(newHolder);
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
