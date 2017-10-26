package com.worldventures.core.modules.infopages.service.command;

import android.os.Build;

import com.worldventures.core.R;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.modules.infopages.service.storage.InfopagesStorage;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.api.feedback.SendFeedbackHttpAction;
import com.worldventures.dreamtrips.api.feedback.model.Feedback;
import com.worldventures.dreamtrips.api.feedback.model.FeedbackAttachment;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableFeedback;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableMetadata;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.service.DeviceInfoProvider;
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class SendFeedbackCommand extends CommandWithError<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject AppVersionNameBuilder appVersionNameBuilder;
   @Inject DeviceInfoProvider deviceInfoProvider;
   @Inject InfopagesStorage repository;
   @Inject MapperyContext mappery;

   private final int reasonId;
   private final String description;
   private final List<FeedbackImageAttachment> imageAttachments;

   public SendFeedbackCommand(int reasonId, String description, List<FeedbackImageAttachment> imageAttachments) {
      this.reasonId = reasonId;
      this.description = description;
      this.imageAttachments = imageAttachments;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      janet.createPipe(SendFeedbackHttpAction.class)
            .createObservableResult(new SendFeedbackHttpAction(provideFeedbackBody()))
            .subscribe(action -> callback.onSuccess(null), callback::onFail);
   }

   private Feedback provideFeedbackBody() {
      return ImmutableFeedback.builder()
            .reasonId(reasonId)
            .text(description)
            .metadata(provideMetadata())
            .attachments(mappery.convert(imageAttachments, FeedbackAttachment.class))
            .build();
   }

   private Feedback.Metadata provideMetadata() {
      String osVersion = String.format(Locale.US, "android-%d", Build.VERSION.SDK_INT);
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
