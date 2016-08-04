package com.worldventures.dreamtrips.wallet.service.command;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.FileUtils;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.AssignUserAction;
import io.techery.janet.smartcard.action.user.UpdateUserPhotoAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.janet.smartcard.model.User;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetupUserDataCommand extends Command<Void> implements InjectableAction {

    @Inject @Named(JANET_WALLET) Janet janet;
    @Inject SessionHolder<UserSession> userSessionHolder;

    private final String fullName;
    private final File avatarFile;

    public SetupUserDataCommand(String fullName, File avatarFile) {
        // TODO: 8/2/16 change on first name and second name
        this.fullName = fullName;
        this.avatarFile = avatarFile;
    }

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        WalletValidateHelper.validateUserFullNameOrThrow(fullName);
        janet.createPipe(AssignUserAction.class)
                .createObservableResult(new AssignUserAction(createUser()))
                .flatMap(action -> Observable.fromCallable(this::getAvatarAsByteArray))
                .flatMap(bytesArray -> janet.createPipe(UpdateUserPhotoAction.class)
                        .createObservableResult(new UpdateUserPhotoAction(bytesArray)))
                .subscribe(action -> callback.onSuccess(null), callback::onFail);
    }

    private User createUser() {
        String[] nameParts = fullName.split(" ");
        return ImmutableUser.builder()
                .firstName(nameParts[0])
                .lastName(nameParts[0])
                .memberStatus(getMemberStatus())
                .memberId(userSessionHolder.get().get().getUser().getId())
                .build();
    }

    private User.MemberStatus getMemberStatus() {
        com.worldventures.dreamtrips.modules.common.model.User user = userSessionHolder.get().get().getUser();
        if (user.isGold()) return User.MemberStatus.GOLD;
        if (user.isGeneral() || user.isPlatinum()) return User.MemberStatus.ACTIVE;
        return User.MemberStatus.INACTIVE;
    }

    private byte[] getAvatarAsByteArray() throws IOException {
        return FileUtils.readByteArray(avatarFile);
    }
}
