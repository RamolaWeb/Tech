package ramola.com.ramola;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Book extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    recycler_adapter adapter;
    ArrayList<Item> list;
    EditText search;
    ImageButton btn;
    String data="http://it-ebooks-api.info/v1/search/";
    OnBookClickListener onBookClickListener;
    SwipeRefreshLayout swipeRefreshLayout;
    int count=1;
    CoordinatorLayout coordinatorLayout;
    Connection connection;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_main,container,false);
        recyclerView= (RecyclerView) v.findViewById(R.id.recycler_view);
        search= (EditText) v.findViewById(R.id.search_edit);
        btn= (ImageButton) v.findViewById(R.id.search_btn);
        swipeRefreshLayout= (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        coordinatorLayout= (CoordinatorLayout) v.findViewById(R.id.main_coordinator);
         connection=new Connection(getActivity());
        if(savedInstanceState!=null){
            list=savedInstanceState.getParcelableArrayList("list");
        }
        else
        list=new ArrayList<>();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connection.isInternet()) {
                    fetchData(data + search.getText().toString());
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    });
                }
                else {
                    createSnackBar("NO INTERNET CONNECTION");
                }
            }
        });

        adapter=new recycler_adapter(getContext(),list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout.setOnRefreshListener(Book.this);
        recyclerView.addOnItemTouchListener(new ClickListener(getActivity(), new ClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onBookClickListener.getId(list.get(position).id);
            }
        }));

        return v;
    }
    public void fetchData(String url){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONArray jsonArray=new JSONArray(jsonObject.getString("Books"));
                    for (int i=0;i<jsonArray.length();i++){
                        final   JSONObject data=jsonArray.getJSONObject(i);

                        ImageRequest imageRequest=new ImageRequest(data.getString("Image"),new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                try {
                                    list.add(new Item(data.getString("Title"),data.getString("isbn"),data.getString("Description"),data.getString("ID"),bitmap));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    createSnackBar("IMAGE COULD NOT BE LOADED");
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        },0,0,null,new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                                createSnackBar("NETWORK ERROR");
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });

                        MySingleton.getInstance(getActivity()).addToRequestQueue(imageRequest);
                    }

                //    load.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    createSnackBar("NO BOOK FOUND");
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                createSnackBar("NETWORK ERROR");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        if(connection.isInternet()){
        count += 1;
fetchData(data+search.getText().toString()+"/page/"+count);}
    else{
            Snackbar.make(coordinatorLayout, "NO INTERNET PRESENT", Snackbar.LENGTH_INDEFINITE).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public interface OnBookClickListener{
        public void getId(String id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onBookClickListener= (OnBookClickListener) context;
    }
    public void createSnackBar(String Message){
        Snackbar.make(coordinatorLayout,Message,Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list",list);
    }
}
