package com.szhr.contacts.model;

import java.io.Serializable;

public class Contact implements Serializable {
    private boolean fromSim;
    private String displayName;
    private String phoneNumber;

    public Contact(String displayName, String phoneNumber) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
    }

    public Contact() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isFromSim() {
        return fromSim;
    }

    public void setFromSim(boolean fromSim) {
        this.fromSim = fromSim;
    }


}
