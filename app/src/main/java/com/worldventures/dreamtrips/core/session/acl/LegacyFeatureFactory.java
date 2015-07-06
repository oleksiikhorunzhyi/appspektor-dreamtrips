package com.worldventures.dreamtrips.core.session.acl;

import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.List;

public class LegacyFeatureFactory {

    private final User user;

    public LegacyFeatureFactory(User user) {
        this.user = user;
    }

    public List<Feature> create() {
        List<Feature> features = new ArrayList<>();
        if (user.isMember()) features.add(new Feature(Feature.TRIPS));
        if (user.isRep()) features.add(new Feature(Feature.REP_TOOLS));
        return features;
    }
}
