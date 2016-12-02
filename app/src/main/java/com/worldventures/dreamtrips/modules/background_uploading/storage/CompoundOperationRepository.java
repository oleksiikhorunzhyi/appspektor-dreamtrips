package com.worldventures.dreamtrips.modules.background_uploading.storage;

import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;

import java.util.List;

public interface CompoundOperationRepository {

   List<CompoundOperationModel> readCompoundOperations();

   void createCompoundOperation(CompoundOperationModel compoundOperationModel);

   void updateCompoundOperation(CompoundOperationModel compoundOperationModel);

   void deleteCompoundOperation(int id);
}
