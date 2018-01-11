package com.worldventures.dreamtrips.modules.dtl_flow.parts.review;

import android.content.Context;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.PostReviewHttpCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;

public class DtlReviewPresenterImpl extends DtlPresenterImpl<DtlReviewScreen,ViewState.EMPTY> implements DtlReviewPresenter {

   private static final int MAX_PHOTOS_COUNT = 5;

   @Inject MerchantsInteractor merchantsInteractor;

   private List<PhotoPickerModel> attachedPhotos = new ArrayList<>();

   public DtlReviewPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      subscribeToPostReview();
   }

   private void subscribeToPostReview() {
      merchantsInteractor.reviewHttpPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideReviewOperationView()).create());
   }

   @Override
   public void post() {
      merchantsInteractor.reviewHttpPipe()
            .send(new PostReviewHttpCommand(getView().provideReviewParams()));
   }

   @Override
   public void onMediaAttached(MediaPickerAttachment attachment) {
      if (attachment.hasImages()) {
         attachedPhotos.addAll(attachment.getChosenImages());
         getView().attachImages(attachment.getChosenImages());
      }
   }

   @Override
   public void onMediaRemoved(PhotoPickerModel attachment) {
      attachedPhotos.remove(attachment);
      getView().removeImage(attachment);
   }

   @Override
   public int getRemainingPhotosCount() {
      return MAX_PHOTOS_COUNT - attachedPhotos.size();
   }
}
