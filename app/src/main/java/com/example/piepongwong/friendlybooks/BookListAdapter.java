package com.example.piepongwong.friendlybooks;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Created by Piepongwong on 10-4-2018.
 */

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {
    private List<Book> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView bookTitle;
        public TextView bookAuthor;
        public TextView bookIsbn13;
        public ImageView thumbnail;
        public ViewGroup container;

        public ViewHolder(View v) {
            super(v);
            bookTitle = (TextView) v.findViewById(R.id.bookTitle);
            bookAuthor = (TextView) v.findViewById(R.id.bookAuthor);
            bookIsbn13 = (TextView) v.findViewById(R.id.isbn13);
            thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            container = (ViewGroup) thumbnail.getParent();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BookListAdapter (List<Book> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BookListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_item, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bookTitle.setText(mDataset.get(position).title);
        holder.bookAuthor.setText(mDataset.get(position).author);
        holder.bookIsbn13.setText(mDataset.get(position).isbn_13);
        holder.bookIsbn13.setTag(mDataset.get(position).uuid);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID uuid = UUID.fromString(v.findViewById(R.id.isbn13).getTag().toString());
                BooksDbHelper db = new BooksDbHelper(v.getContext());
                db.deleteBook(uuid);
            }
        });

        File directory = holder.thumbnail.getContext().getDir("images", Context.MODE_PRIVATE);
        File mypath = new File(directory,mDataset.get(position).uuid + ".jpg");
        boolean exist = mypath.exists();
        Log.i("Image path", mypath.toString());

       holder.thumbnail.setImageDrawable(Drawable.createFromPath(mypath.toString()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
