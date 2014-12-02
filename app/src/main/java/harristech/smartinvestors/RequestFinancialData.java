package harristech.smartinvestors;

import android.os.AsyncTask;
import android.util.ArrayMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by henry on 11/30/14.
 */
public class RequestFinancialData extends AsyncTask<String, Void, Map<String, List<String>>> {
    private String query;

    private Map<String, List<String>> data = new ArrayMap<>();

    public RequestFinancialData(String stockTicker) {
        query = stockTicker;
    }

    @Override
    protected Map<String, List<String>> doInBackground(String... strings) {
        Document incomeStatement;
        Document balanceSheet;
        Document cashFlow;

        try {
            // get html of financial statement
            cashFlow = Jsoup.connect("http://finance.yahoo.com/q/cf?s=" + query).get();
            balanceSheet = Jsoup.connect("http://finance.yahoo.com/q/bs?s=" + query).get();
            incomeStatement = Jsoup.connect("http://finance.yahoo.com/q/is?s=" + query).get();

            // get all links
            Elements elsIncomeStatement = incomeStatement.select("td");
            Elements elsBalanceSheet = balanceSheet.select("td");
            Elements elsCashFlow = cashFlow.select("td");

            // data handling
            dataHandler(elsCashFlow, FinancialData.CASH_FLOW, data);
            dataHandler(elsBalanceSheet, FinancialData.BALANCE_SHEET, data);
            dataHandler(elsIncomeStatement, FinancialData.INCOME_STATEMENT, data);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(Map<String, List<String>> stringListMap) {
        super.onPostExecute(stringListMap);
        // TODO: Adding the data into database
        for (Map.Entry<String, List<String>> entry : stringListMap.entrySet()) {
            String key = entry.getKey();
            List<String> list = entry.getValue();
            String val = key + ": ";

        }
    }

    private boolean dataHandler(Elements elements, String[] table,
                                Map<String, List<String>> map) {
        int tIndex = 0;
        for (Element element : elements) {
            if (table[tIndex].equals(element.text())) {
                // Adding the data to the hash map
                List<String> valSet = new ArrayList<>();
                String key = table[tIndex];
                int eIndex = elements.indexOf(element);
                valSet.add(elements.get(++eIndex).text());
                valSet.add(elements.get(++eIndex).text());
                valSet.add(elements.get(++eIndex).text());
                valSet.add(elements.get(++eIndex).text());
                map.put(key, valSet);
                ++tIndex;
            }
            if (tIndex >= table.length) break;
        }
        return true;
    }

    public Map<String, List<String>> getData() {
        return data;
    }

}
