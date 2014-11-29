package harristech.smartinvestors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by henry on 11/22/14.
 */
public class RequestFinancialData {
    private String query;
    public static final String[] INCOME_STATEMENT = {
            //"Period Ending",
            "Total Revenue",
            "Cost of Revenue",
            "Gross Profit",
            "Research Development",
            "Selling General and Administrative",
            "Non Recurring",
            "Others",
            "Total Operating Expenses",
            "Operating Income or Loss",
            "Total Other Income/Expenses Net",
            "Earnings Before Interest And Taxes",
            "Interest Expense",
            "Income Before Tax",
            "Income Tax Expense",
            "Minority Interest",
            "Net Income From Continuing Ops",
            "Discontinued Operations",
            "Extraordinary Items",
            "Effect Of Accounting Changes",
            "Other Items",
            "Net Income",
            "Preferred Stock And Other Adjustments",
            "Net Income Applicable To Common Shares"
    };
    public static final String[] BALANCE_SHEET = {
            "Period Ending",
            "Cash And Cash Equivalents",
            "Short Term Investments",
            "Net Receivables",
            "Inventory",
            "Other Current Assets",
            "Total Current Assets",
            "Long Term Investments",
            "Property Plant and Equipment",
            "Goodwill",
            "Intangible Assets",
            "Accumulated Amortization",
            "Other Assets",
            "Deferred Long Term Asset Charges",
            "Total Assets",
            "Accounts Payable",
            "Short/Current Long Term Debt",
            "Other Current Liabilities",
            "Total Current Liabilities",
            "Long Term Debt",
            "Other Liabilities",
            "Deferred Long Term Liability Charges",
            "Minority Interest",
            "Negative Goodwill",
            "Total Liabilities",
            "Misc Stocks Options Warrants",
            "Redeemable Preferred Stock",
            "Preferred Stock",
            "Common Stock",
            "Retained Earnings",
            "Treasury Stock",
            "Capital Surplus",
            "Other Stockholder Equity",
            "Total Stockholder Equity",
            "Net Tangible Assets"
    };
    public static final String[] CASH_FLOW = {
            "Period Ending",
            "Net Income",
            "Depreciation",
            "Adjustments To Net Income",
            "Changes In Accounts Receivables",
            "Changes In Liabilities",
            "Changes In Inventories",
            "Changes In Other Operating Activities",
            "Total Cash Flow From Operating Activities",
            "Capital Expenditures",
            "Investments",
            "Other Cash flows from Investing Activities",
            "Total Cash Flows From Investing Activities",
            "Dividends Paid",
            "Sale Purchase of Stock",
            "Net Borrowings",
            "Other Cash Flows from Financing Activities",
            "Total Cash Flows From Financing Activities",
            "Effect Of Exchange Rate Changes",
            "Change In Cash and Cash Equivalents"
    };
    private Map<String, List<String>> data = new HashMap<String, List<String>>();

    public RequestFinancialData(String stockTicker) {
        query = stockTicker;
    }

    public void parseHTML() {
        Document incomeStatement;
        Document balanceSheet;
        Document cashFlow;

        try {
            // get html of financial statement
            incomeStatement = Jsoup.connect("http://finance.yahoo.com/q/is?s=" + query).get();
            balanceSheet = Jsoup.connect("http://finance.yahoo.com/q/bs?s=" + query).get();
            cashFlow = Jsoup.connect("http://finance.yahoo.com/q/cf?s=" + query).get();

            // get page title
            String title = cashFlow.title();
            System.out.println("title : " + title);

            // get all links
            Elements elsIncomeStatement = incomeStatement.select("td");
            Elements elsBalanceSheet = balanceSheet.select("td");
            Elements elsCashFlow = cashFlow.select("td");

            // TODO: check the function accuracy
            dataHandler(elsIncomeStatement, INCOME_STATEMENT);
            dataHandler(elsBalanceSheet, BALANCE_SHEET);
            dataHandler(elsCashFlow, CASH_FLOW);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean dataHandler(Elements elements, String[] table) {
        int tIndex = 0;
        for (int eIndex=0; tIndex<table.length; ++eIndex) {
            if (table[tIndex].equals(elements.get(eIndex).text())) {
                // Adding the data to the hash map
                List<String> valSet = new ArrayList<String>();
                String key = (String) elements.get(eIndex).text();
                valSet.add(elements.get(++eIndex).text());
                valSet.add(elements.get(++eIndex).text());
                valSet.add(elements.get(++eIndex).text());
                valSet.add(elements.get(++eIndex).text());
                data.put(key, valSet);
                ++tIndex;
            }
        }
        return true;
    }

    public Map<String, List<String>> getData() {
        return data;
    }

}
