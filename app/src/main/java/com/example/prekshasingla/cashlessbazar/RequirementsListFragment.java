package com.example.prekshasingla.cashlessbazar;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequirementsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequirementsListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private ProgressDialog dialog;

    RecyclerView recyclerView;
    RequirementsAdapter adapter;
    List<Requirement> requirementList;


    public RequirementsListFragment() {
        // Required empty public constructor
    }


    public static RequirementsListFragment newInstance(String param1) {
        RequirementsListFragment fragment = new RequirementsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        requirementList=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_requirements_list, container, false);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);

        recyclerView = rootview.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new RequirementsAdapter();
        recyclerView.setAdapter(adapter);

        Button postButton=rootview.findViewById(R.id.post_button);
        if(mParam1.equalsIgnoreCase("looking")){
            postButton.setText("Post Your Requirement");

        }else{
            postButton.setText("Sell Your Product");
        }
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(),PostRequirementActivity.class);
                intent.putExtra("service",mParam1);
                getActivity().startActivity(intent);
            }
        });
        tokenRequest();
        return rootview;
    }

    public void tokenRequest() {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        dialog.show();
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
                                    getList(token);

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
                return "application/json";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void getList(final String token) {
        dialog.show();
        requirementList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlRequirementList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject loginResponse = new JSONObject(response);
                            if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {

                                JSONArray jsonArrayState = loginResponse.getJSONArray("data");

                                for (int i = 0; i < jsonArrayState.length(); i++) {


                                    JSONObject jsonObject = jsonArrayState.getJSONObject(i);
                                    if (jsonObject.getString("serviceName").equalsIgnoreCase(mParam1)) {
                                        Requirement requirement = new Requirement();
                                        requirement.title = jsonObject.getString("title");
                                        requirement.image = jsonObject.getString("image");
                                        requirement.price = jsonObject.getString("price");
                                        requirement.qty = jsonObject.getString("unit");
                                        requirement.location = jsonObject.getString("locationName") + ", " +
                                                jsonObject.getString("cityName");
                                        requirement.adid=jsonObject.getString("adId");
                                        requirementList.add(requirement);
                                    }

                                }

                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
//                                    citybuilder.show();
//                                    Toast.makeText(getActivity(), "Requirement Posted", Toast.LENGTH_SHORT).show();
//                                    getActivity().onBackPressed();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "Some error occurred, please try again later", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Do something with the response
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


//                public String getBodyContentType() {
//                    return "application/x-www-form-urlencoded";
//                }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }

    class RequirementsAdapter extends RecyclerView.Adapter<RequirementsAdapter.RequirementsAdapterViewHolder> {

        private RequirementsAdapterViewHolder holder;

        @NonNull
        @Override
        public RequirementsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_requirement_list, parent, false);
            holder = new RequirementsAdapterViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RequirementsAdapterViewHolder holder, int position) {

            Requirement requirement=requirementList.get(position);
            holder.title.setText(requirement.title);
            holder.location.setText(requirement.location);
            holder.quantity.setText("Quantity: "+requirement.qty);
            holder.price.setText("Price: "+requirement.price);
            Picasso.with(getActivity()).load(requirement.image).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return requirementList.size();
        }


        public class RequirementsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView image;
            TextView title,location,price,quantity;

            public RequirementsAdapterViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
                title=itemView.findViewById(R.id.title);
                location=itemView.findViewById(R.id.location);
                price=itemView.findViewById(R.id.price);
                quantity=itemView.findViewById(R.id.quantity);

                image.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),RequirementDetail.class);
                intent.putExtra("adid",requirementList.get(getAdapterPosition()).adid);
                getActivity().startActivity(intent);
            }
        }
    }

    class Requirement {
        String title, image, price, qty, location,adid;

    }
}
