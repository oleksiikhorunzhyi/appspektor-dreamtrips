package com.worldventures.dreamtrips.social.ui.background_uploading.storage;

import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;

import java.util.List;

public interface CompoundOperationRepository {

   List<PostCompoundOperationModel> readCompoundOperations();

   void saveCompoundOperations(List<PostCompoundOperationModel> compoundOperations);
}
