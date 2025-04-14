package com.example.image_classification;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import com.example.image_classification.MainActivity;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity2 extends AppCompatActivity {

    String input;

    TextView answer;
    Button btn;
    ProgressBar progressBar;

    private final String API_KEY = "AIzaSyCVpQJI3BmRpJNW7EIXfuPEeAXxAGxDeCU";
    private final String MODEL_ID = "models/gemini-2.0-flash-exp";

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        answer = findViewById(R.id.answer);
        progressBar = findViewById(R.id.progressBar);
        input = getIntent().getStringExtra("prediction_result");
        String word ="Get me the possible ways to recycle "+input+"in short and also get me the nearby location for its disposal";
        fetchDefinition(word);


    }

    private void fetchDefinition(String prompt) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        answer.setText("");

        String url = "https://generativelanguage.googleapis.com/v1beta/" + MODEL_ID + ":generateContent?key=" + API_KEY;

        try {
            JSONObject part = new JSONObject();
            part.put("text", prompt);

            JSONObject content = new JSONObject();
            content.put("parts", new JSONArray().put(part));

            JSONObject body = new JSONObject();
            body.put("contents", new JSONArray().put(content));

            RequestBody requestBody = RequestBody.create(body.toString(), JSON);

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(ProgressBar.GONE);
                        answer.setText("Request failed: " + e.getMessage());
                    });
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    runOnUiThread(() -> progressBar.setVisibility(ProgressBar.GONE));

                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> answer.setText("Unexpected response: " + response.code()));
                        return;
                    }

                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray candidates = json.getJSONArray("candidates");
                        JSONObject candidate = candidates.getJSONObject(0);
                        JSONArray parts = candidate.getJSONObject("content").getJSONArray("parts");
                        String result = parts.getJSONObject(0).getString("text");

                        new Handler(Looper.getMainLooper()).post(() -> answer.setText(result));
//                        answer.setMovementMethod(LinkMovementMethod.getInstance());

                    } catch (Exception e) {
                        runOnUiThread(() -> answer.setText("Parse error: " + e.getMessage()));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(ProgressBar.GONE);
            answer.setText("Error: " + e.getMessage());
        }
    }

}
