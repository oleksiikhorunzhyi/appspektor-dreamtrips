package com.messenger.api;

import java.util.Arrays;
import java.util.List;

public class ShortProfilesBody {

    public final List<String> usernames;

    public ShortProfilesBody(List<String> usernames) {
        this.usernames = usernames;
    }

    public ShortProfilesBody(String... usernames) {
        this.usernames = Arrays.asList(usernames);
    }
}
