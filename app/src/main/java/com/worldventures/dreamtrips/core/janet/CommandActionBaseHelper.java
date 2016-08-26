package com.worldventures.dreamtrips.core.janet;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Subscriber;

public final class CommandActionBaseHelper extends Command {

   private CommandActionBaseHelper() {}

   @Override
   protected void run(CommandCallback callback) {
   }

   public static class ActionStateCommandSubscriber<A> extends ActionStateSubscriber<A> {

      private ActionStateCommandSubscriber(CommandCallback<A> callback) {
         this
               .onProgress((a, progress) -> callback.onProgress(progress))
               .onSuccess(a -> callback.onSuccess(a))
               .onFail((a, e) -> callback.onFail(e));
      }

      public static <U> ActionStateCommandSubscriber<U> wrap(CommandCallback<U> callback) {
         return new ActionStateCommandSubscriber<>(callback);
      }
   }

   public static class ActionCommandSubscriber<A> extends Subscriber<A> {

      private CommandCallback<A> callback;
      private volatile boolean executed;

      private ActionCommandSubscriber(CommandCallback<A> callback) {
         this.callback = callback;
      }

      public static <U> ActionCommandSubscriber<U> wrap(CommandCallback<U> callback) {
         return new ActionCommandSubscriber<>(callback);
      }

      @Override
      public void onNext(A action) {
         tryMarkExecuted();
         callback.onSuccess(action);
      }

      @Override
      public void onError(Throwable e) {
         tryMarkExecuted();
         callback.onFail(e);
      }

      @Override
      public void onCompleted() {
         if (!executed) throw new IllegalStateException("Callback has been ignored before onComplete");
      }

      private void tryMarkExecuted() throws IllegalStateException {
         if (executed) throw new IllegalStateException("Callback has been already called once");
         executed = true;
      }
   }
}