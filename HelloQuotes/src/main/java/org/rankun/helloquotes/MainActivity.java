package org.rankun.helloquotes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.rankun.helloquotes.net.GetDataEx1;
import org.rankun.helloquotes.util.PlainTextTokenizer;


public class MainActivity extends ActionBarActivity {
    private static final String DEBUG_TAG = "HelloLogin";
    private static final String[] URLS = new String[] {
            "http://quotesondesign.com/api/3.0/api-3.0.json",
            "France", "Italy", "Germany", "Spain"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set Auto complete on InputField.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, URLS);
        MultiAutoCompleteTextView textView = (MultiAutoCompleteTextView) findViewById(R.id.url_box);
        textView.setAdapter(adapter);
        textView.setTokenizer(new PlainTextTokenizer());

        //Set default Action of "Done" button in IME.
        TextView.OnEditorActionListener searchListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.d(DEBUG_TAG, "Search Button Pressed.");
                    Button button = (Button) findViewById(R.id.action_btn);
                    addMsg(button);
                }
                return true;
            }
        };
        textView.setOnEditorActionListener(searchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Response to Button #action_btn.
     * @param view A View occupies a rectangular area on the screen and is responsible for
     *             drawing and event handling. View is the base class for widgets.
     */
    public void addMsg(View view) {

        EditText ipTxtView = (EditText) findViewById(R.id.url_box);
        Editable editable = ipTxtView.getText();

        if (null != editable) {
            //perform the request.
            getFromServer(editable.toString());
        } else {
            toast("Please input an URL!");
        }
    }

    /**
     * Retrieve data from Server. and set it to UI by calling {@link #appendContent(String)}
     * @param url the url.
     */
    public String getFromServer(String url) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            String dataStr = null;
            if (networkInfo != null && networkInfo.isConnected()) {
                new DownloadDataTask().execute(url);
            } else {
                toast("disconnect");
                dataStr = "DISCONNECTED!";
            }
            return dataStr;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR!";
        }
    }

    public class DownloadDataTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                return new GetDataEx1().getDataStr(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject obj) {
            if (null != obj) {
                try {
                    int id = obj.getInt("id");
                    String quote = obj.getString("quote");
                    String author = obj.getString("author");
                    String permaLink = obj.getString("permalink");

                    Log.d(DEBUG_TAG, "id=" + id);
                    Log.d(DEBUG_TAG, "quote=" + quote);
                    Log.d(DEBUG_TAG, "author=" + author);
                    Log.d(DEBUG_TAG, "permaLink=" + permaLink);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(DEBUG_TAG, "No JSONObject passed.");
            }
        }
    }

    /**
     * append String from server to TextView.
     * @param str Text to append.
     */
    public void appendContent(String str) {
        LinearLayout logBox = (LinearLayout) findViewById(R.id.log_box);

        TextView textView = new TextView(this);
        textView.setText(str);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        logBox.addView(textView);
    }

    /**
     * Show a msg for a short time.
     * @param msg Text to show.
     */
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
