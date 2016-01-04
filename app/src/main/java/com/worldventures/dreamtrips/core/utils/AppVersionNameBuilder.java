package com.worldventures.dreamtrips.core.utils;

import static com.worldventures.dreamtrips.BuildConfig.BUILD_TYPE;
import static com.worldventures.dreamtrips.BuildConfig.FLAVOR;
import static com.worldventures.dreamtrips.BuildConfig.versionBuild;
import static com.worldventures.dreamtrips.BuildConfig.versionMajor;
import static com.worldventures.dreamtrips.BuildConfig.versionMinor;
import static com.worldventures.dreamtrips.BuildConfig.versionPatch;

/**
 * For generate build version name for server side.
 */
public class AppVersionNameBuilder {

    public String versionName() {
        String name = "";
        if (isCurrentFlavor("dev")) {
            name = "dev";
        } else {
            boolean release = isCurrentBuildType("release");
            boolean debug = isCurrentBuildType("debug");
            if (isCurrentFlavor("stage")) {
                if (debug) name = "alpha";
                else if (release) name = "beta";

            } else if (isCurrentFlavor("prod")) {
                if (release) name = "prod";
            }
        }
        return generateName(name);
    }

    private boolean isCurrentFlavor(String flavor) {
        return FLAVOR.equals(flavor);
    }

    private boolean isCurrentBuildType(String flavor) {
        return BUILD_TYPE.equals(flavor);
    }

    private String generateName(String name) {
        return String.format("%s.%s.%s-%s-%s", versionMajor, versionMinor, versionPatch, versionBuild, name);
    }
}
