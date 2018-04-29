package SQLDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static SQLDatabase.NightOutDb.DatabaseEntries.CREATE_PLACES_TABLE_QUERY;
import static SQLDatabase.NightOutDb.DatabaseEntries.NAME;
import static SQLDatabase.NightOutDb.DatabaseEntries.PLACE_TABLE_NAME;
import static SQLDatabase.NightOutDb.DatabaseEntries.VERSION;

/**
 * Created by Ofek on 18-Mar-18.
 */

public class NightOutDb extends SQLiteOpenHelper{



    public NightOutDb(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e("SqliteDb", "database onCreate called");
        sqLiteDatabase.execSQL(CREATE_PLACES_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.e("SqliteDb", "database onUpdate called");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+PLACE_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    protected class DatabaseEntries{
        protected static final String NAME = "NightOutDatabase";
        protected static final int VERSION = 1;
        protected static final String PLACE_TABLE_NAME = "PLACES";
        protected static final String ID_COL_1 = "place_id";
        protected static final String ICON_COL_2 = "icon";
        protected static final String NAME_COL_3 = "name";
        protected static final String ADDRESS_COL_4 = "address";
        protected static final String TYPES_COL_5 = "types";
        protected static final String LAT_COL_6 = "latitude";
        protected static final String LNG_COL_7 = "longitude";
        protected static final String CREATE_PLACES_TABLE_QUERY = "CREATE TABLE "+PLACE_TABLE_NAME+"("
                + ID_COL_1+" TEXT PRIMARY KEY, "
                + ICON_COL_2+" TEXT, "
                + NAME_COL_3+" TEXT, "
                + ADDRESS_COL_4+" TEXT, "
                + TYPES_COL_5+" TEXT, "
                + LAT_COL_6 + " REAL, "
                + LNG_COL_7 + " REAL" + ")";


    }
}

