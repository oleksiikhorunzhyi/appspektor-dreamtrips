package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.SendFeedbackCommand;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class SendFeedbackPresenter extends Presenter<SendFeedbackPresenter.View> {

   @Inject FeedbackInteractor feedbackInteractor;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      feedbackInteractor.getFeeedbackPipe()
            .createObservable(new GetFeedbackCommand())
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetFeedbackCommand>()
                  .onStart(action -> {
                     view.setFeedbackTypes(action.items());
                     view.showProgressDialog();
                  })
                  .onSuccess(action -> {
                     view.setFeedbackTypes(action.items());
                     view.hideProgressBar();
                  })
                  .onFail((action, e) -> {
                     view.informUser(action.getErrorMessage());
                     view.setFeedbackTypes(action.items());
                     view.hideProgressBar();
                  }));
   }

   public void sendFeedback(int type, String text) {
      TrackingHelper.sendFeedback(type);
      //
      feedbackInteractor.sendFeedbackPipe()
            .createObservable(new SendFeedbackCommand(type, text))
            .subscribe(new ActionStateSubscriber<SendFeedbackCommand>()
                  .onSuccess(action -> view.feedbackSent())
                  .onFail((action, e) -> view.informUser(action.getErrorMessage())));
   }

   public interface View extends Presenter.View {
      void setFeedbackTypes(List<FeedbackType> feedbackTypes);

      void feedbackSent();

      void showProgressDialog();

      void hideProgressBar();
   }
}
