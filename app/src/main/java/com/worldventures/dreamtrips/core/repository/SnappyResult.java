package com.worldventures.dreamtrips.core.repository;

import com.snappydb.DB;
import com.snappydb.SnappydbException;

public interface SnappyResult<T> {
   T call(DB db) throws SnappydbException;
}