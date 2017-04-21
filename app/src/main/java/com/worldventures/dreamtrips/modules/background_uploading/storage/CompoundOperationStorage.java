package com.worldventures.dreamtrips.modules.background_uploading.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.DeleteCompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.QueryCompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.UpdateCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.UpdateCompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.uploading.util.PostCompoundOperationModelComparator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CompoundOperationStorage implements MultipleActionStorage<List<CompoundOperationModel>> {

   private static final Comparator<CompoundOperationModel> CELLS_COMPARATOR = new PostCompoundOperationModelComparator();

   private CompoundOperationRepository compoundOperationRepository;

   public CompoundOperationStorage(CompoundOperationRepository compoundOperationRepository) {
      this.compoundOperationRepository = compoundOperationRepository;
   }

   private MemoryStorage<List<CompoundOperationModel>> memoryStorage = new MemoryStorage<>();

   @Override
   public List<Class<? extends CachedAction>> getActionClasses() {
      return Arrays.asList(
            UpdateCompoundOperationCommand.class,
            UpdateCompoundOperationsCommand.class,
            QueryCompoundOperationsCommand.class,
            DeleteCompoundOperationsCommand.class);
   }

   @Override
   public void save(@Nullable CacheBundle params, List<CompoundOperationModel> data) {
      memoryStorage.save(params, data);
      compoundOperationRepository.saveCompoundOperations(data);
   }

   @Override
   public List<CompoundOperationModel> get(@Nullable CacheBundle action) {
      List<CompoundOperationModel> models = getInternal(action);
      if (models != null) Collections.sort(models, CELLS_COMPARATOR);
      return models;
   }

   private List<CompoundOperationModel> getInternal(@Nullable CacheBundle action) {
      List<CompoundOperationModel> compoundOperationModels = memoryStorage.get(action);
      return compoundOperationModels != null ? compoundOperationModels : compoundOperationRepository.readCompoundOperations();
   }
}
