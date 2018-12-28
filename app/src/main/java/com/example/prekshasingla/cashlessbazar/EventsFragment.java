package com.example.prekshasingla.cashlessbazar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;


public class EventsFragment extends Fragment {


    Context mContext;
    List<Event> eventList;
    private RecyclerView recycler;
    EventsAdapter adapter;
    ProgressDialog dialog;

    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait");
        mContext = getActivity();
        eventList = new ArrayList<>();

        recycler = rootView.findViewById(R.id.recycler);
        adapter = new EventsAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recycler.setAdapter(adapter);
        tokenRequest();


        return rootView;
    }

    public void tokenRequest() {
        dialog.show();
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api2.cashlessbazar.com/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dialog.dismiss();
                        if (response != null && !response.equals("")) {

                            try {
                                JSONObject tokenResponse = new JSONObject(response);
                                String token = tokenResponse.getString("access_token");
                                if (token != null)
                                    getEvents(token);
                                else
                                    Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else
                            Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();

                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", "developer");
                params.put("password", "SPleYwIt");
                params.put("grant_type", "password");

                return params;
            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
////                params.put("Content-Type","application/x-www-form-urlencoded");
//                //params.put("Authorization","bearer kZnREUlqOg4CSoqmN-fvrR53Gyp6JGUG9VQh-w4J9fu0ZwAVSdsJNkzA00bw-ZsOWX6ZTuEOxCGoGqxEJz_xk-PXvZ3UnI0zEmjCbmkvsA8cyFzvtRVtpbFFNwo5SWh85D1MtVHIaKBWzJur14LQjCuFW2WX87B-UsyDZbxmgMSdxJbqgiD3cVKipsMThQJDtM6ZM1-V1OM-rL75O0t6r3Ew36Ve6HkebmcKKyrssRJeP4rgyD9m3prKJs5lr_pFTRhkYq2hi07pcIjwCet1wRe9NQo4k8xp9FF5n4U-1gScdP4JXPoikp4HG9QAPrm5");
//                return params;
//            }

            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void getEvents(final String token) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlGetEvents,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {

                                JSONArray eventsArray = responseObject.getJSONArray("Events");
                                for (int i = 0; i < eventsArray.length(); i++) {
                                    JSONObject data = eventsArray.getJSONObject(i);
                                    Event event = new Event();
                                    String date[] = data.getString("eventstartdate").split(" ");
                                    event.day = date[0];
                                    event.month = date[1].substring(0, 3);
                                    event.name = data.getString("title");
                                    JSONArray costs = data.getJSONArray("ticketcost");
                                    event.price = ((JSONObject) costs.get(0)).getString("price");
                                    event.image = data.getString("bannerimage");
                                    event.location = data.getString("eventvenue");
                                    event.payment_url = data.getString("paymentURL");
                                    eventList.add(event);

                                }
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        dialog.dismiss();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Authorization", "bearer " + token);
                return params;
            }

            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsAdapterViewHolder> {

        private EventsAdapterViewHolder holder;

        @NonNull
        @Override
        public EventsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
            holder = new EventsAdapterViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull EventsAdapterViewHolder holder, int position) {
            Event event = eventList.get(position);
            holder.name.setText(event.name);
            Picasso.with(mContext).load(event.image).into(holder.image);
            holder.day.setText(event.day);
            holder.month.setText(event.month);
            holder.location.setText(event.location);
            holder.price.setText(mContext.getResources().getString(R.string.rupee) + " " + Math.round(Float.parseFloat(event.price)) + " onwards");
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public class EventsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView image;
            public TextView name, day, month, price, location, book;

            public EventsAdapterViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.event_image);
                name = (TextView) itemView.findViewById(R.id.event_name);
                day = itemView.findViewById(R.id.event_day);
                month = itemView.findViewById(R.id.event_month);
                price = (TextView) itemView.findViewById(R.id.event_price);
                location = itemView.findViewById(R.id.event_location);
                book = itemView.findViewById(R.id.book);
                image.setOnClickListener(this);
                book.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                Bundle args = new Bundle();
                args.putString("event_name", eventList.get(getAdapterPosition()).name);
                args.putString("event_url", eventList.get(getAdapterPosition()).payment_url);
                navController.navigate(R.id.eventDetailFragment, args);
            }
        }
    }

    class Event {
        String name, price, location, image, day, month, payment_url;

    }
}
