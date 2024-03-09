package com.example.minigpt;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
//import android.widget.Toast;

import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.google.ai.client.generativeai.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView welcomeText;
    EditText queryHolder;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
//    public static final MediaType JSON
//            = MediaType.get("application/json; charset=utf-8");
//    OkHttpClient client = new OkHttpClient.Builder()
//            .readTimeout(60, TimeUnit.SECONDS)
//            .build();
    String apiKey = "";
    GenerativeModel gm = new GenerativeModel( "gemini-pro",apiKey);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeText = findViewById(R.id.welcome_text);
        queryHolder = findViewById(R.id.query_holder);
        sendButton = findViewById(R.id.send_btn);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        sendButton.setOnClickListener((v) -> {
            String query = queryHolder.getText().toString().trim();
            addToChat(query,Message.SENT_BY_ME);
            queryHolder.setText("");
            //callAPI(query);
            callGemini(query);
            welcomeText.setVisibility(View.GONE);
        });

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

//    void callAPI(String query) {
//        //okhttp
//        messageList.add(new Message("Typing... ", Message.SENT_BY_BOT));
//
//        JSONObject jsonBody = new JSONObject();
//        JSONObject userMessage = new JSONObject();
//        JSONArray messagesArray = new JSONArray();
//
//        try{
//
//            userMessage.put("role", "user");
//            userMessage.put("content", query);
//            messagesArray.put(userMessage);
//            jsonBody.put("model", "gpt-3.5-turbo");
//            jsonBody.put("messages", messagesArray);
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//
//        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
//        Request request = new Request.Builder()
//                .url("https://api.openai.com/v1/chat/completions")
//                .header("Authorization", "Bearer ")
//                .post(body)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                addResponse("Failed to load response due to " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    JSONObject jsonObject;
//                    try {
//                          jsonObject = new JSONObject(response.body().string());
//                          JSONArray jsonArray = jsonObject.getJSONArray("choices");
//                          String result = jsonArray.getJSONObject(0)
//                                  .getJSONObject("message")
//                                          .getString("content");
//                          addResponse(result.trim());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    addResponse("Failed to load response due to " + response.body().string());
//                }
//            }
//        });
//    }

    void callGemini(String query){

        messageList.add(new Message("Typing... ", Message.SENT_BY_BOT));

        Content content = new Content.Builder()
                .addText(query)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                addResponse(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
    }
}
