package com.worldventures.dreamtrips.modules.infopages.service.command;

import android.os.Build;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.feedback.SendFeedbackHttpAction;
import com.worldventures.dreamtrips.api.feedback.model.Feedback;
import com.worldventures.dreamtrips.api.feedback.model.FeedbackAttachment;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableFeedback;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableMetadata;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableSmartCardMetadata;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.smartCardFirmwareVersion;

@CommandAction
public class SendFeedbackCommand extends CommandWithError implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject AppVersionNameBuilder appVersionNameBuilder;
   @Inject DeviceInfoProvider deviceInfoProvider;
   @Inject SnappyRepository snappyRepository;
   @Inject MapperyContext mappery;

   private int reasonId;
   private String description;
   private List<FeedbackImageAttachment> imageAttachments;

   public SendFeedbackCommand(int reasonId, String description, List<FeedbackImageAttachment> imageAttachments) {
      this.reasonId = reasonId;
      this.description = description;
      this.imageAttachments = imageAttachments;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(SendFeedbackHttpAction.class)
            .createObservableResult(new SendFeedbackHttpAction(provideFeedbackBody()))
            .subscribe(action -> callback.onSuccess(null), callback::onFail);
   }

   private Feedback provideFeedbackBody() {
      ImmutableFeedback.Builder builder = ImmutableFeedback.builder()
            .reasonId(reasonId)
            .text(description)
            .metadata(provideMetadata())
            .smartCardMetadata(provideSmartCardMetadata());
      builder.attachments(mappery.convert(imageAttachments, FeedbackAttachment.class));

      return builder.build();
   }

   private Feedback.SmartCardMetadata provideSmartCardMetadata() {
      SmartCard smartCard = snappyRepository.getSmartCard();
      SmartCardDetails details = snappyRepository.getSmartCardDetails();
      if (smartCard == null || details == null) return null;

      return ImmutableSmartCardMetadata.builder()
            .smartCardId((int) details.smartCardId())
            .smartCardSerialNumber(details.serialNumber())
            .bleId(details.bleAddress())

            .firmwareVersion(smartCardFirmwareVersion(smartCard.firmwareVersion()))
            .sdkVersion(smartCard.sdkVersion())
            .build();
   }

   private Feedback.Metadata provideMetadata() {
      String osVersion = String.format("android-%d", Build.VERSION.SDK_INT);
      String appVersion = appVersionNameBuilder.getSemanticVersionName();
      String deviceModel = String.format("%s:%s", Build.MANUFACTURER, Build.MODEL);
      Feedback.DeviceType deviceType = deviceInfoProvider.isTablet()
            ? Feedback.DeviceType.TABLET : Feedback.DeviceType.PHONE;

      return ImmutableMetadata.builder()
            .appVersion(appVersion)
            .deviceModel(deviceModel)
            .osVersion(osVersion)
            .deviceType(deviceType)
            .build();
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_send_feedback;
   }
}
