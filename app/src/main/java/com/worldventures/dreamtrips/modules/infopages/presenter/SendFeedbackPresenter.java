package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.api.GetFeedbackTypesQuery;
import com.worldventures.dreamtrips.modules.infopages.api.SendFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;

import java.util.List;

import javax.inject.Inject;

public class SendFeedbackPresenter extends Presenter<SendFeedbackPresenter.View> {

    @Inject
    SnappyRepository db;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        doRequest(new GetFeedbackTypesQuery(), feedbackTypes -> {
            db.setFeedbackTypes(feedbackTypes);
            view.setFeedbackTypes(feedbackTypes);
            view.hideProgressBar();
        }, spiceException -> {
            SendFeedbackPresenter.this.handleError(spiceException);
            view.setFeedbackTypes(db.getFeedbackTypes());
            view.hideProgressBar();
        });
        view.showProgressDialog();
    }

    public void sendFeedback(int type, String text) {
        TrackingHelper.sendFeedback(type);
        doRequest(new SendFeedbackCommand(type, text), o -> view.feedbackSent());
    }

    public interface View extends Presenter.View {
        void setFeedbackTypes(List<FeedbackType> feedbackTypes);

        void feedbackSent();

        void showProgressDialog();

        void hideProgressBar();
    }
}
