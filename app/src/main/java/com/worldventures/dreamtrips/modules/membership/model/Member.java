package com.worldventures.dreamtrips.modules.membership.model;

import android.text.TextUtils;

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
    int originalPosition;

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

    public boolean isEmailIsMain() {
        return emailIsMain;
    }

    public int getOriginalPosition() {
        return originalPosition;
    }

    public void setOriginalPosition(int originalPosition) {
        this.originalPosition = originalPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        boolean nameEq = name != null ? name.equals(member.name) : member.name == null;
        boolean emailEq = email != null ? email.equals(member.email) : member.email == null;
        boolean phoneEq = phone != null ? phone.equals(member.phone) : member.phone == null;
        if (emailIsMain && nameEq && emailEq) return true;
        else if (nameEq && phoneEq) return true;
        else return false;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        return result;
    }

    @Override
    public boolean containsQuery(String query) {
        if (query == null || TextUtils.isEmpty(query.trim())) return false;
        return (name != null && name.toLowerCase().contains(query))
                || (email != null && email.toLowerCase().contains(query))
                || (phone != null && phone.contains(query));
    }
}
