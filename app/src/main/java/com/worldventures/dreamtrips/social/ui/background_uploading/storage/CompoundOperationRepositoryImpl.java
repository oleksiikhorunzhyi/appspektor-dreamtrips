package com.worldventures.dreamtrips.social.ui.background_uploading.storage;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CompoundOperationRepositoryImpl implements CompoundOperationRepository {

   private String COMPOUND_OBJECT_POSTS = "COMPOUND_OBJECT_POSTS";

   private SnappyRepository snappyRepository;

   public CompoundOperationRepositoryImpl(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public List<PostCompoundOperationModel> readCompoundOperations() {
      List<PostCompoundOperationModel> compoundOperationModels = new ArrayList<>();
      compoundOperationModels.addAll(snappyRepository.readList(COMPOUND_OBJECT_POSTS, PostCompoundOperationModel.class));
      Timber.d("Reading compound operations, %s", compoundOperationModels.toString());
      return compoundOperationModels;
   }

   @Override
   public void saveCompoundOperations(List<PostCompoundOperationModel> compoundOperations) {
      Timber.d("Saving compound operations, %s", compoundOperations.toString());
      snappyRepository.putList(COMPOUND_OBJECT_POSTS, Queryable.from(compoundOperations)
            .cast(PostCompoundOperationModel.class).toList());
   }
}
