package com.github.faisal.firebasecloudfunctions;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText title, message;
    OkHttpClient mClient;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = FirebaseInstanceId.getInstance().getToken();

        Log.d("token_id", token);

        mClient = new OkHttpClient();

        String refreshedToken = "ezqKcosAWbY:APA91bEN5-3FNypgqbnreRKWrKVTg_K0oY1V8cXzAr7-pNVkhb_-mibogo5V13SbXJRss4mBTPHf9I0O_k3nHeuSMxAnV1mGV6sizzZ3zjJJ8AYUvaBet2oNglLo_gqDo0_5gCZk9eAPoX31kWCtw9ct6ABfa8ZMHA";

        jsonArray = new JSONArray();
        jsonArray.put(refreshedToken);


    }


    public void sendCall(View view) {
        sendMessage(jsonArray,"Hello","How r u","Http:\\google.com","My Name is Faisal");
    }


    @SuppressLint("StaticFieldLeak")
    public void sendMessage(final JSONArray recipients, final String title, final String body, final String icon, final String message) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
                    notification.put("icon", icon);

                    JSONObject data = new JSONObject();
                    data.put("message", message);
                    root.put("data", notification);
                    root.put("data", data);
                    root.put("registration_ids", recipients);

                    String result = postToFCM(root.toString());
                    Log.d("Main Activity", "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                    Toast.makeText(MainActivity.this, "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {
        String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + "AAAAPXJBzRQ:APA91bHltPn8hNAjtSBaXkChD74q5U0Eof5EFaAGSufXURX8TlQWAd8Ker3qFMPddceM0pDnx_oZ8ZrfIF6r_hMPp3Jw2F67pJS5lcSkGrHJuEFZ1_1z_Cvu-mj9FxiJUf8nFKCLYJm6HBrQne9FnpW28F4tANLQyQ")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

}
