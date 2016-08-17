package com.worldventures.dreamtrips.core.flow.path;

import flow.path.Path;

public interface PathView<P extends Path> {

   void setPath(P path);

   P getPath();
}
