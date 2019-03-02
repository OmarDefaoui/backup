package com.omardev.backuptest;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    int itemViewType;
    ArrayList<ListItemContact> listContact = new ArrayList<>();
    ArrayList<ListItemCallLog> listCalllog = new ArrayList<>();
    ArrayList<ListItemSms> listSMS = new ArrayList<>();

    public RecyclerViewAdapter(int itemViewType, ArrayList<ListItemContact> listContact) {
        this.itemViewType = itemViewType;
        this.listContact = listContact;
    }

    public RecyclerViewAdapter(ArrayList<ListItemCallLog> listCalllog, int itemViewType) {
        this.itemViewType = itemViewType;
        this.listCalllog = listCalllog;
    }

    public RecyclerViewAdapter(ArrayList<ListItemSms> listSMS, int itemViewType, String rien) {
        this.itemViewType = itemViewType;
        this.listSMS = listSMS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(context).inflate(R.layout.row_contact, parent, false);
                return new UploadContact(view);
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.row_calllog, parent, false);
                return new UploadCalllog(view);
            case 2:
                view = LayoutInflater.from(context).inflate(R.layout.row_sms, parent, false);
                return new UploadSMS(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                UploadContact holder0 = (UploadContact) holder;
                holder0.tv_contact_name.setText(listContact.get(position).getName());
                holder0.tv_contact_num.setText(listContact.get(position).getNum());
                holder0.tv_frst_ltr_iv.setText(listContact.get(position).getName().substring(0,1).toUpperCase());
                break;

            case 1:
                UploadCalllog holder1 = (UploadCalllog) holder;
                holder1.tv_call_name.setText(listCalllog.get(position).getName());
                holder1.tv_call_num.setText(listCalllog.get(position).getNum());
                String callType = "";
                switch (Integer.parseInt(listCalllog.get(position).getCallType())) {
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
                holder1.tv_call_type.setText(callType);
                holder1.tv_frst_ltr_iv.setText(listCalllog.get(position).getName().substring(0,1).toUpperCase());
                break;

            case 2:
                UploadSMS holder2 = (UploadSMS) holder;
                holder2.tv_sms_num.setText(listSMS.get(position).getNum());
                holder2.tv_sms_body.setText(listSMS.get(position).getBody());
                String typeWrited = "";
                switch (Integer.parseInt((listSMS.get(position).getType()))) {
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
                holder2.tv_sms_type.setText(typeWrited);
                holder2.tv_frst_ltr_iv.setText("M");
                break;
        }
    }

    @Override
    public int getItemCount() {
        switch (itemViewType) {
            case 0:
                return listContact.size();
            case 1:
                return listCalllog.size();
            case 2:
                return listSMS.size();
            default:
                return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return itemViewType;
    }

    public class UploadContact extends RecyclerView.ViewHolder {

        TextView tv_contact_name, tv_contact_num, tv_frst_ltr_iv;

        public UploadContact(View itemView) {
            super(itemView);
            tv_contact_name = itemView.findViewById(R.id.tv_contact_name);
            tv_contact_num = itemView.findViewById(R.id.tv_contact_num);
            tv_frst_ltr_iv = itemView.findViewById(R.id.tv_frst_ltr_iv);
        }
    }

    public class UploadCalllog extends RecyclerView.ViewHolder {

        TextView tv_call_name, tv_call_num, tv_call_type,tv_frst_ltr_iv;

        public UploadCalllog(View itemView) {
            super(itemView);
            tv_call_name = itemView.findViewById(R.id.tv_call_name);
            tv_call_num = itemView.findViewById(R.id.tv_call_num);
            tv_call_type = itemView.findViewById(R.id.tv_call_type);
            tv_frst_ltr_iv = itemView.findViewById(R.id.tv_frst_ltr_iv);
        }
    }

    public class UploadSMS extends RecyclerView.ViewHolder {

        TextView tv_sms_num, tv_sms_body, tv_sms_type,tv_frst_ltr_iv;

        public UploadSMS(View itemView) {
            super(itemView);
            tv_sms_num = itemView.findViewById(R.id.tv_sms_num);
            tv_sms_body = itemView.findViewById(R.id.tv_sms_body);
            tv_sms_type = itemView.findViewById(R.id.tv_sms_type);
            tv_frst_ltr_iv = itemView.findViewById(R.id.tv_frst_ltr_iv);
        }
    }
}
