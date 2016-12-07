package com.worldventures.dreamtrips.core.repository;

import com.snappydb.DB;
import com.snappydb.SnappydbException;

public interface SnappyAction {
   void call(DB db) throws SnappydbException;
}