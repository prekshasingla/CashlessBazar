package com.example.prekshasingla.cashlessbazar;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import static com.example.prekshasingla.cashlessbazar.PostRequirementActivity.categoryId;
import static com.example.prekshasingla.cashlessbazar.PostRequirementActivity.categoryName;
import static com.example.prekshasingla.cashlessbazar.PostRequirementActivity.subCategoryId;
import static com.example.prekshasingla.cashlessbazar.PostRequirementActivity.subCategoryName;


/**
 * A simple {@link Fragment} subclass.
 */
public class SellProductFragment extends Fragment {

    private NavController navController;
    List<String> cities;
    List<String> citiesId;
    List<String> localities;
    List<String> localitiesId;
    List<String> units;
    List<String> unitIds;


    AppCompatEditText city, locality;
    String cityId, localityId, unitId;
    AppCompatEditText title, description, quantityMin,quantityMax,mrp,offerPrice,shipping,locationInfo;
    int POSTREQUIREMENT = 1;
    int CITYLIST = 2;
    int LOCALITYLIST = 3;

    AppCompatEditText categoryText, unit;
    AlertDialog.Builder citybuilder;
    AlertDialog.Builder localityBuilder;
    ProgressDialog dialog;
    public SellProductFragment() {
        // Required empty public constructor
        cities = new ArrayList<>();
        citiesId = new ArrayList<>();
        localities = new ArrayList<>();
        localitiesId = new ArrayList<>();
        units = new ArrayList<>();
        unitIds = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sell_product, container, false);
        navController = Navigation.findNavController(getActivity(), R.id.fragment);
        Intent intent=getActivity().getIntent();

            rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Please Wait");
            dialog.setCancelable(false);
            categoryText = rootView.findViewById(R.id.requirement_category);
            title = rootView.findViewById(R.id.requirement_title);
            description = rootView.findViewById(R.id.requirement_description);
            quantityMin = rootView.findViewById(R.id.requirement_quantity_min);
            quantityMax = rootView.findViewById(R.id.requirement_quantity_max);
            mrp = rootView.findViewById(R.id.requirement_mrp);
            offerPrice=rootView.findViewById(R.id.requirement_offer_price);
            unit = rootView.findViewById(R.id.requirement_unit);
            shipping=rootView.findViewById(R.id.requirement_shipping);
            locationInfo=rootView.findViewById(R.id.requirement_location_info);

            final AlertDialog.Builder unitBuilder = new AlertDialog.Builder(getActivity());
            unitBuilder.setTitle("Select Unit ");

            units.clear();
            unitIds.clear();
            units.add("Per Piece");
            units.add("Per Kg");
            units.add("Per Lot");
            units.add("Deal Price");

