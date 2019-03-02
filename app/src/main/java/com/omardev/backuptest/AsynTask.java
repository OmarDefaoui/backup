package com.omardev.backuptest;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Omar on 27/03/2018.
 */

public class AsynTask extends AsyncTask<String, String, String> {

    Context context;
    ArrayList<ListItemContact> listContact = new ArrayList<>();
    ArrayList<ListItemCallLog> listCalllog = new ArrayList<>();
    ArrayList<ListItemSms> listSms = new ArrayList<>();
    String type, typeOperation, username = ContentActivity.username;

    public AsynTask(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    public AsynTask(Context context, String type, String typeOperation) {
        this.context = context;
        this.type = type;
        this.typeOperation = typeOperation;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            String NewsData = "";
            URL url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            NewsData = readStream(in);
            in.close();
            //Thread.sleep(3000);
            publishProgress(NewsData);
            urlConnection.disconnect();

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onProgressUpdate(String... values) {
        try {
            JSONArray jsonArray;
            switch (type) {
                case "contact":
                    jsonArray = new JSONArray(values[0]);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);

                        String name = "" + o.getString("name");
                        String num = "" + o.getString("num");

                        listContact.add(new ListItemContact(username, name, num));
                    }
                    ContentActivity.UploadContact.adapter = new RecyclerViewAdapter(0, getAllContacts(listContact, typeOperation));
                    ContentActivity.UploadContact.recyclerView.setAdapter(ContentActivity.UploadContact.adapter);
                    //((GetFromPhone) context).getAllContacts(listContact);
                    break;

                case "calllog":
                    jsonArray = new JSONArray(values[0]);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);

                        String name = "" + o.getString("name");
                        String num = "" + o.getString("num");
                        String duration = "" + o.getString("duration");
                        String calltype = "" + o.getString("calltype");
                        String calldate = "" + o.getString("calldate");

                        listCalllog.add(new ListItemCallLog(username, name, num, duration, calltype, calldate));
                    }
                    ContentActivity.UploadContact.adapter = new RecyclerViewAdapter(getAllCallLog(listCalllog, typeOperation), 1);
                    ContentActivity.UploadContact.recyclerView.setAdapter(ContentActivity.UploadContact.adapter);
                    //((GetFromPhone) context).getAllCallLog(listCalllog);
                    break;

                case "sms":
                    jsonArray = new JSONArray(values[0]);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);

                        String num = "" + o.getString("num");
                        String body = "" + o.getString("body");
                        String date = "" + o.getString("date");
                        String type = "" + o.getString("type");

