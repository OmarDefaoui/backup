package com.omardev.backuptest;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DrawerLayout drawer;
    FloatingActionButton fab;
    public static boolean isUpload = true;
    public static int tabSelected = 0;
    public static String username;

    public static ArrayList<ListItemContact> listContact = new ArrayList<>();
    public static ArrayList<ListItemCallLog> listCalllog = new ArrayList<>();
    public static ArrayList<ListItemSms> listSms = new ArrayList<>();
    public static int isContact = 0, isCalllog = 0, isSMS = 0; //0=>empty , 1=>isUpload(to server) , 2=>isDownload(insert in phone)

    private LinearLayout ll_nav_header;
    private TextView tv_nav_header;
    TabLayout tabLayout;
    boolean dis = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        if (!new checkInternetConnexion(this).isConnectedToInternet()) {
            finish();
            startActivity(new Intent(this, noInternetConnexion.class));
        }
        updateUsername();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = findViewById(R.id.tabs);
        TabPagedFunction();

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_upload);

        final View view = navigationView.getHeaderView(0);
        tv_nav_header = view.findViewById(R.id.tv_nav_header);
        ll_nav_header = view.findViewById(R.id.ll_nav_header);
        ll_nav_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ContentActivity.this, account.class));
            }
        });
        tv_nav_header.setText(username);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigartion_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /**Handler handler = new Handler();
         handler.postDelayed(new Runnable() {
        @Override public void run() {
        Toast.makeText(ContentActivity.this, "ttt", Toast.LENGTH_SHORT).show();
        //tabLayout.getTabAt(1).select();
        //tabLayout.getTabAt(2).select();
        //tabLayout.getTabAt(0).select();
        mViewPager.setCurrentItem(1);
        }
        }, 5000);*/

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUpload) {  //in server and have to be inserted into phone
                    switch (tabSelected) {
                        case 0:
                            if (isContact == 2)
                                insertContacts(listContact);
                            break;
                        case 1:
                            if (isCalllog == 2)
                                insertCallLogs(listCalllog);
                            break;
                        case 2:
                            if (isSMS == 2 && listSms.size() > 0)
                                Toast.makeText(ContentActivity.this, listSms.size() + " msg in server not saved in this phone", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {    //in phone and have to be uploaded to server
                    switch (tabSelected) {
                        case 0:
                            if (isContact == 1 && listContact.size() > 0) {
                                String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/saveContacts.php?type=contact";
                                new AsynTaskPost(ContentActivity.this, "contact", listContact).execute(url);
                                //Toast.makeText(ContentActivity.this, listContact.size() + " inserted to db", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            if (isCalllog == 1 && listCalllog.size() > 0) {
                                String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/saveContacts.php?type=calllog";
                                new AsynTaskPost(ContentActivity.this, listCalllog, "calllog").execute(url);
                                //Toast.makeText(ContentActivity.this, listCalllog.size() + " inserted to db", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 2:
                            if (isSMS == 1 && listSms.size() > 0) {
                                String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/saveContacts.php?type=sms";
                                new AsynTaskPost(listSms, ContentActivity.this, "sms").execute(url);
                                //Toast.makeText(ContentActivity.this, listSms.size() + " inserted to db", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            }
        });

    }

    private void updateUsername() {
        username = getSharedPreferences("account", Context.MODE_PRIVATE).getString("username", "no");
        if (username.equals("no")) {
            finish();
            startActivity(new Intent(this, account.class));
        }
    }

    private void TabPagedFunction() {
        tabSelected = 0;
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tabSelected = 0;
                        UploadContact.tv_indicator.setText(listContact.size() + " contacts");
                        break;
                    case 1:
                        tabSelected = 1;
                        UploadContact.tv_indicator.setText(listCalllog.size() + " calls");
                        break;
                    case 2:
                        tabSelected = 2;
                        UploadContact.tv_indicator.setText(listSms.size() + " sms");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        /**tabLayout.getTabAt(1).select();
         tabLayout.getTabAt(2).select();
         tabLayout.getTabAt(0).select();*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_upload:
                isUpload = true;
                fab.setImageResource(R.drawable.ic_cloud_download);
                TabPagedFunction();
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.nav_download:
                isUpload = false;
                fab.setImageResource(R.drawable.ic_cloud_upload);
                TabPagedFunction();
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.nav_about:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_rate:
                break;
            case R.id.nav_contactdev:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "omarituto@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Hi,\nI ");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case R.id.nav_donation:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/omartuto")));
                break;
            case R.id.nav_fb:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/omaritutoriel/")));
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_share:
                break;
            case R.id.nav_rate:
                break;
            case R.id.nav_donation:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/omartuto")));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class UploadContact extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public UploadContact() {
        }

        public static UploadContact newInstance(int sectionNumber) {
            UploadContact fragment = new UploadContact();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        Context context;
        public static RecyclerView recyclerView;
        public static RecyclerViewAdapter adapter;
        private LinearLayoutManager linearLayoutManager;
        public static TextView tv_indicator;
        String username = ContentActivity.username;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_content, container, false);
            context = inflater.getContext();

            tv_indicator = view.findViewById(R.id.tv_indicator);
            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
            String typeOperation;
            if (isUpload)
                typeOperation = "get";  // show item in server and don't exist in phone
            else typeOperation = "post";    // show item in phone does not exist in server

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 0:
                    getAllContacts(typeOperation);
                    break;
                case 1:
                    getAllCallLog(typeOperation);
                    break;
                case 2:
                    getAllMessages(typeOperation);
                    break;
            }
            return view;
        }

        private void getAllContacts(String type) {
            String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/getContacts.php?username=" + username + "&type=contact";
            new AsynTask(context, "contact", type).execute(url);
        }

        private void getAllCallLog(String type) {
            String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/getContacts.php?username=" +
                    username + "&type=calllog";
            new AsynTask(context, "calllog", type).execute(url);
        }

        private void getAllMessages(String type) {
            String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/getContacts.php?username=" +
                    username + "&type=sms";
            new AsynTask(context, "sms", type).execute(url);
        }
    }


    // A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return UploadContact.newInstance(0);
                case 1:
                    return UploadContact.newInstance(1);
                case 2:
                    return UploadContact.newInstance(2);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }


    //insert to phone from server
    public void insertContacts(ArrayList<ListItemContact> list) {
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
        Toast.makeText(this, "Succes, saved: " + list.size(), Toast.LENGTH_SHORT).show();

    }

    public void insertCallLogs(ArrayList<ListItemCallLog> list) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission requierd", Toast.LENGTH_SHORT).show();
            return;
        }

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
        Toast.makeText(this, "succes, saved: " + list.size(), Toast.LENGTH_SHORT).show();
    }
}
