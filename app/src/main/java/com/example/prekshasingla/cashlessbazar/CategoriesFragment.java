package com.example.prekshasingla.cashlessbazar;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.text.Line;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    List<Category> categoryList;
    CategoryAdapter adapter;
    private NavController navController;

    public CategoriesFragment() {
        // Required empty public constructor
        categoryList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_categories, container, false);
        rootview.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        navController=Navigation.findNavController(getActivity(),R.id.fragment);

        RecyclerView recyclerView = rootview.findViewById(R.id.recycler);
        adapter = new CategoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        tokenRequest();
        return rootview;
    }

    private void tokenRequest() {
//        dialog.show();
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api2.cashlessbazar.com/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        dialog.dismiss();
                        if (response != null && !response.equals("")) {

                            try {
                                JSONObject tokenResponse = new JSONObject(response);
                                String token = tokenResponse.getString("access_token");
                                if (token != null)
                                    getCategories(token);
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
//                        dialog.dismiss();
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

    private void getCategories(final String token) {
//        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlCategories + "PageNumber=1&PageSize=50",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        dialog.dismiss();
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {

                                JSONArray dataArray = responseObject.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject data = dataArray.getJSONObject(i);
                                    Category category = new Category();
                                    category.name = data.getString("name");
                                    category.id = data.getString("id");
                                    category.subCategories = new HashMap<>();
                                    JSONArray sub = data.getJSONArray("subcategory_data");
                                    for (int j = 0; j < sub.length(); j++) {
                                        JSONObject object = sub.getJSONObject(j);
                                        category.subCategories.put(object.getString("id"), object.getString("name"));
                                    }
                                    categoryList.add(category);

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
//                        dialog.dismiss();
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

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryAdapterViewHolder> {


        private CategoryAdapterViewHolder holder;

        @NonNull
        @Override
        public CategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            holder = new CategoryAdapterViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapterViewHolder holder, int position) {
            final Category category = categoryList.get(position);
            holder.name.setText(category.name);
            holder.subLayout.removeAllViews();
            Iterator it = category.subCategories.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                TextView subCategory = (TextView) getActivity().getLayoutInflater()
                        .inflate(R.layout.textview_subcategory,null,false);
                subCategory.setText(pair.getValue() + "");
                subCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PostRequirementFragment.categoryId = category.id;
                        PostRequirementFragment.categoryName=category.name;
                        PostRequirementFragment.subCategoryId=pair.getKey()+"";
                        PostRequirementFragment.subCategoryName=pair.getValue()+"";
                        navController.navigateUp();
                    }
                });
                holder.subLayout.addView(subCategory);
            }
            holder.subLayout.setVisibility(View.GONE);
            if(category.subCategories.size()==0){
                holder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PostRequirementFragment.categoryId = category.id;
                        PostRequirementFragment.categoryName=category.name;
                        PostRequirementFragment.subCategoryId=null;
                        PostRequirementFragment.subCategoryName=null;
                        navController.navigateUp();
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        public class CategoryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            public TextView name;
            public LinearLayout subLayout;

            public CategoryAdapterViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                subLayout = itemView.findViewById(R.id.sub_layout);
                name.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                subLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    class Category {
        String id, name;
        HashMap<String, String> subCategories;
    }

}


