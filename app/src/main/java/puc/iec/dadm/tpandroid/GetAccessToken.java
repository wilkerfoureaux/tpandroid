package puc.iec.dadm.tpandroid;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by wilker on 30/07/16.
 */
public class GetAccessToken {
    static InputStream inputStream = null;
    static JSONObject jsonObject = null;
    static String json = "";

    public GetAccessToken() {

    }

    public JSONObject getToken(String token_url, String token, String client_id, String redirect_uri, String grant_type) {
    // public JSONObject getToken(String token_url, String token, String client_id, String client_secret, String redirect_uri, String grant_type) {

        OkHttpClient httpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .addEncoded("code", token)
                .addEncoded("client_id", client_id)
                // .addEncoded("client_secret", client_secret)
                .addEncoded("redirect_uri", redirect_uri)
                .addEncoded("grant_type", grant_type)
                .build();

        Request request = new Request.Builder()
                .url(token_url)
                .post(body)
                .build();

        try {
            json = httpClient.newCall(request).execute().body().string();
            Log.i(">>>>>>>>>> JSONStr", json);
            jsonObject = new JSONObject(json);

        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}