package com.example.area;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.area.home.HomePage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CardAdaptater extends
        RecyclerView.Adapter<CardAdaptater.ViewHolder> {

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String token;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView TriggerService;
        public TextView ActionService;
        public TextView Conditions;
        public TextView Actions;
        public Button EditButton;
        public Button DeleteButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            TriggerService = (TextView) itemView.findViewById(R.id.trigger_service);
            ActionService = (TextView) itemView.findViewById(R.id.action_service);
            Conditions = (TextView) itemView.findViewById(R.id.conditions);
            Actions = (TextView) itemView.findViewById(R.id.actions);

            DeleteButton = (Button) itemView.findViewById(R.id.delete_button);
        }
    }

    // Store a member variable for the contacts
    private List<Card> mCards;

    // Pass in the contact array into the constructor
    public CardAdaptater(List<Card> cards) {
        mCards = cards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.cardview, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card card = mCards.get(position);

        token = card.get_token();

        TextView triggerService = holder.TriggerService;
        TextView actionService = holder.ActionService;
        TextView conditions = holder.Conditions;
        TextView actions = holder.Actions;

        triggerService.setText(card.get_TriggerService());
        actionService.setText(card.get_ActionService());
        conditions.setText(card.get_Conditions());
        actions.setText(card.get_Actions());

        Button delete = holder.DeleteButton;

        delete.setOnClickListener(v -> {
            JSONObject area = new JSONObject();
            try {
                area.put("id", card.get_id());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, area.toString());

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/api/service/delete")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("ERR ", "REQ FAILED **********************************************");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    } else {
                        Log.e("TEST", "**********************************************" + response.body().string());
                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }
}