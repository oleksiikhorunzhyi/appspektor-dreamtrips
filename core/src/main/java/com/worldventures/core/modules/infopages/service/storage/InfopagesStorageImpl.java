package com.worldventures.core.modules.infopages.service.storage;

import android.content.Context;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.core.modules.infopages.model.Document;
import com.worldventures.core.modules.infopages.model.FeedbackType;
import com.worldventures.core.repository.BaseSnappyRepository;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;

import java.util.List;


public class InfopagesStorageImpl extends BaseSnappyRepository implements InfopagesStorage {

   String FEEDBACK_TYPES = "FEEDBACK_TYPES";
   String DOCUMENTS = "DOCUMENTS";

   private final DefaultSnappyOpenHelper defaultSnappyOpenHelper;

   public InfopagesStorageImpl(Context context, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      super(context, defaultSnappyOpenHelper.provideExecutorService());
      this.defaultSnappyOpenHelper = defaultSnappyOpenHelper;
   }

   @Override
   public List<FeedbackType> getFeedbackTypes() {
      return readList(FEEDBACK_TYPES, FeedbackType.class);
   }

   @Override
   public void setFeedbackTypes(List<FeedbackType> types) {
      clearAllForKeys(FEEDBACK_TYPES);
      putList(FEEDBACK_TYPES, types);
   }

   @Override
   public List<Document> getDocuments(String type) {
      return readList(DOCUMENTS + ":" + type, Document.class);
   }

   @Override
   public void setDocuments(String type, List<Document> documents) {
      putList(DOCUMENTS + ":" + type, documents);
   }

   @Nullable
   @Override
   protected DB openDbInstance(Context context) throws SnappydbException {
      return defaultSnappyOpenHelper.openDbInstance(context);
   }

   private void clearAllForKeys(String... keys) {
      Queryable.from(keys).forEachR(key -> act(db -> {
         String[] placesKeys = db.findKeys(key);
         for (String placeKey : placesKeys) {
            db.del(placeKey);
         }
      }));
   }
}
