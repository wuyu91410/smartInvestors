package harristech.smartinvestors;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FinancialActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_financial, new FinancialFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_financial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    // TODO: Complete the detail fragment
    public static class FinancialFragment extends Fragment {
        private String mStockTicker;
        private ListView mListView;
        private ArrayAdapter<String> mFinancialListAdapter;

        public FinancialFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_financial, container, false);
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mStockTicker = intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.financial))
                        .setText(mStockTicker);
                mFinancialListAdapter = new ArrayAdapter<String>(
                        // Current context (this activity)
                        getActivity(),
                        // ID of list item layout
                        R.layout.list_item_financial,
                        // ID of the textview to populate
                        R.id.list_item_financial_textview,
                        // Forecast data
                        new ArrayList<String>());
            }
            // Get a reference to the ListView, and attach this adapter to it
            mListView =(ListView) rootView.findViewById(R.id.list_financial);
            mListView.setAdapter(mFinancialListAdapter);

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            fetchFinancial();
        }

        private void fetchFinancial() {
            FetchFinancialTask fetchFinancialTask = new FetchFinancialTask();
            fetchFinancialTask.execute(mStockTicker);
        }

        private class FetchFinancialTask extends
                AsyncTask<String, Void, Map<String, List<String>>> {
            private Map<String, List<String>> data = new ArrayMap<>();

            private final String LOG_TAG = FetchFinancialTask.class.getSimpleName();

            @Override
            protected Map<String, List<String>> doInBackground(String... params) {
                Document incomeStatement;
                Document balanceSheet;
                Document cashFlow;
                String query = params[0];

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
                // Set the List view
                if (stringListMap.size() != 0) {
                    mFinancialListAdapter.clear();
                    for (Map.Entry<String, List<String>> entry : stringListMap.entrySet()) {
                        String key = entry.getKey();
                        List<String> list = entry.getValue();
                        String val = key + ": ";
                        for (String item : list) {
                            val = val + item + "\t";
                        }

                        mFinancialListAdapter.add(val);
                    }
                } else {
                    Log.d(LOG_TAG, "Requested data is null");
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
        }

    }
}
