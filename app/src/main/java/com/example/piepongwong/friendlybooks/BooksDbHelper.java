package com.example.piepongwong.friendlybooks;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static com.example.piepongwong.friendlybooks.BooksContract.FeedEntry.COLUMN_NAME_AUTHOR;
import static com.example.piepongwong.friendlybooks.BooksContract.FeedEntry.COLUMN_NAME_DESCRIPTION;
import static com.example.piepongwong.friendlybooks.BooksContract.FeedEntry.COLUMN_NAME_ISBN_10;
import static com.example.piepongwong.friendlybooks.BooksContract.FeedEntry.COLUMN_NAME_ISBN_13;
import static com.example.piepongwong.friendlybooks.BooksContract.FeedEntry.COLUMN_NAME_SMALL_THUMBNAIL;
import static com.example.piepongwong.friendlybooks.BooksContract.FeedEntry.COLUMN_NAME_THUMBNAIL;
import static com.example.piepongwong.friendlybooks.BooksContract.FeedEntry.COLUMN_NAME_TITLE;
import static com.example.piepongwong.friendlybooks.BooksContract.FeedEntry.COLUMN_NAME_UUID;
import static com.example.piepongwong.friendlybooks.BooksContract.FeedEntry.TABLE_NAME;

/**
 * Created by Piepongwong on 4-4-2018.
 */

public class BooksDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 106;
    public static final String DATABASE_NAME = "FeedReader.db";
    private Context theContext;

    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + TABLE_NAME + " (" +
        BooksContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
        BooksContract.FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
        BooksContract.FeedEntry.COLUMN_NAME_AUTHOR + " TEXT, " +
        BooksContract.FeedEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
        BooksContract.FeedEntry.COLUMN_NAME_ISBN_10 + " TEXT," +
        BooksContract.FeedEntry.COLUMN_NAME_ISBN_13 + " TEXT," +
        BooksContract.FeedEntry.COLUMN_NAME_SMALL_THUMBNAIL + " TEXT," +
        BooksContract.FeedEntry.COLUMN_NAME_UUID + " TEXT," +
        BooksContract.FeedEntry.COLUMN_NAME_THUMBNAIL + " TEXT" + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public BooksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        theContext = context;
    }
    public void onCreate(SQLiteDatabase db) {
        Log.i("CREATE TABLE QUERY", SQL_CREATE_ENTRIES);
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public long deleteBook(UUID uniqueID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = {uniqueID.toString()};
        String whereClause = BooksContract.FeedEntry.COLUMN_NAME_UUID + "=?";
        long rowId = db.delete(BooksContract.FeedEntry.TABLE_NAME, whereClause, args);
        /*** delete thumbnail file ***/
        ContextWrapper wrapper = new ContextWrapper(theContext);
        File file = wrapper.getDir(theContext.getResources().getString(R.string.thumbnailFolder) ,MODE_PRIVATE);
        file = new File(file, uniqueID +".jpg");
        file.delete();

        return rowId;
    }
    public long createBook(String title, String author, String description,
                           String isbn_10, String isbn_13, String thumbnail,
                           String smallThumbnail, String uuid) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BooksContract.FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(BooksContract.FeedEntry.COLUMN_NAME_AUTHOR, author);
        values.put(BooksContract.FeedEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(BooksContract.FeedEntry.COLUMN_NAME_ISBN_10, isbn_10);
        values.put(COLUMN_NAME_ISBN_13, isbn_13);
        values.put(BooksContract.FeedEntry.COLUMN_NAME_SMALL_THUMBNAIL, smallThumbnail);
        values.put(BooksContract.FeedEntry.COLUMN_NAME_THUMBNAIL, thumbnail);
        values.put(BooksContract.FeedEntry.COLUMN_NAME_UUID, uuid);

        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    public boolean checkExists(String isbn13Or10) {
        SQLiteDatabase db = this.getReadableDatabase();
        String theQuery = String.format("SELECT %s FROM %s WHERE %s = \'%s\' OR %s = \'%s\'", BooksContract.FeedEntry.COLUMN_NAME_UUID,
                BooksContract.FeedEntry.TABLE_NAME, BooksContract.FeedEntry.COLUMN_NAME_ISBN_10,
                isbn13Or10, BooksContract.FeedEntry.COLUMN_NAME_ISBN_13, isbn13Or10);
        Cursor cursor = db.rawQuery(theQuery, null);
        if(cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public List<Book> getAllBooks() {
        List<Book> allBooks = new ArrayList<Book>();
        SQLiteDatabase db = this.getReadableDatabase();
        String theQuery = "SELECT * FROM " + TABLE_NAME ;

        Cursor c = db.rawQuery(theQuery, null);
        if(c.moveToFirst()) {
            do {
                String title = c.getString(c.getColumnIndex(COLUMN_NAME_TITLE));
                String author = c.getString(c.getColumnIndex(COLUMN_NAME_AUTHOR));
                String description = c.getString(c.getColumnIndex(COLUMN_NAME_DESCRIPTION));
                String isbn_10 = c.getString(c.getColumnIndex(COLUMN_NAME_ISBN_10));
                String isbn_13 = c.getString(c.getColumnIndex(COLUMN_NAME_ISBN_13));
                String smallThumbnail = c.getString(c.getColumnIndex(COLUMN_NAME_SMALL_THUMBNAIL));
                String thumbnail = c.getString(c.getColumnIndex(COLUMN_NAME_THUMBNAIL));
                String uuid = c.getString(c.getColumnIndex(COLUMN_NAME_UUID));
                Book book = new Book(title, author, isbn_10, isbn_13, description, smallThumbnail, thumbnail, uuid);
                allBooks.add(book);
            } while(c.moveToNext());
        } else {
            Log.i("Database", "No books in database yet");
        }
        db.close();
        return allBooks;
    }
}
