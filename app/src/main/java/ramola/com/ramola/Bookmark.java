
package ramola.com.ramola;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Bookmark extends Fragment {
    RecyclerView recyclerView;
    MainAdapter adapter;
    ArrayList<Item_main> list;
    DBHelper dbHelper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_main_screen,container,false);
        recyclerView= (RecyclerView) v.findViewById(R.id.recycler_list_main);
        list=new ArrayList<>();
        dbHelper=new DBHelper(getActivity());
        adapter=new MainAdapter(getActivity(),list);
        getData();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
              SQLiteDatabase sqLiteDatabase= dbHelper.getWritableDatabase();
              sqLiteDatabase.delete(DBHelper.TABLE_NAME,DBHelper.KEY_ROWID+"="+(++position),null);
                list.remove(position);
                adapter.notifyDataSetChanged();
                dbHelper.close();
            }
        }));
        return v;
    }
    public void getData(){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String columns[]={DBHelper.KEY_ROWID,DBHelper.Key_Topic,DBHelper.KEY_Title,DBHelper.KEY_description,DBHelper.Key_url};
        Cursor cursor=db.query(DBHelper.TABLE_NAME,columns,null,null,null,null,null);
        if(cursor.moveToFirst()){
        do{
           list.add(new Item_main(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
            adapter.notifyDataSetChanged();
        }while (cursor.moveToNext());}
        cursor.close();
        db.close();
    }
}
