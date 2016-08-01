package puc.iec.dadm.tpandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences preferences = getSharedPreferences("email", MODE_PRIVATE);

    private String email = preferences.getString("email",null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (email != null) {
            Intent intent = new Intent(MainActivity.this, OffersActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);
        }

        Button login = (Button) findViewById(R.id.btLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Button loginGoogle = (Button) findViewById(R.id.btLoginGoogle);

        loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OAuthActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        email = preferences.getString("email",null);

        Log.i(">>>>> rEMAIL",email);

        if (email != null) {
            Intent intent = new Intent(MainActivity.this, OffersActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        email = preferences.getString("email",null);

        Log.i(">>>>> sEMAIL",email);

        if (email != null) {
            Intent intent = new Intent(MainActivity.this, OffersActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);
        }
    }
}
