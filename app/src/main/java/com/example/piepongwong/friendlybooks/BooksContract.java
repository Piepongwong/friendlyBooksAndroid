package com.example.piepongwong.friendlybooks;

import android.provider.BaseColumns;

/**
 * Created by Piepongwong on 4-4-2018.
 */

public final class BooksContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private BooksContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_ISBN_10 = "isbn_10";
        public static final String COLUMN_NAME_ISBN_13 = "isbn_13";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_SMALL_THUMBNAIL = "small_thumbnail";
        public static final String COLUMN_NAME_THUMBNAIL = "thumbnail";
        public static final String COLUMN_NAME_UUID = "uuid";

    }

}
