package com.worldventures.dreamtrips.social.ui.feed.presenter

import com.worldventures.core.model.Location
import com.worldventures.dreamtrips.social.ui.background_uploading.model.CompoundOperationState
import com.worldventures.dreamtrips.social.ui.background_uploading.model.ImmutablePostCompoundOperationModel
import com.worldventures.dreamtrips.social.ui.background_uploading.model.ImmutableTextPostBody
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostBody
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle
import java.util.Date

const val OPERATION_ID = 134

fun provideCompoundOperation(): PostCompoundOperationModel<PostBody> {
   return ImmutablePostCompoundOperationModel.builder<PostBody>()
         .id(OPERATION_ID)
         .creationDate(Date())
         .type(PostBody.Type.TEXT)
         .state(CompoundOperationState.SCHEDULED)
         .body(ImmutableTextPostBody.builder()
               .text("text")
               .origin(CreateEntityBundle.Origin.FEED)
               .location(Location())
               .build())
         .build()!!
}