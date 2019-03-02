package com.omardev.backuptest;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GetFromPhone extends AppCompatActivity {

    public static TextView textView;
    ArrayList<String> listContact = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    String date, type;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_from_phone);

        textView = findViewById(R.id.textView);
        //getAllMessages("get"); //get=>only get , post=>get and post
        insertMessages();
    }

    //post to server from phone
    public void getAllContacts(String type) {
        this.type = type;
        String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/getContacts.php?username=omar&type=contact";
        new AsynTask(this, "contact").execute(url);
    }

    public void getAllContacts(ArrayList<ListItemContact> list) {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        ArrayList<ListItemContact> listItemContacts = new ArrayList<>();
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String rawContentID = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
            String label = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));

            listItemContacts.add(new ListItemContact("omar", name, phoneNumber));
        }
        phones.close();

        switch (type) {
            case "post":
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < listItemContacts.size(); j++) {
                        if (list.get(i).getNum().equals(listItemContacts.get(j).getNum())) {
                            if (list.get(i).getName().equals(listItemContacts.get(j).getName())) {
                                listItemContacts.remove(j);
                            }
                        }
                    }
                }

                String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/saveContacts.php?type=contact";
                Toast.makeText(this, listItemContacts.size() + " insert to db", Toast.LENGTH_SHORT).show();
                new AsynTaskPost(this, "contact", listItemContacts).execute(url);
                break;

            case "get":
                for (int j = 0; j < listItemContacts.size(); j++) {
                    for (int i = 0; i < list.size(); i++) {
                        if (listItemContacts.get(j).getNum().equals(list.get(i).getNum())) {
                            if (listItemContacts.get(j).getName().equals(list.get(i).getName())) {
                                list.remove(i);
                            }
                        }
                    }
                }

                insertContacts(list);
                break;
        }

    }

    private void getAllCallLog(String type) {
        this.type = type;
        String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/getContacts.php?username=omar&type=calllog";
        new AsynTask(this, "calllog").execute(url);
    }

    public void getAllCallLog(ArrayList<ListItemCallLog> list) {
        Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor c = managedQuery(allCalls, null, null, null, null);
        ArrayList<ListItemCallLog> listItemCallLogs = new ArrayList<>();

        while (c.moveToNext()) {
            String num = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));// for  number
            String name = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
            String duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));// for duration in second

            int type = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)));// for call type, Incoming or out going
            String callType = "";
            switch (type) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    callType = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    callType = "MISSED";
                    break;
            }

            long date = Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DATE)));
            Date callDate = new Date(date);

            if (TextUtils.isEmpty(name))
                name = "noname";
            listItemCallLogs.add(new ListItemCallLog("omar", name, num, duration,
                    String.valueOf(type), String.valueOf(date)));
        }
        c.close();

        switch (type) {
            case "post":
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < listItemCallLogs.size(); j++) {
                        if (list.get(i).getNum().equals(listItemCallLogs.get(j).getNum())) {
                            if (list.get(i).getName().equals(listItemCallLogs.get(j).getName())) {
                                if (list.get(i).getDuration().equals(listItemCallLogs.get(j).getDuration())) {
                                    if (list.get(i).getCallType().equals(listItemCallLogs.get(j).getCallType()))
                                        listItemCallLogs.remove(j);
                                }
                            }
                        }
                    }
                }

                String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/saveContacts.php?type=calllog";
                Toast.makeText(this, listItemCallLogs.size() + " insert to db", Toast.LENGTH_SHORT).show();
                new AsynTaskPost(this, listItemCallLogs, "calllog").execute(url);
                break;

            case "get":
                for (int j = 0; j < listItemCallLogs.size(); j++) {
                    for (int i = 0; i < list.size(); i++) {
                        if (listItemCallLogs.get(j).getNum().equals(list.get(i).getNum())) {
                            if (listItemCallLogs.get(j).getName().equals(list.get(i).getName())) {
                                if (listItemCallLogs.get(j).getDuration().equals(list.get(i).getDuration())) {
                                    if (listItemCallLogs.get(j).getCallType().equals(list.get(i).getCallType()))
                                        list.remove(i);
                                }
                            }
                        }
                    }
                }

                insertCallLogs(list);
                break;
        }

    }

    private void getAllMessages(String type) {
        this.type = type;
        String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/getContacts.php?username=omar&type=sms";
        new AsynTask(this, "sms").execute(url);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getAllMessages(ArrayList<ListItemSms> list) {
        Cursor c = getContentResolver().query(Telephony.Sms.CONTENT_URI,
                null, null, null, null);

        ArrayList<ListItemSms> listSms = new ArrayList<>();
        if (c != null) {
            int totalSMS = c.getCount();

            if (c.moveToFirst()) {
                for (int j = 0; j < totalSMS; j++) {

                    String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));

                    String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                    Date dateFormat = new Date(Long.valueOf(smsDate));
                    date = smsDate;

                    String type = "";
                    String tpe = String.valueOf(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)));
                    switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                        case Telephony.Sms.MESSAGE_TYPE_INBOX:
                            type = "inbox";
                            break;
                        case Telephony.Sms.MESSAGE_TYPE_SENT:
                            type = "sent";
                            break;
                        case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                            type = "outbox";
                            break;
                    }

                    listSms.add(new ListItemSms("omar", number, body, date, tpe));
                    //Log.d("info", number + " , " + body + " , " + smsDate + " , " + type);
                    c.moveToNext();
                }
            }
        } else
            Toast.makeText(this, "No message to show!", Toast.LENGTH_SHORT).show();
        c.close();

        switch (type) {
            case "post":
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < listSms.size(); j++) {
                        if (list.get(i).getNum().equals(listSms.get(j).getNum())) {
                            if (list.get(i).getBody().equals(listSms.get(j).getBody())) {
                                if (list.get(i).getType().equals(listSms.get(j).getType()))
                                    listSms.remove(j);
                            }
                        }
                    }
                }

                String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/saveContacts.php?type=sms";
                Toast.makeText(this, listSms.size() + " insert to db", Toast.LENGTH_SHORT).show();
                new AsynTaskPost(listSms, this, "sms").execute(url);
                break;

            case "get":
                /**for (int j = 0; j < listItemContacts.size(); j++) {
                 for (int i = 0; i < list.size(); i++) {
                 if (listItemContacts.get(j).getNum().equals(list.get(i).getNum())) {
                 if (listItemContacts.get(j).getName().equals(list.get(i).getName())) {
                 list.remove(i);
                 }
                 }
                 }
                 }

                 insertContacts(list);*/
                break;
        }

    }


    //insert to phone from server
    public void insertContacts(ArrayList<ListItemContact> list) {
        Toast.makeText(this, "contact to save: " + list.size(), Toast.LENGTH_SHORT).show();
        if (list.size() == 0)
            return;

        for (int i = 0; i < list.size(); i++) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            int rawContactInsertIndex = ops.size();

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, list.get(i).getName()) // Name of the person
                    .build());
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, list.get(i).getNum()) // Number of the person
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) // Type of mobile number
                    .build());
            try {
                ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {
                Toast.makeText(this, "error in inserting contact !", Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(this, "succes", Toast.LENGTH_SHORT).show();

    }

    public void insertCallLogs(ArrayList<ListItemCallLog> list) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission requierd", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "call to save: " + list.size(), Toast.LENGTH_SHORT).show();
        if (list.size() == 0)
            return;
        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(CallLog.Calls.NUMBER, list.get(i).getNum());
            values.put(CallLog.Calls.DATE, list.get(i).getCallDate());
            values.put(CallLog.Calls.DURATION, list.get(i).getDuration());
            values.put(CallLog.Calls.TYPE, list.get(i).getCallType());
            getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
        }
        Toast.makeText(this, "succes", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void insertMessages() {
        //ArrayList<ListItemSms> list
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.ADDRESS, "66453");
        values.put(Telephony.Sms.BODY, "voici le tst message que j'attends");
        values.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_INBOX);
        values.put(Telephony.Sms.READ, SmsManager.STATUS_ON_ICC_UNREAD);
        values.put(Telephony.Sms.DATE_SENT, Calendar.getInstance().getTimeInMillis() - 3600);
        values.put(Telephony.Sms.DATE, String.valueOf(Calendar.getInstance().getTimeInMillis()));
        values.put(Telephony.Sms.SEEN, "1");
        values.put(Telephony.Sms.THREAD_ID, "4");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission requierd", Toast.LENGTH_SHORT).show();
            return;
        }
        getContentResolver().insert(Telephony.Sms.CONTENT_URI, values);
        Toast.makeText(this, "succes", Toast.LENGTH_SHORT).show();
    }

}
