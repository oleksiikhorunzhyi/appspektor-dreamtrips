package com.worldventures.dreamtrips.modules.profile.api;

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
}
