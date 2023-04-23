package com.example.dailyquotes

import android.content.ContentValues
import android.content.Context
import android.content.pm.ActivityInfo
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.provider.BaseColumns
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dailyquotes.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var bind:ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        bind = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        addQuote(1, "The greatest glory in living lies not in never falling, but in rising every time we fall.", "— Nelson Mandela",this)
        addQuote(2,"It is better to fail in originality than to succeed in imitation.","— Herman Melville",this)
        addQuote(3,"Success is not final; failure is not fatal: It is the courage to continue that counts.","— Winston S. Churchill",this)
        addQuote(4,"Learn as if you will live forever, live like you will die tomorrow.","— Mahatma Gandhi",this)
        addQuote(5,"If you are working on something that you really care about, you don’t have to be pushed. The vision pulls you.","— Steve Jobs",this)
        addQuote(6,"Concentrate all your thoughts upon the work in hand. The sun's rays do not burn until brought to a focus.","— Alexander Graham Bell",this)
        addQuote(7,"When we strive to become better than we are, everything around us becomes better too.","— Paulo Coelho",this)
        addQuote(8,"Opportunity is missed by most people because it is dressed in overalls and looks like work.","— Thomas Edison",this)

        //load next quote
        bind.nextQuote.setOnClickListener {

            retrieveQuote(this,bind.quoteTxt,bind.name,bind.letter)
            var newColor = getRandomColor(this)
            bind.name.setTextColor(newColor)
            bind.letter.setTextColor(newColor)
            bind.qMarks.setTextColor(newColor)

        }
   }

}

//private fun getQuote(id:Int): String {}
//private fun getName():String{}
data class Quote(val id: Int, val text: String, val author: String)

object QuotesContract {
    object QuoteEntry : BaseColumns {
        const val TABLE_NAME = "quotes"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_TEXT = "text"
        const val COLUMN_NAME_AUTHOR = "author"
    }
}

class QuotesDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Quotes.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${QuotesContract.QuoteEntry.TABLE_NAME} (" +
                    "${QuotesContract.QuoteEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY," +
                    "${QuotesContract.QuoteEntry.COLUMN_NAME_TEXT} TEXT," +
                    "${QuotesContract.QuoteEntry.COLUMN_NAME_AUTHOR} TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${QuotesContract.QuoteEntry.TABLE_NAME}"
    }
}

fun addQuote(i: Int, q:String, author: String, context: Context){
    val dbHelper = QuotesDbHelper(context)

// Insert a new quote
    val quote = Quote(i,q,author)
    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {
        put(QuotesContract.QuoteEntry.COLUMN_NAME_ID, quote.id)
        put(QuotesContract.QuoteEntry.COLUMN_NAME_TEXT, quote.text)
        put(QuotesContract.QuoteEntry.COLUMN_NAME_AUTHOR, quote.author)
    }
    db.insert(QuotesContract.QuoteEntry.TABLE_NAME, null, values)
}

fun retrieveQuote(context: Context, q: TextView,a:TextView, l:TextView) {
    // Create a QuotesDbHelper instance to access the database
    val dbHelper = QuotesDbHelper(context)

    // Retrieve a quote from the database
    val db = dbHelper.readableDatabase
    val projection = arrayOf(
        QuotesContract.QuoteEntry.COLUMN_NAME_ID,
        QuotesContract.QuoteEntry.COLUMN_NAME_TEXT,
        QuotesContract.QuoteEntry.COLUMN_NAME_AUTHOR
    )
    val selection = "1 ORDER BY RANDOM() LIMIT 1"
    val cursor = db.query(
        QuotesContract.QuoteEntry.TABLE_NAME,
        projection,
        selection,
        null,
        null,
        null,
        null
    )


    // Check if the cursor contains any data
    if (cursor.moveToFirst()) {
        // Extract the quote information from the cursor
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(QuotesContract.QuoteEntry.COLUMN_NAME_ID))
        val text = cursor.getString(cursor.getColumnIndexOrThrow(QuotesContract.QuoteEntry.COLUMN_NAME_TEXT))
        val author = cursor.getString(cursor.getColumnIndexOrThrow(QuotesContract.QuoteEntry.COLUMN_NAME_AUTHOR))

        // Display the quote in the text view
        q.text = text
        a.text = author
        l.text  = author[2].toString()
    }

    // Close the cursor and database to avoid memory leaks
    cursor.close()
    db.close()
}

fun getRandomColor(context: Context): Int {
    // List of color resource IDs
    val colorList = listOf(
        R.color.option1,
        R.color.option2,
        R.color.option3,
        R.color.option4,
        R.color.option5
    )

    // Randomly select a color from the list
    val randomIndex = Random.nextInt(colorList.size)
    val randomColor = colorList[randomIndex]

    // Return the color as an integer value
    return ContextCompat.getColor(context, randomColor)
}
