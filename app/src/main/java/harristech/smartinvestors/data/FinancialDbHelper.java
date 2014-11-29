package harristech.smartinvestors.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import harristech.smartinvestors.data.FinancialContract.StockEntry;
import harristech.smartinvestors.data.FinancialContract.FinancialEntry;

/**
 * Created by henry on 11/21/14.
 */
public class FinancialDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "financial.db";

    public FinancialDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold stocks
        final String SQL_CREATE_STOCK_TABLE =
                "CREATE TABLE " + StockEntry.TABLE_NAME + " (" +
                StockEntry._ID + " INTEGER PRIMARY KEY," +
                StockEntry.COLUMN_STOCK_TICKER + " TEXT UNIQUE NOT NULL, " +
                StockEntry.COLUMN_COMPANY_NAME + " TEXT NOT NULL, " +
                StockEntry.COLUMN_EXCH_DISP + " TEXT NOT NULL, " +
                StockEntry.COLUMN_TYPE_DISP + " TEXT NOT NULL, " +
                "UNIQUE (" + StockEntry.COLUMN_STOCK_TICKER +") ON CONFLICT IGNORE" +
                " );";

        // Create a table to hold financial statement details
        final String SQL_CREATE_FINANCIAL_TABLE =
                "CREATE TABLE " + FinancialEntry.TABLE_NAME + " (" +
                FinancialEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.

                // the ID of the location entry associated with this weather data
                FinancialEntry.COLUMN_STOCK_KEY + " INTEGER NOT NULL, " +
                FinancialEntry.COLUMN_QUARTER_TEXT + " TEXT NOT NULL, " +
                FinancialEntry.COLUMN_UNIT + " TEXT NOT NULL, " +
                // WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

                // TODO: ADD FINANCIAL TERM HERE
                FinancialEntry.COLUMN_TOTAL_Revenue + " TEXT NOT NULL, " +

                // Set up the financial column as a foreign key to stock table.
                " FOREIGN KEY (" + FinancialEntry.COLUMN_STOCK_KEY + ") REFERENCES " +
                StockEntry.TABLE_NAME + " (" + StockEntry._ID + "), " +

                // To assure the application have just one financial entry per day
                // per stock, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + FinancialEntry.COLUMN_QUARTER_TEXT + ", " +
                FinancialEntry.COLUMN_STOCK_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_STOCK_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FINANCIAL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
