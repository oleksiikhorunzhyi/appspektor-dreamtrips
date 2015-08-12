package com.worldventures.dreamtrips.modules.profile.api;

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
}
