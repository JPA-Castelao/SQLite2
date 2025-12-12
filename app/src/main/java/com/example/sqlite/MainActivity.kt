package com.example.sqlite

import android.R.attr.subtitle
import android.content.ContentValues
import android.os.Bundle
import android.provider.BaseColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sqlite.ui.theme.SQLiteTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SQLiteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        val dbHelper = sqlite.FeedReaderContract.FeedReaderDbHelper(application)

// Gets the data repository in write mode
        val db1 = dbHelper.writableDatabase

// Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(sqlite.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, "titulo")
            put(sqlite.FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "subtitle")
        }


// Insert the new row, returning the primary key value of the new row
        val newRowId = db1?.insert(sqlite.FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
        val db2 = dbHelper.readableDatabase

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        val projection = arrayOf(
            BaseColumns._ID,
            sqlite.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
            sqlite.FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE
        )

// Filter results WHERE "title" = 'My Title'
        val select1 = "${sqlite.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} = ?"
        val selAtgs = arrayOf("My Title")

// How you want the results sorted in the resulting Cursor
        val sortOrder = "${sqlite.FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

        val cursor = db1.query(
            sqlite.FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            select1,              // The columns for the WHERE clause
            selAtgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        val itemIds = mutableListOf<Long>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                itemIds.add(itemId)
            }
        }
        cursor.close()

        // Define 'where' part of query.
        val select2 = "${sqlite.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
// Specify arguments in placeholder order.
        val selAtgs2 = arrayOf("MyTitle")
// Issue SQL statement.
        val deletedRows =
            db1.delete(sqlite.FeedReaderContract.FeedEntry.TABLE_NAME, select2, selAtgs2)

        val db = dbHelper.writableDatabase

// New value for one column
        val title = "MyNewTitle"
        val valores = ContentValues().apply {
            put(sqlite.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title)
        }

// Which row to update, based on the title
        val selection = "${sqlite.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
        val selectionArgs = arrayOf("MyOldTitle")
        val count = db.update(
            sqlite.FeedReaderContract.FeedEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )

        dbHelper.close()

    }

    override fun onDestroy() {
        super.onDestroy()
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SQLiteTheme {
        Greeting("Android")
    }
}