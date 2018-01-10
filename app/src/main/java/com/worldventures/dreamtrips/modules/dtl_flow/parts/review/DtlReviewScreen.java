package com.worldventures.dreamtrips.modules.dtl_flow.parts.review;

import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.PostReviewActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.PostReviewHttpCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface DtlReviewScreen extends DtlScreen {

   OperationView<PostReviewHttpCommand> provideReviewOperationView();

   PostReviewActionParams provideReviewParams();

   void attachImages(List<PhotoPickerModel> images);
}
