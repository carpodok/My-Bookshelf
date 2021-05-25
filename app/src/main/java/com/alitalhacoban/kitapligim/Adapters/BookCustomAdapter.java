package com.alitalhacoban.kitapligim.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.alitalhacoban.kitapligim.Activities.MainActivity;
import com.alitalhacoban.kitapligim.Classes.Book;
import com.alitalhacoban.kitapligim.Classes.Firebase;
import com.alitalhacoban.kitapligim.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BookCustomAdapter extends RecyclerView.Adapter<BookCustomAdapter.BookHolder>  {

    private Context context;

    private ArrayList<String> booknameList;
    private ArrayList<String> bookAuthorList;
    private ArrayList<String> bookImageList;

    private ArrayList<Book> books;
    private ArrayList<Book> booksFull;

    private ClickListener clickListener;

    public BookCustomAdapter(ArrayList<String> bookImageList, ArrayList<String> booknameList, ArrayList<String> bookAuthorList,ClickListener clickListener,Context context) {
        this.bookImageList = bookImageList;
        this.booknameList = booknameList;
        this.bookAuthorList = bookAuthorList;
        this.clickListener = clickListener;
        this.context = context;
    }

    public BookCustomAdapter(ArrayList<Book> books,ClickListener clickListener,Context context) {
        this.books = books;
        this.clickListener = clickListener;
        this.context = context;
        booksFull = new ArrayList<>(books);
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //View view = inflater.inflate(R.layout.recycler_row, parent, false);

        View view = inflater.inflate(R.layout.recycler_cardview,parent,false);
       // View view = inflater.inflate(R.layout.recycler_row,parent,false);
        return new BookHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {

        holder.name.setText(books.get(position).getName());
        holder.author.setText(books.get(position).getAuthor());

       /* if (!books.get(position).getImageUri().equals("-")){
           // Picasso.get().load(books.get(position).getImageUri()).into(holder.imageView);

            loadImage(context.getApplicationContext(), books.get(position).getImageUri(), holder.imageView);
        }
*/


    }


    public void loadImage(Context context,String uri,ImageView imageView){

        Glide.with(context)
                .load(uri)
                .into(imageView);

    }

    @Override
    public int getItemCount() {

        return books.size();
    }

    class BookHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

       // ImageView imageView;
        TextView name, author;

        public BookHolder(@NonNull View itemView) {
            super(itemView);

         //   imageView = itemView.findViewById(R.id.cardView_imageView);
            name = itemView.findViewById(R.id.cardView_nameText);
            author = itemView.findViewById(R.id.cardView_authorText);

            /*imageView = itemView.findViewById(R.id.recyclerView_row_bookimage);
            name = itemView.findViewById(R.id.recyclerView_row_bookname_text);
            author = itemView.findViewById(R.id.recyclerView_row_bookauthor_text);*/
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v,getAdapterPosition());
        }
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }
}

