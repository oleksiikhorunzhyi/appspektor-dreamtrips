package com.worldventures.core.modules.auth.api.command;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.core.utils.CrashlyticsTracker;
import com.worldventures.dreamtrips.api.session.LogoutHttpAction;
import com.worldventures.janet.injection.InjectableAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class LogoutCommand extends Command<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject SessionHolder appSessionHolder;
   @Inject SessionActionPipeCreator sessionActionPipeCreator;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject Set<LogoutAction> logoutActions;
   @Inject @Named(LogoutAction.PRIORITY_HIGH) Set<LogoutAction> highPriorityLogoutActions;

   private boolean userDataCleared;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(clearSessionDependants(), args -> null)
            .flatMap(o -> deleteSession().doOnNext(o1 -> {
               userDataCleared = true;
               callback.onProgress(0);
            }))
            .flatMap(o -> clearUserData())
            .doOnError(throwable -> {
               Timber.w((Throwable) throwable, "Could not log out");
               CrashlyticsTracker.trackError((Throwable) throwable);
            })
            .subscribe(o -> logoutComplete(callback));
   }

   private void logoutComplete(CommandCallback<Void> callback) {
      analyticsInteractor.analyticsActionPipe()
            .send(new com.worldventures.core.modules.auth.service.analytics.LogoutAction());
      callback.onSuccess(null);
   }

   private Iterable<Observable<Void>> clearSessionDependants() {
      return Arrays.asList(executeLogoutActions());
   }

   private Observable executeLogoutActions() {
      ArrayList<LogoutAction> logoutActionQueue = new ArrayList<>(highPriorityLogoutActions);
      logoutActionQueue.addAll(logoutActions);
      return Observable.create(subscriber -> {
         for (LogoutAction action : logoutActionQueue) {
            try {
               action.call();
            } catch (Exception e) {
               Timber.e(e, "%s was failed", action);
            }
         }
         subscriber.onNext(null);
         subscriber.onCompleted();
      });
   }

   private Observable deleteSession() {
      return janet.createPipe(LogoutHttpAction.class)
            .createObservableResult(new LogoutHttpAction())
            .onErrorResumeNext(Observable.just(null))
            .doOnNext(action -> clearSessionHolder());
   }

   private void clearSessionHolder() {
      appSessionHolder.destroy();
      sessionActionPipeCreator.clearReplays();
   }

   private Observable clearUserData() {
      return Observable.create(subscriber -> {
         clearFrescoCaches();

         subscriber.onNext(null);
         subscriber.onCompleted();
      });
   }

   public boolean isUserDataCleared() {
      return userDataCleared;
   }

   private void clearFrescoCaches() {
      ImagePipeline imagePipeline = Fresco.getImagePipeline();
      imagePipeline.clearCaches();
   }
}
