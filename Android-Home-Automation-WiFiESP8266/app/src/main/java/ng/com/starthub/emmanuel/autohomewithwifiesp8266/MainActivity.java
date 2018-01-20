package ng.com.starthub.emmanuel.autohomewithwifiesp8266;


import android.annotation.SuppressLint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    //Device statue]s flag
    private static boolean deviceStatusFlag = false;
    private HttpAsyncTask HAT = null;
    //private Button allOnButton ;
    //private Button allOffButton;
    private EditText editIp;
    CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (deviceStatusFlag) {
                String dat = null;
                int id = buttonView.getId();
                switch (id) {
                    case R.id.device1_switch:
                        if (isChecked) {
                            dat = "0";
                        } else {
                            dat = "1";
                        }
                        try {
                            dat = URLEncoder.encode(dat, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.device2_switch:
                        if (isChecked) {
                            dat = "2";
                        } else {
                            dat = "3";
                        }
                        try {
                            dat = URLEncoder.encode(dat, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;


                    case R.id.device3_switch:
                        if (isChecked) {
                            dat = "4";
                        } else {
                            dat = "5";
                        }
                        try {
                            dat = URLEncoder.encode(dat, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.device4_switch:
                        if (isChecked) {
                            dat = "6";
                        } else {
                            dat = "7";
                        }
                        try {
                            dat = URLEncoder.encode(dat, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;

                }
                String serverIP = editIp.getText().toString() + ":80";
                final String p = "http://" + serverIP + "?dev=" + dat;

                HttpAsyncTask HAT = new HttpAsyncTask();
                HAT.execute(p);
                Log.d(TAG, "... device data send... " + dat + ": " + isChecked);
            } else {
                Log.d(TAG, "... device  status confirm... ");

            }
        }


    };
    private TextView textInfo1, textInfo2;
    private Switch device_1_switch;
    private Switch device_2_switch;
    private Switch device_3_switch;
    private Switch device_4_switch;

    //For HttpAsync Functions: sending requests and receiving responses
    public static String httpRequestResponse(String url) {
        StringBuilder sb = null;
        BufferedReader reader = null;
        String serverResponse = null;
        Log.d(TAG, "Connection...");
        try {
            URL urll = new URL(url);
            Log.d(TAG, urll.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setConnectTimeout(300);
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                Log.d(TAG, "Status good...");
                sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            }
            urlConnection.disconnect();
            if (sb != null)
                serverResponse = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return serverResponse;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.mipmap.ic_launcher_round);
            actionBar.setTitle(R.string.app_name);
        }


        editIp = (EditText) findViewById(R.id.ip);
        device_1_switch = findViewById(R.id.device1_switch);
        device_2_switch = findViewById(R.id.device2_switch);
        device_3_switch = findViewById(R.id.device3_switch);
        device_4_switch = findViewById(R.id.device4_switch);

        textInfo1 = (TextView) findViewById(R.id.info1);
        textInfo2 = (TextView) findViewById(R.id.info2);

        // allOnButton = findViewById(R.id.all_on_btn);
        //allOffButton = findViewById(R.id.all_off_btn);

        device_1_switch.setOnCheckedChangeListener(switchListener);
        device_2_switch.setOnCheckedChangeListener(switchListener);
        device_3_switch.setOnCheckedChangeListener(switchListener);
        device_4_switch.setOnCheckedChangeListener(switchListener);

        try {

            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            String ipAdd = null;
            // String ipAddress = null;
            if (wm != null) {

                WifiInfo wmInfo = wm.getConnectionInfo();
                //int ip = wmInfo.getIpAddress();
                ipAdd = intToInetAddress(wm.getDhcpInfo().serverAddress).toString();
                //ipAddress = String.format("%d.%d.%d.%d",(ip & 0xff),  (ip >> 8 & 0xff),(ip >> 16 & 0xff),(ip >> 24 & 0xff));
            }
            if (ipAdd != null) {
                editIp.setText(ipAdd.replace("/", ""));
            }
            Log.d(TAG, "IP Address is: " + ipAdd);
            // Log.d(TAG, "IP Address is: " + ipAddress);
        } catch (Exception ex) {
            // Log.d(TAG, "unable to get the ip " );
            ex.printStackTrace();
        }






       /* allOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //turnAllOnOff(false);
            }

        });

        allOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //turnAllOnOff(true);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (editIp.getText() != null) {
            String serverIP = editIp.getText().toString() + ":80";
            final String p = "http://" + serverIP + "?dev=" + 8;

            HttpAsyncTask HAT = new HttpAsyncTask();
            HAT.execute(p);
            Log.d(TAG, "Confirm device status...");
        }
    }

    private InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress), (byte) (0xff & hostAddress >> 8), (byte) (0xff & hostAddress >> 16), (byte) (0xff & hostAddress >> 24)};
        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        deviceStatusFlag = false;
    }

    private void turnAllOnOff(boolean state) {

        String dat;
        if (state) {
            device_1_switch.setChecked(true);
            device_2_switch.setChecked(true);
            device_3_switch.setChecked(true);
            device_4_switch.setChecked(true);
        } else {
            device_1_switch.setChecked(false);
            device_2_switch.setChecked(false);
            device_3_switch.setChecked(false);
            device_4_switch.setChecked(false);
        }

      /*  if(state){
            allOnButton.setClickable(false);
            allOffButton.setClickable(true);
        }else {
            allOnButton.setClickable(true);
            allOffButton.setClickable(false);
        }*/


    }

    @SuppressLint("StaticFieldLeak")
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        /*private HttpAsyncTask NextTask = null;
        String server;

        HttpAsyncTask(String server){
            this.server = server;
        }


        public void queneTask(HttpAsyncTask task){
            if(NextTask == null){
                NextTask = task;
            }
            else{
                NextTask.queneTask(task);
            }
        }*/

        @Override
        protected String doInBackground(String... urls) {

            return httpRequestResponse(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                if (deviceStatusFlag) {
                    textInfo2.setText(result);
                    Log.d(TAG, "Finish Executed!!!!!!");
                } else {
                    Log.d(TAG, "The device status: " + result);
                    if (result.length() > 3) {
                        char[] st = result.toCharArray();
                        if (st[0] == '1') {
                            device_1_switch.setChecked(true);
                        } else {
                            device_1_switch.setChecked(false);
                        }

                        if (st[1] == '1') {
                            device_2_switch.setChecked(true);
                        } else {
                            device_2_switch.setChecked(false);
                        }

                        if (st[2] == '1') {
                            device_3_switch.setChecked(true);
                        } else {
                            device_3_switch.setChecked(false);
                        }

                        if (st[3] == '1') {
                            device_4_switch.setChecked(true);
                        } else {
                            device_4_switch.setChecked(false);
                        }

                    }
                    deviceStatusFlag = true;
                }

           /* if(NextTask != null){
                NextTask.execute();
            }*/

            }
        }
    }
}