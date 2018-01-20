package ng.com.starthub.emmanuel.autohomewithwifiesp8266;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public static final int DETAIL_REQUEST_CODE = 1001;
    private final static String TAG = LoginActivity.class.getSimpleName();
    SharedPreferences sharedPreferences; //= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    private Button btnLogin;
    private EditText txtUserName;
    private EditText txtPassword;
    private String shareUsername;
    private String sharePassword;
    private String status;
    private TextView login_Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //try {
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.mipmap.ic_launcher_round);
            actionBar.setTitle(R.string.app_name);
        }

        login_Status = (TextView) findViewById(R.id.Login_Status);
        btnLogin = (Button) findViewById(R.id.Login_button);
        txtUserName = (EditText) findViewById(R.id.userName);
        txtPassword = (EditText) findViewById(R.id.password);

        // get value from existing preference

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        status = sharedPreferences.getString("status", "");

        if (!status.equals("Set")) {
            login_Status.setText("Create Account");
            btnLogin.setText("Create");
        }
        /*}catch (Exception ex){
            Log.d("Main_BT", ex.toString());
        }*/
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUsername = sharedPreferences.getString("username", "");
                sharePassword = sharedPreferences.getString("password", "");
                status = sharedPreferences.getString("status", "");
                Log.d(TAG, "Username: " + shareUsername);
                Log.d(TAG, "Password: " + sharePassword);
                Log.d(TAG, "Status: " + status);

                if ((txtUserName.getText().toString().isEmpty()) || (txtPassword.getText().toString().isEmpty())) {
                    if (!status.equals("Set")) {
                        Toast.makeText(getBaseContext(), "Enter your prefer Username and Password.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Enter your correct Username and Password.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (!status.equals("Set")) {
                        Log.d(TAG, "Preferences not set");
                        // put value in preference on button click
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", txtUserName.getText().toString());
                        editor.putString("password", txtPassword.getText().toString());
                        editor.putString("status", "Set");
                        editor.apply();
                        btnLogin.setText("Login");
                        txtUserName.getText().clear();
                        txtPassword.getText().clear();
                        txtUserName.requestFocus();
                        login_Status.setText("Sign In");
                        Log.d(TAG, "Preferences committed");
                    } else {
                        if (status.equals("Set")) {
                            Log.d(TAG, "Preferences set");
                            if (!(txtUserName.getText().toString().trim().equals(shareUsername.toString())) && !(txtPassword.getText().toString().trim().equals(sharePassword.toString()))) {
                                Toast.makeText(getBaseContext(), "Wrong Username or Password!", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Wrong");
                            } else {
                                txtUserName.getText().clear();
                                txtPassword.getText().clear();
                                Log.d(TAG, "Loading main");
                                loadMain();
                            }
                        }
                    }
                }

            }
        });
    }

    private void loadMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, DETAIL_REQUEST_CODE);
    }
}
