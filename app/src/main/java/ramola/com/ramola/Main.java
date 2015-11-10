package ramola.com.ramola;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Main extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView recyclerView;
    MainAdapter adapter;
    ArrayList<Item_main> list;
    CoordinatorLayout coordinatorLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    String url="http://api.nytimes.com/svc/news/v3/content/all/Technology;world?api-key=84ced263117a1e6d770f560e9ca6f079:0:73275181";
    Connection connection;
    DBHelper dbHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View v=inflater.inflate(R.layout.activity_main_screen,container,false);
        recyclerView= (RecyclerView) v.findViewById(R.id.recycler_list_main);
        coordinatorLayout= (CoordinatorLayout) v.findViewById(R.id.main_news_coordinator);
        swipeRefreshLayout= (SwipeRefreshLayout) v.findViewById(R.id.swipe_main_news);
        dbHelper=new DBHelper(getActivity());
        swipeRefreshLayout.setOnRefreshListener(this);

        connection=new Connection(getActivity());
        if(connection.isInternet()){
            if(savedInstanceState==null) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
            }
        }
        else
        {
            Snackbar.make(coordinatorLayout, "NO INTERNET PRESENT", Snackbar.LENGTH_INDEFINITE).show();
            list=new ArrayList<>();
        }
        if(savedInstanceState!=null){
            list=savedInstanceState.getParcelableArrayList("list");
           swipeRefreshLayout.setRefreshing(false);
        }
        else {
            list=new ArrayList<>();
            GetJson(url);
        }
        adapter=new MainAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                adddata(list.get(position).Section,list.get(position).title,list.get(position).description,list.get(position).url);
                Toast.makeText(getActivity(),"Bookmark Added",Toast.LENGTH_SHORT).show();


            }
        }));
        return v;
    }
    public void GetJson(String url){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String url;
                try {
                    JSONArray data=new JSONArray(jsonObject.getString("results"));
                    for(int i=0;i<=data.length()-1;i++){
                        JSONObject dataItem=data.getJSONObject(i);
                        if(dataItem.getString("multimedia").length()!=0) {
                            JSONArray urlpic = new JSONArray(dataItem.getString("multimedia"));
                            if (urlpic.length() != 0) {
                                JSONObject urlpicItem = urlpic.getJSONObject(urlpic.length() - 1);
                                url = urlpicItem.getString("url");
                                list.add(new Item_main(dataItem.getString("section"), dataItem.getString("title"), dataItem.getString("abstract"), url,dataItem.getString("url")));
                            }
                        }
                        else
                            list.add(new Item_main(dataItem.getString("section"),dataItem.getString("title"),dataItem.getString("abstract"),dataItem.getString("url")));
                        if(swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                    createSnackBar("NO NEWS FOUND");
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
                createSnackBar(volleyError.getMessage());

            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public void onRefresh() {
        if(connection.isInternet())
        GetJson(url);
        else {
            Snackbar.make(coordinatorLayout, "NO INTERNET PRESENT", Snackbar.LENGTH_INDEFINITE).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    public void createSnackBar(String Message){
        Snackbar.make(coordinatorLayout,Message,Snackbar.LENGTH_INDEFINITE).show();
    }
    public void adddata(String Section,String title,String description,String url){
        ContentValues values=new ContentValues();
        values.put(DBHelper.Key_Topic,Section);
        values.put(DBHelper.KEY_Title,title);
        values.put(DBHelper.KEY_description,description);
        values.put(DBHelper.Key_url,url);
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        long id=sqLiteDatabase.insert(DBHelper.TABLE_NAME,null,values);
        dbHelper.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list",list);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }
}
