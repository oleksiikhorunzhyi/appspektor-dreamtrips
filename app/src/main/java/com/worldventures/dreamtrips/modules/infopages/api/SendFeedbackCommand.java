package com.worldventures.dreamtrips.modules.infopages.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

public class SendFeedbackCommand extends Command<Void> {

    int type;
    String text;

    public SendFeedbackCommand(int type, String text) {
        super(Void.class);
        this.type = type;
        this.text = text;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().sendFeedback(new FeedbackBody(type, text));
    }

    public static class FeedbackBody {

        int reasonId;
        String text;

        public FeedbackBody(int typeId, String text) {
            this.reasonId = typeId;
            this.text = text;
        }
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_send_feedback;
    }
}
