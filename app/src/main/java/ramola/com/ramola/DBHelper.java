package ramola.com.ramola;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Bookmark";
    static final String TABLE_NAME = "Bookmark";
    static final String KEY_ROWID = "id";
    static final String KEY_Title = "Title";
    static final String KEY_description = "Description";
    static final String Key_Topic = "Topic";
    static final String Key_url = "url";
    String query = "Create Table " + TABLE_NAME + " ( " + KEY_ROWID + " integer primary key autoincrement, " + KEY_Title + " text ," + KEY_description + " text, " + Key_Topic + " text, " + Key_url + " text )";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(query);
        } catch (Exception e) {
            Log.e("Database error", e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(sqLiteDatabase);
    }
}
