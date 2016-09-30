package com.worldventures.dreamtrips.wallet.ui.common.navigation;

import flow.Flow.Direction;
import flow.History;
import flow.path.Path;

public interface Navigator {

   /**
    * Navigate on next screen
    *
    * @param path is screen descriptor
    */
   void go(Path path);

   /**
    * Navigate on next screen and clear history
    * Direction by default is FORWARD {@link #single(Path, Direction)}
    *
    * @param path is screen descriptor
    */
   void single(Path path);

   /**
    * Navigate on next screen and clear history
    *
    * @param path      is screen descriptor
    * @param direction is
    */
   void single(Path path, Direction direction);

   /**
    * Navigate on next screen and pop current screen from stack
    *
    * @param path is screen descriptor
    */
   void withoutLast(Path path);

   void setHistory(History history);

   void setHistory(History history, Direction direction);

   void goBack();
}
