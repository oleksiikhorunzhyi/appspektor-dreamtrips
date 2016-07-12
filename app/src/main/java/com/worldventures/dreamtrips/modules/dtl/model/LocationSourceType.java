package com.worldventures.dreamtrips.modules.dtl.model;

/**
 * Defines set of possible ways for user to select location for further merchants loading.<br />
 */
public enum LocationSourceType {

    /**
     * Location has never been stored
     */
    UNDEFINED,
    /**
     * User explicitly selected to load merchants from GPS location (or when selected automatically <br />
     * as default logic for cold start)
     */
    NEAR_ME,
    /**
     * User loaded merchants with map-selected location
     */
    FROM_MAP,
    /**
     * User selected particular location among those available from API
     */
    EXTERNAL,
}
