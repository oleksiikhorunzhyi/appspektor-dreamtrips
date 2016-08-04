package com.worldventures.dreamtrips.wallet.util;


import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;

public class AddressUtil {

    public static String obtainAddressLabel(AddressInfo addressInfo, String country) {
        return String.format("%s\n%s\n%s, %s %s\n%s", addressInfo.address1(), addressInfo.address2(),
                addressInfo.city(), addressInfo.state(), addressInfo.zip(), country);
    }

}