                        listSms.add(new ListItemSms(username, num, body, date, type));
                    }
                    ContentActivity.UploadContact.adapter = new RecyclerViewAdapter(getAllMessages(listSms, typeOperation), 2, "");
                    ContentActivity.UploadContact.recyclerView.setAdapter(ContentActivity.UploadContact.adapter);
                    //((GetFromPhone) context).getAllMessages(listSms);
                    break;
            }

        } catch (Exception e) {
            Toast.makeText(context, "error in onProgress asynctask", Toast.LENGTH_SHORT).show();
        }

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    String readStream(InputStream in) {
        String s = "";
        try {
            InputStreamReader i = new InputStreamReader(in);
            BufferedReader b = new BufferedReader(i);
            String line;
            while ((line = b.readLine()) != null) {
                s += line;
            }

        } catch (Exception e) {
        }
        return s;
    }

    public ArrayList getAllContacts(ArrayList<ListItemContact> list, String type) {
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        ArrayList<ListItemContact> listItemContacts = new ArrayList<>();
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String rawContentID = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
            String label = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
            //String namePrimary = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
            //String nameAlternative = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE));

            listItemContacts.add(new ListItemContact(username, name, phoneNumber));
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

                ContentActivity.isContact = 1;
                ContentActivity.listContact = listItemContacts;
                //String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/saveContacts.php?type=contact";
                //Toast.makeText(context, listItemContacts.size() + " will be inserted to db", Toast.LENGTH_SHORT).show();
                //new AsynTaskPost(context, "contact", listItemContacts).execute(url);
                return listItemContacts;

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

                ContentActivity.isContact = 2;
                ContentActivity.listContact = list;
                //Toast.makeText(context, list.size() + " will be saved into phone", Toast.LENGTH_SHORT).show();
                //insertContacts(list);
                return list;

            default:
                return null;
        }

    }

    public ArrayList getAllCallLog(ArrayList<ListItemCallLog> list, String type) {
        Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor c = context.getContentResolver().query(allCalls, null, null, null, null);
        ArrayList<ListItemCallLog> listItemCallLogs = new ArrayList<>();

        while (c.moveToNext()) {
            String num = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));// for  number
            String name = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
            String duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));// for duration in second

            int typeCall = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)));// for call type, Incoming or out going
            String callType = "";
            switch (typeCall) {
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
            listItemCallLogs.add(new ListItemCallLog(username, name, num, duration,
                    String.valueOf(typeCall), String.valueOf(date)));
        }
        c.close();

        switch (type) {
            case "post":
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < listItemCallLogs.size(); j++) {
                        if (list.get(i).getNum().equals(listItemCallLogs.get(j).getNum())) {
                            if (list.get(i).getDuration().equals(listItemCallLogs.get(j).getDuration())) {
                                if (list.get(i).getCallType().equals(listItemCallLogs.get(j).getCallType()))
                                    listItemCallLogs.remove(j);
                            }
                        }
                    }
                }

                ContentActivity.isCalllog = 1;
                ContentActivity.listCalllog = listItemCallLogs;
                //String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/saveContacts.php?type=calllog";
                //Toast.makeText(context, listItemCallLogs.size() + " insert to db", Toast.LENGTH_SHORT).show();
                //new AsynTaskPost(context, listItemCallLogs, "calllog").execute(url);
                return listItemCallLogs;

            case "get":
                for (int j = 0; j < listItemCallLogs.size(); j++) {
                    for (int i = 0; i < list.size(); i++) {
                        if (listItemCallLogs.get(j).getNum().equals(list.get(i).getNum())) {
                            if (listItemCallLogs.get(j).getDuration().equals(list.get(i).getDuration())) {
                                if (listItemCallLogs.get(j).getCallType().equals(list.get(i).getCallType()))
                                    list.remove(i);
                            }
                        }
                    }
                }

                ContentActivity.isCalllog = 2;
                ContentActivity.listCalllog = list;
                //Toast.makeText(context, list.size() + " will be saved into phone", Toast.LENGTH_SHORT).show();
                //insertCallLogs(list);
                return list;

            default:
                return null;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList getAllMessages(ArrayList<ListItemSms> list, String type) {
        Cursor c = context.getContentResolver().query(Telephony.Sms.CONTENT_URI,
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
                    String date = smsDate;

                    String typeWrited = "";
                    String tpe = String.valueOf(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)));
                    switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                        case Telephony.Sms.MESSAGE_TYPE_INBOX:
                            typeWrited = "inbox";
                            break;
                        case Telephony.Sms.MESSAGE_TYPE_SENT:
                            typeWrited = "sent";
                            break;
                        case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                            typeWrited = "outbox";
                            break;
                    }

                    listSms.add(new ListItemSms(username, number, body, date, tpe));
                    c.moveToNext();
                }
            }
        } else
            Toast.makeText(context, "No message to show!", Toast.LENGTH_SHORT).show();
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

                ContentActivity.isSMS = 1;
                ContentActivity.listSms = listSms;
                //String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/saveContacts.php?type=sms";
                //Toast.makeText(context, listSms.size() + " insert to db", Toast.LENGTH_SHORT).show();
                //new AsynTaskPost(listSms, context, "sms").execute(url);
                return listSms;

            case "get":
                for (int j = 0; j < listSms.size(); j++) {
                    for (int i = 0; i < list.size(); i++) {
                        if (listSms.get(j).getNum().equals(list.get(i).getNum())) {
                            if (listSms.get(j).getBody().equals(list.get(i).getBody())) {
                                if (listSms.get(j).getType().equals(list.get(i).getType()))
                                    list.remove(i);
                            }
                        }
                    }
                }

                ContentActivity.isSMS = 2;
                ContentActivity.listSms = list;
                //Toast.makeText(context, list.size() + " will be saved into phone", Toast.LENGTH_SHORT).show();
                //insertContacts(list);
                return list;

            default:
                return null;
        }

    }

}
