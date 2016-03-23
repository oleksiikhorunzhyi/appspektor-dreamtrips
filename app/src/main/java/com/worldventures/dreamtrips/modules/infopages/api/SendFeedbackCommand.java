package com.worldventures.dreamtrips.modules.infopages.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

public class SendFeedbackCommand extends Command<Void> {

    int type;
    String text;
    private Metadata metadata;

    public SendFeedbackCommand(int type, String text, Metadata metadata) {
        super(Void.class);
        this.type = type;
        this.text = text;
        this.metadata = metadata;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().sendFeedback(new FeedbackBody(type, text, metadata));
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_send_feedback;
    }

    public static class FeedbackBody {
        int reasonId;
        String text;
        Metadata metadata;

        public FeedbackBody(int reasonId, String text, Metadata metadata) {
            this.reasonId = reasonId;
            this.text = text;
            this.metadata = metadata;
        }
    }

    public static class Metadata {
        String deviceModel;
        String appVersion;
        String osVersion;

        public Metadata(String deviceModel, String appVersion, String osVersion) {
            this.deviceModel = deviceModel;
            this.appVersion = appVersion;
            this.osVersion = osVersion;
        }
    }

}
