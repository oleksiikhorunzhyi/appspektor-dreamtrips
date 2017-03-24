package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableRequestReviewParams;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableReviewParams;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.AddReviewAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;

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
    private User user;

    private static final String ERROR_FORM_PROFANITY = "ERROR_FORM_PROFANITY";
    private static final String ERROR_UNKNOWN = "ERROR_UNKNOWN";
    private static final String ERROR_REQUEST_LIMIT_REACHED = "ERROR_REQUEST_LIMIT_REACHED";

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
    public void onPostClick() {
        if (isInternetConnection()){
            if (validateComment()) {
                getView().sendPostReview();
            }
        } else {
            getView().showNoInternetMessage();
        }
    }

    @Override
    public boolean validateComment() {
        boolean validated = false;
        if (getView().isMinimumCharacterWrote()) {
            if (getView().isMaximumCharacterWrote()) {
                if(getView().getRatingBar() > 0){
                    validated = true;
                }
            } else {
                getView().showSnackbarMessage(String.format(getContext().getString(R.string.review_comment_major_letter), maximumCharactersAllowed()));
            }
        } else if (getView().getSizeComment() > 0) {
            getView().showSnackbarMessage(String.format(getContext().getString(R.string.review_comment_minor_letter), minimumCharactersAllowed()));
        }
        return validated;
    }

    @Override
    public void sendAddReview(String description, Integer rating) {
        this.user = appSessionHolder.get().get().getUser();
        ActionPipe<AddReviewAction> addReviewActionActionPipe = merchantInteractor.addReviewsHttpPipe();
        addReviewActionActionPipe
                .observe()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<AddReviewAction>()
                        .onSuccess(this::onMerchantsLoaded)
                        .onProgress(this::onMerchantsLoading)
                        .onFail(this::onMerchantsLoadingError));
        addReviewActionActionPipe.send(AddReviewAction.create(ImmutableRequestReviewParams.builder()
                .brandId(BRAND_ID)
                .productId(merchant.id())
                .build(), ImmutableReviewParams.builder()
                .userEmail(user.getEmail())
                .userNickName(user.getFullName())
                .reviewText(description)
                .rating(String.valueOf(rating))
                .verified(getView().isVerified())
                .userId(String.valueOf(user.getId()))
                .deviceFingerprint(getView().getFingerprintId())
                .authorIpAddress(getIpAddress())
                .build()));
    }

    @Override
    public int maximumCharactersAllowed() {
        int maximumCharactersAllowed = 0;
        if (merchant.reviews() != null && merchant.reviews().reviewSettings() != null) {
            maximumCharactersAllowed = Integer.parseInt(merchant.reviews().reviewSettings().maximumCharactersAllowed());
        }
        return maximumCharactersAllowed;
    }

    @Override
    public int minimumCharactersAllowed() {
        int minimumCharactersAllowed = 0;
        if (merchant.reviews() != null && merchant.reviews().reviewSettings() != null) {
            minimumCharactersAllowed = Integer.parseInt(merchant.reviews().reviewSettings().minimumCharactersAllowed());
        }
        return minimumCharactersAllowed;
    }

    private void onMerchantsLoaded(AddReviewAction action) {
        if (action.getResult().errors() != null){
            getView().onRefreshSuccess();
            validateCodeMessage(action.getResult().errors().get(0).innerError().get(0).formErrors().fieldErrors().reviewText().code());
        } else {
            this.user = appSessionHolder.get().get().getUser();
            ReviewStorage.saveReviewsPosted(context, String.valueOf(user.getId()), merchant.id());
            getView().onRefreshSuccess();
            handlePostNavigation();
        }
    }

    private void handlePostNavigation(){
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
    }

    private void validateCodeMessage(String message){
        switch (message){
            case ERROR_FORM_PROFANITY:
                    getView().showProfanityError();
                break;

            case ERROR_UNKNOWN:
                    getView().showErrorUnknown();
                break;

            case ERROR_REQUEST_LIMIT_REACHED:
                    getView().showErrorLimitReached();
                break;

            default:
                    getView().unrecognizedError();
                break;
        }
    }

    private void onMerchantsLoading(AddReviewAction action, Integer progress) {
        getView().onRefreshProgress();
    }

    private void onMerchantsLoadingError(AddReviewAction action, Throwable throwable) {
        getView().onRefreshError(throwable.getMessage());
    }

    public boolean isInternetConnection(){
        boolean isInternet = false;
        try{
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isInternet = activeNetwork.isConnectedOrConnecting();
        } catch(Exception e){
            e.printStackTrace();
        }
        return isInternet;
    }

    public String getIpAddress() {
        return "10.20.20.122";
    }
}