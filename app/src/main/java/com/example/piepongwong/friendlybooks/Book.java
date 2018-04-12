package com.example.piepongwong.friendlybooks;

/**
 * Created by Piepongwong on 8-4-2018.
 */

public class Book {
    String title;
    String author;
    String isbn_10;
    String isbn_13;
    String description;
    String smallThumbnail;
    String thumbnail;
    String uuid;

    Book(String title, String author, String isbn_10, String isbn_13, String description,
         String smallThumbnail, String thumbnail, String uuid) {
        this.title = title;
        this.author = author;
        this.isbn_10 = isbn_10;
        this.isbn_13 = isbn_13;
        this. description = description;
        this.smallThumbnail = smallThumbnail;
        this.thumbnail = thumbnail;
        this.uuid = uuid;
    }
}
