package puc.iec.dadm.tpandroid;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static String CLIENT_ID = "877636504113-2vt68qp3inp1m6qo6rnibkmjmoi4g4uo.apps.googleusercontent.com";
    // private static String CLIENT_SECRET = "_CnoTUVy5jQ9qrmi6ltUwjry";
    private static String REDIRECT_URI = "puc.iec.dadm.tpandroid:/oauth-tpandroid";
    private static String OAUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static String TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";
    private static String GRANT_TYPE = "authorization_code";
    private static String OAUTH_SCOPE = "email profile";

    private WebView webView;
    private Button btAuth;
    private SharedPreferences preferences;
    private TextView access;

    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private String email;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("AppPref", MODE_PRIVATE);
        email = preferences.getString("email",null);

        // FirebaseMessaging.getInstance().subscribeToTopic("test");
        // FirebaseInstanceId.getInstance().getToken();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        access = (TextView) findViewById(R.id.access);
        btAuth = (Button) findViewById(R.id.auth);

        if ( email != null ){
            access.setText( email );
            btAuth.setText("Authenticated");
            btAuth.setActivated(false);
        }


        btAuth.setOnClickListener(new View.OnClickListener() {

            Dialog auth_dialog;

            @Override
            public void onClick(View v) {

                if (getSharedPreferences("email", MODE_PRIVATE) == null) {
                    pickUserAccount();
                }

                auth_dialog = new Dialog(MainActivity.this);

                auth_dialog.setContentView(R.layout.activity_auth_dialog);

                webView = (WebView) auth_dialog.findViewById(R.id.authwebview);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(OAUTH_URL + "?redirect_uri=" + REDIRECT_URI + "&response_type=code&client_id=" + CLIENT_ID + "&scope=" + OAUTH_SCOPE);

                auth_dialog.show();

                webView.setWebViewClient(new WebViewClient() {

                    boolean authComplete = false;

                    String authCode;

                    @Override
                    public void onPageFinished(WebView view, String url) {

                        super.onPageFinished(view, url);

                        Uri u = Uri.parse(url);

                        authCode = u.getQueryParameter("code");

                        if (authCode != null && authComplete != true) {

                            Log.i(">>>>>>>>>> CODE", authCode);

                            authComplete = true;

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("code", authCode);

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("code", authCode);
                            editor.putString("email", email);
                            editor.apply();

                            auth_dialog.dismiss();

                            new GetToken().execute();

                        } else {

                            Toast.makeText(MainActivity.this, "Allowing this app is necessary", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

            } else {
                Toast.makeText(this, "Your e-mail is necessary", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetToken extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog progressDialog;
        private String code;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Contacting Google ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);

            code = preferences.getString("code", null);

            Log.i(">>>>>>>>>> aCODE", code);

            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            GetAccessToken token = new GetAccessToken();

            JSONObject json = token.getToken(TOKEN_URL, code, CLIENT_ID, REDIRECT_URI, GRANT_TYPE);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            progressDialog.dismiss();

            if (jsonObject != null) {

                try {
                    String token = jsonObject.getString("access_token"),
                            expire = jsonObject.getString("expires_in"),
                            refresh = jsonObject.getString("refresh_token");

                    Log.d("Token", token);
                    Log.d("Expires", expire);
                    Log.d("Refreshes", refresh);

                    btAuth.setText("Authenticated");

                    access.setText(jsonObject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}