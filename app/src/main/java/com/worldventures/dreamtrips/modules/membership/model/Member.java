package com.worldventures.dreamtrips.modules.membership.model;

import android.telephony.PhoneNumberUtils;

import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

import java.io.Serializable;

public class Member implements Serializable, Filterable {

    String id;
    String name;
    String email;
    String phone;
    boolean emailIsMain = true;
    boolean isChecked;
    History history;


    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public void setEmailIsMain(boolean emailIsMain) {
        this.emailIsMain = emailIsMain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSubtitle() {
        return emailIsMain ? email : phone;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isEmailMain() {
        return emailIsMain;
    }

    @Override
    public boolean containsQuery(String query) {
        return (name != null && name.toLowerCase().contains(query))
                || (email != null && email.toLowerCase().contains(query))
                || PhoneNumberUtils.normalizeNumber(phone).contains(query);
    }
}
