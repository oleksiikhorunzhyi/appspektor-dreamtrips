package com.worldventures.dreamtrips.core.flow.path;

import com.worldventures.dreamtrips.core.flow.container.TabletMasterDetailRoot;

import flow.path.Path;

/**
 * Identifies screens in a master / detail relationship. Both master and detail screens
 * extend this class.
 * <p>
 * Not a lot of thought has been put into making a decent master / detail modeling here. Rather
 * this is an excuse to show off using Flow to build a responsive layout. See {@link
 * TabletMasterDetailRoot}.
 */
public abstract class MasterDetailPath extends Path {
  /**
   * Returns the screen that shows the master list for this type of screen.
   * If this screen is the master, returns self.
   */
  public abstract MasterDetailPath getMaster();

  public final boolean isMaster() {
    return equals(getMaster());
  }
}