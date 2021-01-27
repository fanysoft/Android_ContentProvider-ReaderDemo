package cz.vancura.contentproviderreader2020;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "myTAG-MainActivity";

    private Cursor mData;
    private int mDefCol, mWordCol;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "activity started");

        textView = findViewById(R.id.textView2);

        //Run the database operation to get the cursor off of the main thread
        new WordFetchTask().execute();

    }


    public class WordFetchTask extends AsyncTask<Void, Void, Cursor> {


        @Override
        protected Cursor doInBackground(Void... params) {

            // Get the content resolver
            ContentResolver resolver = getContentResolver();

            // Call the query method on the resolver with the correct Uri from the contract class
            Uri uri = Uri.parse("content://cz.vancura.android.todolist/tasks/");
            Cursor cursor = resolver.query(uri, null, null, null, null);
            Log.d(TAG, "AsyncTask - doInBackground - reading from uri=" + uri);

            return cursor;
        }


        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            Log.d(TAG, "AsyncTask - onPostExecute");

            if (cursor == null){
                // cursor has no data
                String error = "empty - missing Content Provider source app ? ";
                Log.e(TAG, error);
                textView.append(error + "\n\n" );

            }else {
                // cursor has data

                int colCount = cursor.getColumnCount();
                int rowCount =  cursor.getCount();

                Log.d(TAG, "Cursor das " + colCount + " collumns and " + rowCount + " rows");
                String[] columnNames = cursor.getColumnNames();
                int loopCol=0;
                for (String s: columnNames) {
                    Log.d(TAG, "Cursor column name = " + columnNames[loopCol]);
                    loopCol++;
                }


                mData = cursor;

                // Get the column index, in the Cursor, of each piece of data
                mDefCol = mData.getColumnIndex("description");
                mWordCol = mData.getColumnIndex("priority");


                // Test - read data from cursor

                int loop = 0;
                // 2 - read data
                if (mData.moveToFirst()){
                    do{
                        loop++;
                        String dataId = mData.getString(mData.getColumnIndex("_id"));
                        String dataDesr = mData.getString(mData.getColumnIndex("description"));
                        String dataPrio = mData.getString(mData.getColumnIndex("priority"));

                        String dataTogether =  loop + "] id=" + dataId + " description=" + dataDesr + " priority=" + dataPrio;

                        Log.d(TAG, dataTogether);

                        textView.append(dataTogether + "\n\n" );

                    }while(mData.moveToNext());
                }
                mData.close();

            }

        }

    }

}