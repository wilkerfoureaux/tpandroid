package puc.iec.dadm.tpandroid;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Auth_dialog extends AppCompatActivity {

    public Auth_dialog(Context context){
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_dialog);
    }
}
