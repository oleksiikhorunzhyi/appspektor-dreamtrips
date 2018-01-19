package com.worldventures.wallet.service.lostcard

import io.techery.janet.ActionState
import io.techery.janet.ReadActionPipe
import rx.Observable
import rx.functions.Func1
import rx.lang.kotlin.PublishSubject

internal class MockReadActionPipe<A> : ReadActionPipe<A> {

   private val subject = PublishSubject<A>()

   override fun clearReplays() {
      throw NotImplementedError()
   }

   override fun observeSuccessWithReplay(): Observable<A> {
      throw NotImplementedError()
   }

   override fun observeWithReplay(): Observable<ActionState<A>> {
      throw NotImplementedError()
   }

   override fun observe(): Observable<ActionState<A>> {
      throw NotImplementedError()
   }

   override fun filter(predicate: Func1<in A, Boolean>?): ReadActionPipe<A> {
      throw NotImplementedError()
   }

   override fun observeSuccess(): Observable<A> = subject.asObservable()

   fun pushSuccessAction(action: A) {
      subject.onNext(action)
   }
}