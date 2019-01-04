package com.example.prekshasingla.cashlessbazar;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostRequirementFragment extends Fragment {


    private NavController navController;
    public static String categoryId=null,categoryName=null,subCategoryId=null,subCategoryName=null;
    List<String> cities;
    List<String> citiesId;
    List<String> localities;
    List<String> localitiesId;

    EditText city, locality;
    String cityId, localityId;
    int POSTREQUIREMENT=1;
    int CITYLIST=2;
    int LOCALITYLIST=3;

    public PostRequirementFragment() {
        cities=new ArrayList<String>();
        citiesId=new ArrayList<String>();
        localities=new ArrayList<String>();
        localitiesId=new ArrayList<String>();
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_post_requirement, container, false);
        navController=Navigation.findNavController(getActivity(),R.id.fragment);
        EditText category= rootView.findViewById(R.id.requirement_category);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.categoriesFragment);
            }
        });

        city=rootView.findViewById( R.id.requirement_city);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tokenRequest(CITYLIST,null);
            }
        });

        locality=rootView.findViewById( R.id.requirement_locality);

        Button postButton=rootView.findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tokenRequest(POSTREQUIREMENT,null);
            }
        });


        return rootView;
    }

    public void tokenRequest(final int type,final String cityId) {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api2.cashlessbazar.com/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null && !response.equals("")) {

                            try {
                                JSONObject tokenResponse = new JSONObject(response);
                                String token = tokenResponse.getString("access_token");
                                if (token != null)

                                    if(type==POSTREQUIREMENT)
                                     postRequirement(token);
                                    else if(type==CITYLIST)
                                        getCityList(token);
                                    else if (type==LOCALITYLIST)
                                        getLocalityList(token,cityId);


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

    @Override
    public void onResume() {
        super.onResume();
    }




    public void postRequirement(final String token) {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Regno", 2);
            jsonBody.put("Title", "Testing Request 2");
            JSONObject categoriesObject=new JSONObject();
            categoriesObject.put("CategoryId",6);
            categoriesObject.put("CategoryName","Business Products and Services");
            categoriesObject.put("SubCategoryId",2);
            categoriesObject.put("SubCategoryName","Advertising and Marketing");
            jsonBody.put("Categories",categoriesObject);
            jsonBody.put("Description","Testing Request Description");
            jsonBody.put("Unit",4);
            jsonBody.put("Quantity",2);
            jsonBody.put("BugetPrice",40.00);
            jsonBody.put("DeliveryPreference","Testing Shipping");
            jsonBody.put("City",2);
            jsonBody.put("Locality",3);

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlPostRequirement,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject loginResponse = new JSONObject(response);
                                if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {

                                    Toast.makeText(getActivity(), "Requirement Posted", Toast.LENGTH_SHORT).show();
                                    getActivity().onBackPressed();
                                }
                                else{
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
                        }
                    }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
                    params.put("Authorization", "bearer " + token);
                    return params;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded";
                }

            };
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getCityList(final String token) {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();

        try {




            StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlCityList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject loginResponse = new JSONObject(response);
                                if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {

                                    JSONArray jsonArrayState=loginResponse.getJSONArray("data");

                                    for(int i=0; i<jsonArrayState.length();i++){

                                        JSONObject jsonObject=jsonArrayState.getJSONObject(i);
                                        JSONArray jsonArraycity=jsonObject.getJSONArray("Locations");
                                        for(int j=0; j<jsonArraycity.length();j++){
                                            JSONObject jsonObject1=jsonArraycity.getJSONObject(j);
                                            cities.add(jsonObject1.getString("Name"));
                                            citiesId.add(jsonObject1.getString("Id"));

                                        }
                                    }


                                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                                    builderSingle.setTitle("Select city -");

                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.select_dialog_post_requirement);
                                    arrayAdapter.addAll(cities);

                                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            city.setText(strName);
                                            cityId=citiesId.get(which);
                                            getLocalityList(token,citiesId.get(which));
                                            AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                                            builderInner.setMessage(strName);
                                            builderInner.setTitle("Your selected city is");
                                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builderInner.show();
                                        }
                                    });
                                    builderSingle.show();
//                                    Toast.makeText(getActivity(), "Requirement Posted", Toast.LENGTH_SHORT).show();
//                                    getActivity().onBackPressed();
                                }
                                else{
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
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void getLocalityList(final String token, final String cityId) {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();

        try {


            StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlLocalityList+cityId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject loginResponse = new JSONObject(response);
                                if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {

                                    JSONArray jsonArrayState=loginResponse.getJSONArray("data");

                                    for(int i=0; i<jsonArrayState.length();i++){

                                        JSONObject jsonObject=jsonArrayState.getJSONObject(i);
                                        localities.add(jsonObject.getString("Name"));
                                        localitiesId.add(jsonObject.getString("Id"));


                                    }



                                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                                    builderSingle.setTitle("Select locality -");

                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.select_dialog_post_requirement);
                                    arrayAdapter.addAll(localities);

                                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            locality.setText(strName);
                                            localityId=localitiesId.get(which);
                                            AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                                            builderInner.setMessage(strName);
                                            builderInner.setTitle("Your selected locality is");
                                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,int which) {

                                                    dialog.dismiss();
                                                }
                                            });
                                            builderInner.show();
                                        }
                                    });
                                    builderSingle.show();
//                                    Toast.makeText(getActivity(), "Requirement Posted", Toast.LENGTH_SHORT).show();
//                                    getActivity().onBackPressed();
                                }
                                else{
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
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



}
