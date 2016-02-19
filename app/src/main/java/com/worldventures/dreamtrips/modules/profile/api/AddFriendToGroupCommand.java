package com.worldventures.dreamtrips.modules.profile.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

import java.util.List;

public class AddFriendToGroupCommand extends Command<Void> {

    private String groupId;
    private List<String> userIds;

    public AddFriendToGroupCommand(String groupId, List<String> userIds) {
        super(Void.class);
        this.groupId = groupId;
        this.userIds = userIds;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().addToGroup(groupId, userIds);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_add_user_to_circle;
    }
}
