package harristech.smartinvestors.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by henry on 11/21/14.
 */
public class FinancialContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "harristech.smartinvestors";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths
    public static final String PATH_FINANCIAL = "financial";
    public static final String PATH_STOCK = "stock";

    // Inner class that defines the table contents of the stock table
    public static final class StockEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STOCK).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;

        // Table name
        public static final String TABLE_NAME = "stock";

        // The stock ticker string is what will be sent to Quandl
        // as the stock query.
        public static final String COLUMN_STOCK_TICKER = "stock_ticker";

        // Human readable stock string (company name), provided by API.
        public static final String COLUMN_COMPANY_NAME = "company_name";

        // Stock exchange market and type as returned by yahoo
        public static final String COLUMN_EXCH_DISP = "exch_disp";
        public static final String COLUMN_TYPE_DISP = "type_disp";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // Inner class that defines the table contents of the stock's financial table
    public static final class FinancialEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FINANCIAL).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_FINANCIAL;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_FINANCIAL;

        public static final String TABLE_NAME = "financial";

        // Column with the foreign key into the stock table.
        public static final String COLUMN_STOCK_KEY = "stock_id";
        // Date, stored as Text with format yyyy-MM
        public static final String COLUMN_QUARTER_TEXT = "quarter";
        // Unit of statement
        public static final String COLUMN_UNIT = "unit";
        // Total Revenue
        public static final String COLUMN_TOTAL_Revenue = "totalrevenue";
    }
}
