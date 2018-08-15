package com.github.faisal.firebasecloudfunctions;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private OkHttpClient mClient;
    private TextView tvMyToken;
    private Button btnCalll;
    private EditText edtUserToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMyToken = findViewById(R.id.textVIew_myToken);
        edtUserToken = findViewById(R.id.edt_token);
        btnCalll = findViewById(R.id.btn_call);
        btnCalll.setOnClickListener(this);
        String token = FirebaseInstanceId.getInstance().getToken();
        if (!TextUtils.isEmpty(token)) {
            tvMyToken.setTextIsSelectable(true);
            tvMyToken.setText(token);
            Log.d("token_id", token);
        }

        mClient = new OkHttpClient();


    }




    @SuppressLint("StaticFieldLeak")
    public void sendMessage(final JSONArray userTokenID, final String title, final String body, final String icon, final String message) {

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
                    root.put("registration_ids", userTokenID);

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

    @Override
    public void onClick(View view) {
        String userToken = edtUserToken.getText().toString();
        String imageUrl = "https://cdn.iconscout.com/icon/premium/png-256-thumb/notification-142-647836.png";
        if (!TextUtils.isEmpty(userToken)){
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(userToken);
            sendMessage(jsonArray, "Hello", "How r u", imageUrl, "My Name is Faisal");
        }

    }
}
