package com.worldventures.dreamtrips.modules.infopages.service.command;


import android.content.Context;
import android.os.Build;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.feedback.SendFeedbackHttpAction;
import com.worldventures.dreamtrips.api.feedback.model.Feedback;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableFeedback;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableMetadata;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableSmartCardMetadata;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateSubscriber;

@CommandAction
public class SendFeedbackCommand extends CommandWithError<Void> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AppVersionNameBuilder appVersionNameBuilder;
   @Inject Context context;

   private int reasonId;
   private String description;

   public SendFeedbackCommand(int reasonId, String description) {
      this.reasonId = reasonId;
      this.description = description;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_send_feedback;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new GetActiveSmartCardCommand())
            .subscribe(new ActionStateSubscriber<GetActiveSmartCardCommand>()
                  .onSuccess(command -> sendFeedback(callback, command.getResult()))
                  .onFail((command, throwable) -> sendFeedback(callback, command.getResult())));
   }

   private void sendFeedback(CommandCallback callback, SmartCard smartCard) {
      janet.createPipe(SendFeedbackHttpAction.class)
            .createObservableResult(new SendFeedbackHttpAction(provideFeedbackBody(smartCard)))
            .subscribe(action -> callback.onSuccess(null), callback::onFail);
   }

   private Feedback provideFeedbackBody(SmartCard smartCard) {
      ImmutableFeedback.Builder builder = ImmutableFeedback.builder()
            .reasonId(reasonId)
            .text(description)
            .metadata(provideMetadata());
      if (smartCard != null) builder.smartCardMetadata(provideSmartCardMetadata(smartCard));
      return builder.build();
   }

   private Feedback.SmartCardMetadata provideSmartCardMetadata(SmartCard smartCard) {
      return ImmutableSmartCardMetadata.builder()
            .smartCardId(Integer.parseInt(smartCard.smartCardId()))
            .smartCardSerialNumber(smartCard.serialNumber())
            .bleId(smartCard.deviceAddress())
            .firmwareVersion(smartCard.firmWareVersion())
            .sdkVersion(smartCard.sdkVersion())
            .build();
   }

   private Feedback.Metadata provideMetadata() {
      String osVersion = String.format("android-%d", Build.VERSION.SDK_INT);
      String appVersion = appVersionNameBuilder.getSemanticVersionName();
      String deviceModel = String.format("%s:%s", Build.MANUFACTURER, Build.MODEL);
      Feedback.DeviceType deviceType = ViewUtils.isTablet(context) ? Feedback.DeviceType.TABLET : Feedback.DeviceType.PHONE;
      return ImmutableMetadata.builder()
            .appVersion(appVersion)
            .deviceModel(deviceModel)
            .osVersion(osVersion)
            .deviceType(deviceType)
            .build();
   }
}
