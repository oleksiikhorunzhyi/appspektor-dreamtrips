package com.worldventures.core.modules.infopages.service.storage;

import com.worldventures.core.modules.infopages.model.Document;
import com.worldventures.core.modules.infopages.model.FeedbackType;

import java.util.List;

public interface InfopagesStorage {

   List<FeedbackType> getFeedbackTypes();

   void setFeedbackTypes(List<FeedbackType> types);

   List<Document> getDocuments(String type);

   void setDocuments(String type, List<Document> documents);
}
