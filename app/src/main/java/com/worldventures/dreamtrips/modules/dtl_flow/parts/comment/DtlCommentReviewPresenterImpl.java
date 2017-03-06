package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableRequestReviewParams;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableReviewParams;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.AddReviewAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPath;
import com.worldventures.dreamtrips.modules.common.model.User;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;
import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlCommentReviewPresenterImpl extends DtlPresenterImpl<DtlCommentReviewScreen, ViewState.EMPTY> implements DtlCommentReviewsPresenter {

    @Inject
    PresentationInteractor presentationInteractor;
    @Inject
    MerchantsInteractor merchantInteractor;
    @Inject
    SessionHolder<UserSession> appSessionHolder;

    private final Merchant merchant;
    private static final String BRAND_ID = "1";

    public DtlCommentReviewPresenterImpl(Context context, Injector injector, Merchant merchant) {
        super(context);
        injector.inject(this);
        this.merchant = merchant;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onBackPressed() {
        if (getView().getRatingBar() > 0 || getView().getSizeComment() > 0) {
            getView().showDialogMessage(getContext().getString(R.string.review_comment_discard_changes));
        } else {
            navigateToDetail("");
        }
    }

    @Override
    public void navigateToDetail(String message) {
        Path path = new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant, null, message);
        History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
        historyBuilder.pop();
        historyBuilder.pop();
        historyBuilder.push(path);
        Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.BACKWARD);
    }

    @Override
    public boolean validateComment() {
        boolean validated = false;
        if (getView().isMinimumCharacterWrote()) {
            if (getView().isMaximumCharacterWrote()) {
                if(getView().getRatingBar() > 0){
                    if (merchant.reviews().total().equals("")){
                        navigateToDetail(getContext().getString(R.string.snack_review_success));
                    } else {
                        Path path = new DtlReviewsPath(merchant, getContext().getString(R.string.snack_review_success));
                        History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
                        historyBuilder.pop();
                        if (getView().isFromListReview()){
                            historyBuilder.pop();
                        }
                        historyBuilder.push(path);
                        Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
                    }
                    validated = true;
                }
            } else {
                getView().showSnackbarMessage(getContext().getString(R.string.review_comment_major_letter));
            }
        } else if (getView().getSizeComment() > 0) {
            getView().showSnackbarMessage(getContext().getString(R.string.review_comment_minor_letter));
        }
        return validated;
    }

    @Override
    public void sendAddReview(String description, Integer rating, Boolean verified) {
        User user = appSessionHolder.get().get().getUser();
        ActionPipe<AddReviewAction> addReviewActionActionPipe = merchantInteractor.addReviewsHttpPipe();
        addReviewActionActionPipe
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<AddReviewAction>()
                        .onSuccess(this::onMerchantsLoaded)
                        .onProgress(this::onMerchantsLoading)
                        .onFail(this::onMerchantsLoadingError));
        addReviewActionActionPipe.send(AddReviewAction.create(ImmutableRequestReviewParams.builder()
                .brandId(BRAND_ID)
                .productId(merchant.id())
                .userEmail(user.getEmail())
                .userNickName(user.getUsername())
                .reviewText(description)
                .rating(String.valueOf(rating))
                .verified(verified)
                .userId(String.valueOf(user.getId()))
                .deviceFingerprint(BRAND_ID)
                .build(), ImmutableReviewParams.builder()
                .userEmail(user.getEmail())
                .userNickName(user.getUsername())
                .reviewText(description)
                .rating(String.valueOf(rating))
                .verified(verified)
                .userId(String.valueOf(user.getId()))
                .build()));
    }

    private void onMerchantsLoaded(AddReviewAction action) {
        getView().onRefreshSuccess();
    }

    private void onMerchantsLoading(AddReviewAction action, Integer progress) {
        getView().onRefreshProgress();
    }

    private void onMerchantsLoadingError(AddReviewAction action, Throwable throwable) {
        getView().onRefreshError(throwable.getMessage());
    }
}