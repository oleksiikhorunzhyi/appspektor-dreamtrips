package com.worldventures.dreamtrips.core.janet;

import io.techery.janet.Command;
import rx.Subscriber;

public final class CommandActionBaseHelper extends Command {

   private CommandActionBaseHelper() {}

   @Override
   protected void run(CommandCallback callback) {
      //do nothing
   }

   public final static class ActionCommandSubscriber<A> extends Subscriber<A> {

      private final CommandCallback<A> callback;
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
         if (!executed) {
            throw new IllegalStateException("Callback has been ignored before onComplete");
         }
      }

      private void tryMarkExecuted() throws IllegalStateException {
         if (executed) {
            throw new IllegalStateException("Callback has been already called once");
         }
         executed = true;
      }
   }
}
