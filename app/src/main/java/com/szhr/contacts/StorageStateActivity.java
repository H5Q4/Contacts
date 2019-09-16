package com.szhr.contacts;

import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.format.Formatter;
import android.widget.TextView;

import com.android.internal.telephony.AdnRecord;
import com.android.internal.telephony.IIccPhoneBook;
import com.android.internal.telephony.IccConstants;
import com.android.os.ServiceManager;
import com.szhr.contacts.base.BaseActivity;
import com.szhr.contacts.util.ContactOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StorageStateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_state);

        setTitle("存储器状态");
        leftTv.setText("");

        TextView simCapacityTv = findViewById(R.id.simCapacityTv);
        TextView simUsedTv = findViewById(R.id.simUsedTv);
        TextView simFreeTv = findViewById(R.id.simFreeTv);
        TextView phoneCapacityTv = findViewById(R.id.phoneCapacityTv);
        TextView phoneUsedTv = findViewById(R.id.phoneUsedTv);
        TextView phoneFreeTv = findViewById(R.id.phoneFreeTv);

        // TODO Uncomment following code.
//        try {
//            int gsmPhonebookCapacity = ICCPhonebook.getGsmPhonebookCapacity();
//            int gsmPhonebookSize = ICCPhonebook.getGsmPhonebookSize();
//            simCapacityTv.setText(String.format(Locale.CHINA, "手机总空间：%d 条", gsmPhonebookCapacity));
//            simFreeTv.setText(String.format(Locale.CHINA, "剩余空间：%d 条", gsmPhonebookCapacity - gsmPhonebookSize));
//            simUsedTv.setText(String.format(Locale.CHINA, "已用空间：%d 条", gsmPhonebookSize));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        int phoneContactCount = ContactOperations.getPhoneContactCount(getContentResolver());

        phoneCapacityTv.setText(String.format(Locale.CHINA, "手机总空间：%d 条", 300));
        phoneFreeTv.setText(String.format(Locale.CHINA, "剩余空间：%d 条", 300 - phoneContactCount));
        phoneUsedTv.setText(String.format(Locale.CHINA, "已用空间：%d 条", phoneContactCount));

    }


    /**
     * 获取手机内部空间总大小
     *
     * @return 大小，字节为单位
     */
    private long getTotalInternalMemorySize() {
        //获取内部存储根目录
        File path = Environment.getDataDirectory();
        //系统的空间描述类
        StatFs stat = new StatFs(path.getPath());
        //每个区块占字节数
        long blockSize = stat.getBlockSizeLong();
        //区块总数
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机内部可用空间大小
     *
     * @return 大小，字节为单位
     */
    private long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        //获取可用区块数量
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    /**
     * The CDMA card is unaccessible when in fly-mode， consequently, please
     * ensure isFlyMode() is true before call other methods;
     *
     * @author chencc
     */
    public final static class ICCPhonebook {
        private static final String gsmPhonebook = "simphonebook";
        private static final String cdmaPhonebook = "simphonebook3";

        public static boolean isFlyMode() {
            // TODO
            return false;
        }

        /**
         * @return 有效的联系人记录个数
         * @throws RemoteException
         */
        public static int getGsmPhonebookSize() throws RemoteException {
            return getPhonebookSize(gsmPhonebook);
        }

        public static int getCdmaPhonebookSize() throws RemoteException {
            return getPhonebookSize(cdmaPhonebook);
        }

        public static int getGsmPhonebookCapacity() throws RemoteException {
            int size[] = getIccPhonebook(gsmPhonebook).getAdnRecordsSize(
                    IccConstants.EF_ADN);
            return size[2];
        }

        public static int getCdmaPhonebookCapacity() throws RemoteException {
            return getAdnRecords(getIccPhonebook(cdmaPhonebook)).size();
        }

        private static int getPhonebookSize(String service)
                throws RemoteException {
            List<AdnRecord> adnRecordList = getAdnRecords(getIccPhonebook(service));

            int i = 0;
            for (AdnRecord r : adnRecordList) {
                if (!r.isEmpty()) {
                    i++;
                }
            }

            return i;
        }

        private static List<AdnRecord> getAdnRecords(IIccPhoneBook simPhoneBook)
                throws RemoteException {
            List<AdnRecord> adnRecordList = simPhoneBook
                    .getAdnRecordsInEf(IccConstants.EF_ADN);
            // do it twice cause the second time shall read from cache only
            adnRecordList = simPhoneBook.getAdnRecordsInEf(IccConstants.EF_ADN);
            if (adnRecordList == null) {
                adnRecordList = new ArrayList<AdnRecord>();
            }
            return adnRecordList;
        }

        public static IIccPhoneBook getIccPhonebook(String service) {
            IIccPhoneBook simPhoneBook = IIccPhoneBook.Stub
                    .asInterface(ServiceManager.getService(service));
            if (simPhoneBook == null)
                throw new NullPointerException("Can't get the Service: "
                        + service);
            return simPhoneBook;
        }
    }
}
