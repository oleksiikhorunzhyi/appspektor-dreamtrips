package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.service.api.GetUserTimelineHttpAction;

import java.util.Date;

import io.techery.janet.command.annotations.CommandAction;

public class GetUserTimelineCommand extends BaseGetFeedCommand<GetUserTimelineHttpAction> {

    private int userId;

    public GetUserTimelineCommand(int userId, Date before) {
        super(before);
        this.userId = userId;
    }

    @Override
    protected Class<GetUserTimelineHttpAction> provideHttpActionClass() {
        return GetUserTimelineHttpAction.class;
    }

    @Override
    protected GetUserTimelineHttpAction provideRequest() {
        return new GetUserTimelineHttpAction(userId, TIMELINE_LIMIT, before);
    }

    @Override
    public int getFallbackErrorMessage() {
        return R.string.error_fail_to_load_timeline;
    }

    @CommandAction
    public static class LoadNext extends GetUserTimelineCommand {
        public LoadNext(int userId, Date before) {
            super(userId, before);
        }
    }

    @CommandAction
    public static class Refresh extends GetUserTimelineCommand {
        public Refresh(int userId) {
            super(userId, null);
        }
    }
}
