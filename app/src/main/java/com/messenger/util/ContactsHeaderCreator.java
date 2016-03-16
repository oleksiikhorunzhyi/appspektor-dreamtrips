package com.messenger.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;

import com.messenger.entities.DataUser;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ContactsHeaderCreator {

    private final Context context;

    public ContactsHeaderCreator(Context context) {
        this.context = context;
    }

    //todo refactor logic in future
    public SpannableString createHeader(Collection<DataUser> contacts) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.new_chat_chosen_contacts_header_contacts_list_start_value));
        if (!contacts.isEmpty()) {
            sb.append(" (");
            sb.append(String.valueOf(contacts.size()));
            sb.append(")");
        }
        sb.append(": ");

        List<String> userNames = new ArrayList<>();

        for (DataUser user : contacts) {
            CharSequence name = user.getName();
            sb.append(name);
            userNames.add(name.toString());
            sb.append(", ");
        }

        String resultString = sb.toString();
        SpannableString spannableString = new SpannableString(resultString);

        for (int i = 0; i < userNames.size(); i++) {
            String name = userNames.get(i);
            int spanBeginning = resultString.indexOf(name);
            int underlinedSpanEnding = spanBeginning + name.length();
            int coloredSpanEnding = underlinedSpanEnding;
            coloredSpanEnding++;
            assignUnderlinedSpan(spannableString, spanBeginning, underlinedSpanEnding);
            assignBlueSpan(context, spannableString, spanBeginning, coloredSpanEnding);
        }

        return spannableString;
    }

    private void assignUnderlinedSpan(SpannableString spannableString, int start, int end) {
        spannableString.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void assignBlueSpan(Context context, SpannableString spannableString, int start, int end) {
        int spannableColor = ContextCompat.getColor(context, R.color.contact_list_header_selected_contacts);
        spannableString.setSpan(new ForegroundColorSpan(spannableColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
