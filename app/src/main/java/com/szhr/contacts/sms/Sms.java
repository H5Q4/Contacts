package com.szhr.contacts.sms;

import java.io.Serializable;
import java.util.Date;

public class Sms implements Serializable {
    public int id;
    public String sender;
    public String receiver;
    public String content;
    public Date date;
    public int type;
    public boolean fromSim;
    public int status;
}
