package com.worldventures.dreamtrips.modules.membership.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.bundle.TemplateBundle;
import com.worldventures.dreamtrips.modules.membership.bundle.UrlBundle;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.service.InviteShareInteractor;
import com.worldventures.dreamtrips.modules.membership.service.command.CreateFilledInviteTemplateCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.SendInvitesCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class EditTemplatePresenter extends Presenter<EditTemplatePresenter.View> {

   private InviteTemplate template;
   private boolean preview = false;

   @Inject InviteShareInteractor inviteShareInteractor;

   public EditTemplatePresenter(TemplateBundle templateBundle) {
      super();
      this.template = templateBundle.getInviteTemplate();
   }

   @Override
   public void onResume() {
      super.onResume();
      view.setFrom(template.getFrom());
      view.setSubject(template.getTitle());
      List<String> to = getMembersAddress();
      view.setTo(TextUtils.join(", ", to));
      view.setWebViewContent(template.getContent());
      if (!TextUtils.isEmpty(template.getVideo())) {
         view.hidePhotoUpload();
      }

   }

   public List<String> getMembersAddress() {
      List<String> to = new ArrayList<>();
      for (Member member : template.getTo()) {
         to.add(member.getSubtitle());
      }
      return to;
   }

   private Intent getShareIntent() {
      InviteTemplate.Type type = template.getType();
      List<String> membersAddress = getMembersAddress();
      String[] addresses = membersAddress.toArray(new String[membersAddress.size()]);
      Intent intent;
      if (type == InviteTemplate.Type.EMAIL) {
         intent = IntentUtils.newEmailIntent(getSubject(), getBody(), addresses);
      } else {
         intent = IntentUtils.newSmsIntent(context, getSmsBody(), addresses);
      }
      trackSharing();
      return intent;
   }

   private void trackSharing() {
      InviteTemplate.Type type = template.getType();
      if (type == InviteTemplate.Type.EMAIL) {
         TrackingHelper.inviteShareAction(TrackingHelper.ACTION_SEND_EMAIL, template.getId(), template.getTo().size());
      } else {
         TrackingHelper.inviteShareAction(TrackingHelper.ACTION_SEND_SMS, template.getId(), template.getTo().size());
      }

   }

   public void previewAction() {
      preview = true;
      updatePreview();
   }

   private String getSubject() {
      return template.getTitle();
   }

   private String getBody() {
      return String.format(context.getString(R.string.invitation_text_template), getUsername(), getMessage(), template.getLink());
   }

   private String getSmsBody() {
      return template.getTitle() + " " + template.getLink();
   }

   private String getMessage() {
      return TextUtils.isEmpty(view.getMessage()) ? "" : "\n\n" + view.getMessage() + ".";
   }

   private String getUsername() {
      return getMembersAddress().size() > 1 ? "" : " " + template.getName();
   }

   private void notifyServer() {
      inviteShareInteractor.sendInvitesPipe()
            .send(new SendInvitesCommand(template.getId(), getContactAddress(), template.getType()));
   }

   private List<String> getContactAddress() {
      return Queryable.from(template.getTo()).map(Member::getSubtitle).toList();
   }

   private void updatePreview() {
      createFilledInviteObservable()
            .subscribe(new ActionStateSubscriber<CreateFilledInviteTemplateCommand>()
                  .onStart(command -> view.startLoading())
                  .onSuccess(command -> getFilledInvitationsTemplateSuccess(command.getResult()))
                  .onFail(this::onFail));
   }

   private void getFilledInvitationsTemplateSuccess(InviteTemplate inviteTemplate) {
      view.finishLoading();
      if (inviteTemplate != null) {
         view.setWebViewContent(inviteTemplate.getContent());
         template.setContent(inviteTemplate.getContent());
         template.setLink(inviteTemplate.getLink());
         if (preview) {
            preview = false;
            view.openPreviewTemplate(new UrlBundle(inviteTemplate.getLink()));
         }
      }
   }

   private void onFail(CreateFilledInviteTemplateCommand createFilledInviteTemplateCommand, Throwable e) {
      view.finishLoading();
      handleError(createFilledInviteTemplateCommand, e);
   }

   public void shareRequest() {
      createFilledInviteObservable()
            .subscribe(new ActionStateSubscriber<CreateFilledInviteTemplateCommand>()
                  .onSuccess(command -> createInviteSuccess(command.getResult()))
                  .onFail(this::handleError));
   }

   private void createInviteSuccess(InviteTemplate template) {
      getFilledInvitationsTemplateSuccess(template);
      view.openShare(getShareIntent());
      notifyServer();
   }

   private Observable<ActionState<CreateFilledInviteTemplateCommand>> createFilledInviteObservable() {
      return inviteShareInteractor.createFilledInviteTemplatePipe()
            .createObservable(new CreateFilledInviteTemplateCommand(template.getId(), view.getMessage()))
            .compose(bindViewToMainComposer());
   }

   public interface View extends Presenter.View {

      void setFrom(String from);

      void setSubject(String title);

      void setTo(String s);

      void setWebViewContent(String content);

      String getMessage();

      void startLoading();

      void finishLoading();

      void hidePhotoUpload();

      void openPreviewTemplate(UrlBundle bundle);

      void openShare(Intent intent);
   }
}
