package com.messenger.flow.path;

import flow.path.Path;

public interface PathView<P extends Path> {

    void setPath(Path path);

    P getPath();
}
