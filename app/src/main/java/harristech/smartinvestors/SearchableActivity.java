package harristech.smartinvestors;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;


/**
 * Created by henry on 11/14/14.
 */
public class SearchableActivity extends Activity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {

    private ListView mListView;
    private SearchView searchView;
    private String requestJsonStr = null;
    private ArrayAdapter<String> mSearchAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.d("Searchable Activity", "success");

        searchView = (SearchView) findViewById(R.id.search);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        mListView = (ListView) findViewById(R.id.list_search);

        mSearchAdapter = new ArrayAdapter<String>(
                // Current context (this activity)
                this,
                // ID of list item layout
                R.layout.search_results,
                // ID of the textview to populate
                R.id.search_res,
                // Forecast data
                new ArrayList<String>());

        // Get a reference to the ListView, and attach this adapter to it
        mListView.setAdapter(mSearchAdapter);

    }

    public class DoMySearch extends AsyncTask<String, Void, String[]> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        protected String[] doInBackground(String... requestTerm) {

            final String LOG_TAG = Connection.class.getSimpleName();
            try {
                final String urlBase = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=";
                String builtUri = urlBase + requestTerm[0]
                        + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
                URL url = new URL(builtUri);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    requestJsonStr = null;
                }
                requestJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, e.toString());
                requestJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, e.toString());
                    }
                }
            }

            try {
                return getStrFromJson(requestJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null) {
                mSearchAdapter.clear();
                for (String oneComInfo : result) {
                    mSearchAdapter.add(oneComInfo);
                }
            }
        }
    }

    private String[] getStrFromJson(String str) throws JSONException {
        if (str == null) {
            System.out.println("Error: Json string null");
            return null;
        }
        int j = 0;
        while (str.charAt(j) != '{') {
            ++j;
        }
        String jsonStr = str.substring(j);

        JSONObject stickerJson = new JSONObject(jsonStr);
        JSONArray resultArr = stickerJson.getJSONObject("ResultSet").getJSONArray("Result");

        String[] stickerInfo = new String[resultArr.length()];
        for (int i = 0; i < resultArr.length(); i++) {
            String symbol = resultArr.getJSONObject(i).getString("symbol");
            String name = resultArr.getJSONObject(i).getString("name");
            String exchDisp = resultArr.getJSONObject(i).getString("exchDisp");
            String typeDisp = resultArr.getJSONObject(i).getString("typeDisp");
            stickerInfo[i] = symbol + "-" + name + "\n" + typeDisp + "-" + exchDisp;
        }

        return stickerInfo;
    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        DoMySearch doMySearch = new DoMySearch();
        doMySearch.execute(query);

        return true;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        DoMySearch doMySearch = new DoMySearch();
        doMySearch.execute(newText);

        return true;
    }

    /**
     * The user is attempting to close the SearchView.
     *
     * @return true if the listener wants to override the default behavior of clearing the
     * text field and dismissing it, false otherwise.
     */
    @Override
    public boolean onClose() {
        return false;
    }
}
