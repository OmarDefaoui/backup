package com.omardev.backuptest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Omar on 27/03/2018.
 */

public class AsynTaskPost extends AsyncTask<String, String, String> {

    Context context;
    String type;
    ArrayList<ListItemContact> listItemContacts = new ArrayList<>();
    ArrayList<ListItemCallLog> listCalllog = new ArrayList<>();
    ArrayList<ListItemSms> listSms = new ArrayList<>();
    ArrayList<String> listInfoAccount = new ArrayList<>();
    ArrayList<String> listInfoLogin = new ArrayList<>();

    public AsynTaskPost(Context context, String type, ArrayList<ListItemContact> listItemContacts) {
        this.context = context;
        this.type = type;
        this.listItemContacts = listItemContacts;
    }

    public AsynTaskPost(Context context, ArrayList<ListItemCallLog> listCalllog, String type) {
        this.context = context;
        this.type = type;
        this.listCalllog = listCalllog;
    }

    public AsynTaskPost(ArrayList<ListItemSms> listSms, Context context, String type) {
        this.context = context;
        this.type = type;
        this.listSms = listSms;
    }

    public AsynTaskPost(String type, ArrayList<String> listInfoAccount, Context context) {
        this.context = context;
        this.type = type;
        this.listInfoAccount = listInfoAccount;
    }

    public AsynTaskPost(String type, Context context, ArrayList<String> listInfoLogin) {
        this.context = context;
        this.type = type;
        this.listInfoLogin = listInfoLogin;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            URL url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            Gson gson = new Gson();
            String str = "";
            switch (type) {
                case "contact":
                    str = gson.toJson(listItemContacts).toString();
                    break;
                case "calllog":
                    str = gson.toJson(listCalllog).toString();
                    break;
                case "sms":
                    str = gson.toJson(listSms).toString();
                    break;
                case "createAccount":
                    str = gson.toJson(listInfoAccount).toString();
                    break;
                case "login":
                    str = gson.toJson(listInfoLogin).toString();
                    break;
            }

            DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream());
            printout.writeBytes(str);
            printout.flush();
            printout.close();

            int responseCode = urlConnection.getResponseCode();
            String response = "";
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            in.close();
            //Thread.sleep(3000);
            publishProgress(response);
            urlConnection.disconnect();

        } catch (Exception e) {
            Toast.makeText(context, "in background post", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        try {

            Toast.makeText(context, "" + values[0], Toast.LENGTH_LONG).show();
            if (values[0].equals("account created with succes") || values[0].startsWith("login succes: ")) {
                SharedPreferences preferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                if (values[0].equals("account created with succes")) {
                    editor.putString("username", listInfoAccount.get(0));
                    editor.putString("email", listInfoAccount.get(1));
                } else if (values[0].startsWith("login succes: ")) {
                    editor.putString("username", values[0].split("login succes: ")[1]);
                    editor.putString("email", listInfoLogin.get(0));
                }
                editor.apply();
                ((account) context).finish();
                context.startActivity(new Intent(context, ContentActivity.class));
            }

        } catch (Exception e) {
            Toast.makeText(context, "error in onProgress post", Toast.LENGTH_SHORT).show();
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
}
