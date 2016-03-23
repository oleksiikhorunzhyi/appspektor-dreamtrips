package com.worldventures.dreamtrips.modules.infopages.presenter;

import android.os.Build;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.api.GetFeedbackReasonsQuery;
import com.worldventures.dreamtrips.modules.infopages.api.SendFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;

import java.util.List;

import javax.inject.Inject;

public class SendFeedbackPresenter extends Presenter<SendFeedbackPresenter.View> {

    @Inject
    SnappyRepository db;

    @Inject
    AppVersionNameBuilder appVersionNameBuilder;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        doRequest(new GetFeedbackReasonsQuery(), feedbackTypes -> {
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
        String osVersion = String.format("android-%d", Build.VERSION.SDK_INT);
        String buildName = appVersionNameBuilder.getSemanticVersionName();
        String deviceModel = String.format("%s:%s", Build.MANUFACTURER, Build.MODEL);
        SendFeedbackCommand.Metadata metadata = new SendFeedbackCommand.Metadata(deviceModel, buildName, osVersion);
        TrackingHelper.sendFeedback(type);

        doRequest(new SendFeedbackCommand(type, text, metadata), o -> view.feedbackSent());
    }

    public interface View extends Presenter.View {
        void setFeedbackTypes(List<FeedbackType> feedbackTypes);

        void feedbackSent();

        void showProgressDialog();

        void hideProgressBar();
    }
}