            unitIds.add("1");
            unitIds.add("2");
            unitIds.add("3");
            unitIds.add("4");


            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.select_dialog_post_requirement);
            arrayAdapter.addAll(units);

            unitBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            unitBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String strName = arrayAdapter.getItem(which);
                    unit.setText(strName);
                    unit.setError(null);
                    unitId = unitIds.get(which);

                    dialog.dismiss();
                }
            });
            unit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unitBuilder.show();
                }
            });

            tokenRequest(CITYLIST, null);


            categoryText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(getActivity());
                    navController.navigate(R.id.categoriesFragment);
                }
            });

            city = rootView.findViewById(R.id.requirement_city);
            city.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    citybuilder.show();
                }
            });


            locality = rootView.findViewById(R.id.requirement_locality);
            locality.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (localities.size() != 0) {
                        localityBuilder.show();
                    }
                }
            });

            Button postButton = rootView.findViewById(R.id.post_button);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(getActivity());
                    if (validate())
                        tokenRequest(POSTREQUIREMENT, null);
                }
            });


        return rootView;
    }
    @Override
    public void onResume() {

        super.onResume();

        if (subCategoryName != null) {
            categoryText.setText(subCategoryName);
            categoryText.setError(null);
        } else if (categoryName != null) {
            categoryText.setText(categoryName);
            categoryText.setError(null);
        }

    }

    private boolean validate() {
        if (title.getText().toString().equals("")) {
            title.setError("Enter Title");
            return false;
        }
        if (description.getText().toString().equals("")) {
            description.setError("Enter Description");
            return false;
        }
        if (categoryText.getText().toString().equals("")) {
            categoryText.setError("Enter Category");
            return false;
        }
        if (quantityMin.getText().toString().equals("")) {
            quantityMin.setError("Enter Min Quantity");
            return false;
        }
        if (quantityMax.getText().toString().equals("")) {
            quantityMax.setError("Enter Max Quantity");
            return false;
        }
        if (mrp.getText().toString().equals("")) {
            mrp.setError("Enter MRP");
            return false;
        }
        if (offerPrice.getText().toString().equals("")) {
            offerPrice.setError("Enter offer price");
            return false;
        }
        if (shipping.getText().toString().equals("")) {
            shipping.setError("Enter shipping");
            return false;
        }
        if (locationInfo.getText().toString().equals("")) {
            locationInfo.setError("Enter location info");
            return false;
        }
        if (city.getText().toString().equals("")) {
            city.setError("Enter City");
            return false;
        }

        if (locality.getText().toString().equals("")) {
            locality.setError("Enter locality");
            return false;
        }
        if (unit.getText().toString().equals("")) {
            unit.setError("Enter unit");
            return false;
        }


        return true;
    }
    public void tokenRequest(final int type, final String cityId) {
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

                                    if (type == POSTREQUIREMENT)
                                        postRequirement(token);
                                    else if (type == CITYLIST)
                                        getCityList(token);
                                    else if (type == LOCALITYLIST)
                                        getLocalityList(token, cityId);


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


    public void postRequirement(final String token) {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        dialog.show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Regno", SharedPreferenceUtils.getInstance(getActivity()).getCId());
            jsonBody.put("Title", title.getText().toString());
            JSONObject categoriesObject = new JSONObject();
            categoriesObject.put("CategoryId", categoryId);
            categoriesObject.put("CategoryName", categoryName);
            categoriesObject.put("SubCategoryId", subCategoryId);
            categoriesObject.put("SubCategoryName", subCategoryName);
            jsonBody.put("Categories", categoriesObject);
            jsonBody.put("Description", description.getText().toString());
            jsonBody.put("Unit", Integer.parseInt(unitId));
            jsonBody.put("MinimumQuantity", Integer.parseInt(quantityMin.getText().toString()));
            jsonBody.put("MaximumQuantity", Integer.parseInt(quantityMax.getText().toString()));
            jsonBody.put("MRP", Integer.parseInt(mrp.getText().toString()));
            jsonBody.put("OfferPrice", Integer.parseInt(offerPrice.getText().toString()));
            jsonBody.put("ShippingInfo", shipping.getText().toString());
            jsonBody.put("LocationInfo", locationInfo.getText().toString());
            jsonBody.put("City", cityId);
            jsonBody.put("Locality", localityId);

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlSellRequirement,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            try {
                                JSONObject loginResponse = new JSONObject(response);
                                if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Posted");
                                    builder.setCancelable(false);
                                    builder.setMessage("Congrats! Your product has been posted successfully");
                                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            getActivity().onBackPressed();
                                        }
                                    });

                                    builder.create();
                                    builder.show();
                                } else {
                                    Toast.makeText(getActivity(), loginResponse.getString("message"), Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(getActivity(), "Some error occurred, please try again later", Toast.LENGTH_SHORT).show();

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
                    return "application/json";
                }

            };
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getCityList(final String token) {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        dialog.show();
        cities.clear();
        citiesId.clear();
        try {


            StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlCityList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject loginResponse = new JSONObject(response);
                                if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {

                                    JSONArray jsonArrayState = loginResponse.getJSONArray("data");

                                    for (int i = 0; i < jsonArrayState.length(); i++) {

                                        JSONObject jsonObject = jsonArrayState.getJSONObject(i);
                                        JSONArray jsonArraycity = jsonObject.getJSONArray("Locations");
                                        for (int j = 0; j < jsonArraycity.length(); j++) {
                                            JSONObject jsonObject1 = jsonArraycity.getJSONObject(j);
                                            cities.add(jsonObject1.getString("Name"));
                                            citiesId.add(jsonObject1.getString("Id"));

                                        }
                                    }


                                    citybuilder = new AlertDialog.Builder(getActivity());
                                    citybuilder.setTitle("Select city ");

                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.select_dialog_post_requirement);
                                    arrayAdapter.addAll(cities);

                                    citybuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    citybuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            city.setText(strName);
                                            city.setError(null);
                                            cityId = citiesId.get(which);
                                            getLocalityList(token, citiesId.get(which));
                                            dialog.dismiss();
//                                            AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
//                                            builderInner.setMessage(strName);
//                                            builderInner.setTitle("Your selected city is");
//                                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog,int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                            builderInner.show();
                                        }
                                    });
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getLocalityList(final String token, final String cityId)
    {
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest();
        dialog.show();
        localities.clear();
        localitiesId.clear();
        locality.setText("");
        try {


            StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlLocalityList + cityId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject loginResponse = new JSONObject(response);
                                if (loginResponse.getString("resultType").equalsIgnoreCase("success")) {

                                    JSONArray jsonArrayState = loginResponse.getJSONArray("data");

                                    for (int i = 0; i < jsonArrayState.length(); i++) {

                                        JSONObject jsonObject = jsonArrayState.getJSONObject(i);
                                        localities.add(jsonObject.getString("Name"));
                                        localitiesId.add(jsonObject.getString("Id"));


                                    }


                                    localityBuilder = new AlertDialog.Builder(getActivity());
                                    localityBuilder.setTitle("Select locality -");

                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.select_dialog_post_requirement);
                                    arrayAdapter.addAll(localities);

                                    localityBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    localityBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            locality.setText(strName);
                                            locality.setError(null);
                                            localityId = localitiesId.get(which);

                                        }
                                    });
                                    locality.setEnabled(true);
                                    dialog.dismiss();
//                                    localityBuilder.show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
