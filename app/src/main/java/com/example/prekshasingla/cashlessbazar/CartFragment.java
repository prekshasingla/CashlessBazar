package com.example.prekshasingla.cashlessbazar;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import androidx.navigation.Navigation;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    ProgressDialog dialog;
    List<CartItem> cartItemList;
    CartAdapter cartAdapter;
    TextView cartEmptyText;
    ImageView cartEmptyImage;
    RecyclerView recyclerView;

    int CODELIST = 0, CODEADD = 1, CODEMINUS = 2, CODEDELETE = 3;
    private int itemQuantity;

    public CartFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_cart, container, false);
        rootview.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading cart");
        dialog.setCancelable(false);

        cartEmptyImage = rootview.findViewById(R.id.cart_empty);
        cartEmptyText = rootview.findViewById(R.id.cart_empty_text);

        cartItemList = new ArrayList<>();
        recyclerView = rootview.findViewById(R.id.recycler);
        cartAdapter = new CartAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(cartAdapter);

        tokenRequest(CODELIST, null);
        return rootview;
    }

    public void tokenRequest(final int code, final String id) {
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
                                if (token != null) {
                                    if (code == CODELIST)
                                        getList(token);
                                    else {
                                        cartUpdate(code, token, id);
                                    }
                                } else
                                    Toast.makeText(getActivity(), "Could not connect, please try again later", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("There may be some error");
                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getActivity().onBackPressed();
                                }
                            });

                            builder.create();
                            builder.show();
                        }
                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("There may be some error");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().onBackPressed();
                            }
                        });

                        builder.create();
                        builder.show();
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


            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void cartUpdate(final int code, final String token, final String id) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.urlCartAdd,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("resultType").equalsIgnoreCase("success")) {
                                tokenRequest(CODELIST, null);

                            } else {
                                Toast.makeText(getActivity(), responseObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("There may be some error");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().onBackPressed();
                            }
                        });

                        builder.create();
                        builder.show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (SharedPreferenceUtils.getInstance(getActivity()).getCartCookiesId() != null)
                    params.put("cookiesId", SharedPreferenceUtils.getInstance(getActivity()).getCartCookiesId());
                else {
//                    params.put("cookiesId", "0");
                }

                params.put("productid", id);
                params.put("regno", SharedPreferenceUtils.getInstance(getActivity()).getCId() + "");
                if (code == CODEDELETE) {
                    params.put("type", "remove");
                    params.put("qunatity", "0");

                } else if (code == CODEADD) {
                    params.put("type", "update");
                    params.put("qunatity", itemQuantity + "");
                    params.put("amount", "0");
                } else if (code == CODEMINUS) {
                    params.put("type", "update");
                    params.put("qunatity", itemQuantity + "");
                    params.put("amount", "0");
                }
                return params;
            }


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

    private void getList(final String token) {
        dialog.show();
        cartItemList.clear();
        String cookiesId = "0";
        if (SharedPreferenceUtils.getInstance(getActivity()).getCartCookiesId() != null) {
            cookiesId = SharedPreferenceUtils.getInstance(getActivity()).getCartCookiesId();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.urlCartList + cookiesId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            try {
                                JSONObject responseObject = new JSONObject(response);
                                if (responseObject.getString("resultType").equalsIgnoreCase("success")) {
                                    JSONObject data = responseObject.getJSONObject("data");
                                    if (!data.getString("totalitem").equalsIgnoreCase("0")) {
                                        JSONArray cart = data.getJSONArray("cart");
                                        for (int i = 0; i < cart.length(); i++) {
                                            JSONObject object = cart.getJSONObject(i);
                                            if (!object.getString("qunatity").equals("0")) {
                                                CartItem item = new CartItem();
                                                item.title = object.getString("productname");
                                                item.image = object.getString("productimage");
                                                item.price = object.getString("total");
                                                item.qty = object.getString("qunatity");
                                                item.id = object.getString("productid");
                                                cartItemList.add(item);
                                            }
                                        }

                                        if (cartItemList.size() == 0) {
                                            recyclerView.setVisibility(View.GONE);
                                            cartEmptyImage.setVisibility(View.VISIBLE);
                                            cartEmptyText.setVisibility(View.VISIBLE);
                                        } else {

                                            cartAdapter.notifyDataSetChanged();
                                            recyclerView.setVisibility(View.VISIBLE);
                                            cartEmptyImage.setVisibility(View.GONE);
                                            cartEmptyText.setVisibility(View.GONE);
                                        }
                                    } else {
                                        recyclerView.setVisibility(View.GONE);
                                        cartEmptyImage.setVisibility(View.VISIBLE);
                                        cartEmptyText.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    cartEmptyImage.setVisibility(View.VISIBLE);
                                    cartEmptyText.setVisibility(View.VISIBLE);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("There may be some error");
                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getActivity().onBackPressed();
                                }
                            });

                            builder.create();
                            builder.show();
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
        } else {
            dialog.dismiss();
            recyclerView.setVisibility(View.GONE);
            cartEmptyImage.setVisibility(View.VISIBLE);
            cartEmptyText.setVisibility(View.VISIBLE);
        }
    }

    class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartAdapterViewHolder> {

        private CartAdapterViewHolder holder;

        @NonNull
        @Override
        public CartAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
            holder = new CartAdapterViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull CartAdapterViewHolder holder, int position) {

            CartItem item = cartItemList.get(position);
            holder.title.setText(item.title);
            holder.qty.setText(item.qty);
            holder.price.setText(getActivity().getResources().getString(R.string.rupee) + " " + item.price);
            Picasso.with(getActivity()).load(item.image).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return cartItemList.size();
        }


        public class CartAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView image, iconDelete, iconMinus, iconAdd;
            public TextView title, qty, price;

            public CartAdapterViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
                title = itemView.findViewById(R.id.title);
                price = itemView.findViewById(R.id.price);
                iconDelete = itemView.findViewById(R.id.delete_icon);
                iconMinus = itemView.findViewById(R.id.icon_minus);
                iconAdd = itemView.findViewById(R.id.icon_add);
                qty = itemView.findViewById(R.id.quantity);

                iconAdd.setOnClickListener(this);
                iconMinus.setOnClickListener(this);
                iconDelete.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.delete_icon) {
                    tokenRequest(CODEDELETE, cartItemList.get(getAdapterPosition()).id);
                }
                if (v.getId() == R.id.icon_add) {
                    itemQuantity = Integer.parseInt(cartItemList.get(getAdapterPosition()).qty) + 1;
                    tokenRequest(CODEADD, cartItemList.get(getAdapterPosition()).id);
                }
                if (v.getId() == R.id.icon_minus) {
                    itemQuantity = Integer.parseInt(cartItemList.get(getAdapterPosition()).qty) - 1;
                    tokenRequest(CODEMINUS, cartItemList.get(getAdapterPosition()).id);
                }
            }
        }
    }

    class CartItem {
        String title, price, qty, image, id;
    }
}
