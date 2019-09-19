package com.android.internal.telephony;

import java.util.List;

public class IIccPhoneBook {
    public int[] getAdnRecordsSize(Object efAdn) {
        return new int[0];
    }

    public List<AdnRecord> getAdnRecordsInEf(Object efAdn) {
        return null;
    }

    public static class Stub {
        public static IIccPhoneBook asInterface(Object service) {
            return null;
        }
    }
}
