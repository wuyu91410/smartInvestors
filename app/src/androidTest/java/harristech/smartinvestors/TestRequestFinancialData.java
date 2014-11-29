package harristech.smartinvestors;

import android.test.AndroidTestCase;

/**
 * Created by henry on 11/24/14.
 */
public class TestRequestFinancialData extends AndroidTestCase {
    public static final String query = "AAPL";

    public void testParseHTML() {
        RequestFinancialData requestFinancialData = new RequestFinancialData(query);
        requestFinancialData.parseHTML();
        requestFinancialData.getData();
    }
}