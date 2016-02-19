package com.worldventures.dreamtrips.modules.profile.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

import java.util.List;

public class DeleteFriendFromGroupCommand extends Command<Void> {

    private String groupId;
    private List<String> userIds;

    public DeleteFriendFromGroupCommand(String groupId, List<String> userIds) {
        super(Void.class);
        this.groupId = groupId;
        this.userIds = userIds;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().deleteFromGroup(groupId, userIds);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_remove_user_from_circle;
    }
}
