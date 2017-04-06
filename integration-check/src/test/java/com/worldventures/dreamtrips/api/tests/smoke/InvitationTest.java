package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.invitation.CreateFilledInvitationTemplateHttpAction;
import com.worldventures.dreamtrips.api.invitation.CreateInvitationHttpAction;
import com.worldventures.dreamtrips.api.invitation.CreateInvitationPreviewHttpAction;
import com.worldventures.dreamtrips.api.invitation.GetFilledInvitationTemplateHttpAction;
import com.worldventures.dreamtrips.api.invitation.GetInvitationTemplatesHttpAction;
import com.worldventures.dreamtrips.api.invitation.GetInvitationsHistoryHttpAction;
import com.worldventures.dreamtrips.api.invitation.model.CreateInvitationParams;
import com.worldventures.dreamtrips.api.invitation.model.FilledInvitationParams;
import com.worldventures.dreamtrips.api.invitation.model.ImmutableCreateInvitationParams;
import com.worldventures.dreamtrips.api.invitation.model.ImmutableFilledInvitationParams;
import com.worldventures.dreamtrips.api.invitation.model.Invitation;
import com.worldventures.dreamtrips.api.invitation.model.InvitationPreview;
import com.worldventures.dreamtrips.api.invitation.model.InvitationTemplate;
import com.worldventures.dreamtrips.api.invitation.model.PreviewInvitationParams;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Invitation"})
public class InvitationTest extends BaseTestWithSession {

    @Fixture("create_invitation_params")
    CreateInvitationParams createInvitationParams;

    @Fixture("filled_invitation_params")
    FilledInvitationParams filledInvitationParams;

    @Fixture("preview_invitation_params")
    PreviewInvitationParams previewInvitationParams;

    volatile List<InvitationTemplate> invitationTemplates;

    volatile InvitationPreview filledTemplate;

    @Test
    void testGetInvitationTemplates() {
        invitationTemplates = execute(new GetInvitationTemplatesHttpAction()).response();
        //
        assertThat(invitationTemplates).isNotEmpty();
    }

    @Test(dependsOnMethods = "testGetInvitationTemplates")
    void testCreateInvitationPreview() {
        InvitationPreview preview = execute(
                new CreateInvitationPreviewHttpAction(firstTemplateId(), previewInvitationParams)
        ).response();
        //
        checkPreview(preview);
    }

    @Test(dependsOnMethods = "testGetInvitationTemplates")
    void testCreateFilledInvitationTemplate() {
        filledInvitationParams = ImmutableFilledInvitationParams.builder()
                .from(filledInvitationParams)
                .templateId(firstTemplateId())
                .build();
        filledTemplate = execute(
                new CreateFilledInvitationTemplateHttpAction(filledInvitationParams)
        ).response();
        //
        checkPreview(filledTemplate);
    }

    @Test(dependsOnMethods = "testCreateFilledInvitationTemplate")
    void testGetFilledTemplate() {
        InvitationPreview invitationPreview = execute(new GetFilledInvitationTemplateHttpAction(filledTemplate.id())).response();
        //
        checkPreview(invitationPreview);
    }

    @Test(dependsOnMethods = "testGetInvitationTemplates")
    void testCreateInvitation() {
        createInvitationParams = ImmutableCreateInvitationParams.builder()
                .from(createInvitationParams)
                .templateId(firstTemplateId())
                .build();
        CreateInvitationHttpAction action = execute(new CreateInvitationHttpAction(createInvitationParams));
        //
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(dependsOnMethods = "testCreateInvitation")
    void testGetInvitationsHistory() {
        List<Invitation> invitations = execute(new GetInvitationsHistoryHttpAction()).response();
        //
        String contactsAsString = createInvitationParams.contacts().stream().collect(Collectors.joining(", "));
        assertThat(invitations)
                .isNotEmpty()
                .extracting(Invitation::contact).contains(contactsAsString);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Sugar
    ///////////////////////////////////////////////////////////////////////////

    private int firstTemplateId() {
        return invitationTemplates.get(0).id();
    }

    private void checkPreview(InvitationPreview preview) {
        assertThat(preview).isNotNull();
        // there are templates which doesn't propagate message to content,
        // by now we can't know for sure if preview will contain custom message
        // assertThat(preview.content()).containsIgnoringCase(filledInvitationParams.message());
    }

}
