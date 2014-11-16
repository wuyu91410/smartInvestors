package harristech.smartinvestors;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by henry on 11/14/14.
 */
public class PlaceholderFragment extends Fragment {

    private ArrayAdapter<String> mFavorListAdapter;

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mFavorListAdapter = new ArrayAdapter<String>(
                // Current context (this activity)
                getActivity(),
                // ID of list item layout
                R.layout.list_item_favor,
                // ID of the textview to populate
                R.id.list_item_favor_textview,
                // Forecast data
                new ArrayList<String>());

        String[] exampleTicker = {"GOOG", "AAPL", "MSFT"};
        for (String ticker : exampleTicker) {
            mFavorListAdapter.add(ticker);
        }

        // Get a reference to the ListView, and attach this adapter to it
        ListView listView =(ListView) rootView.findViewById(R.id.list_favor);
        listView.setAdapter(mFavorListAdapter);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }
*/

}
