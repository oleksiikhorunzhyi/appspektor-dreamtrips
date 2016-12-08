package com.worldventures.dreamtrips.modules.background_uploading.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsCommand;

import java.util.List;

public class CompoundOperationStorage implements ActionStorage<List<CompoundOperationModel>> {

   private CompoundOperationRepository compoundOperationRepository;

   public CompoundOperationStorage(CompoundOperationRepository compoundOperationRepository) {
      this.compoundOperationRepository = compoundOperationRepository;
   }

   private MemoryStorage<List<CompoundOperationModel>> memoryStorage = new MemoryStorage<>();

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return CompoundOperationsCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<CompoundOperationModel> data) {
      memoryStorage.save(params, data);
      compoundOperationRepository.saveCompoundOperations(data);
   }

   @Override
   public List<CompoundOperationModel> get(@Nullable CacheBundle action) {
      List<CompoundOperationModel> compoundOperationModels = memoryStorage.get(action);
      return compoundOperationModels != null ? compoundOperationModels : compoundOperationRepository.readCompoundOperations();
   }
}
