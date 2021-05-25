package com.alitalhacoban.kitapligim.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.alitalhacoban.kitapligim.Activities.SpecificBookActivity;
import com.alitalhacoban.kitapligim.Adapters.BookCustomAdapter;
import com.alitalhacoban.kitapligim.Classes.Book;
import com.alitalhacoban.kitapligim.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;


public class to_do extends Fragment {

    public to_do() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    BookCustomAdapter customAdapter;

    ArrayList<Book> books;
    ArrayList<String> ids;

    Book book;

    private BookCustomAdapter.ClickListener clickListener;

    RecyclerView recyclerView;

    String currDocId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    public static int countToDo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_did, container, false);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        books = new ArrayList<>();
        ids = new ArrayList<>();

        readDataFromFireStore();
        countToDo = books.size();


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setOnClickListener();

        customAdapter = new BookCustomAdapter(books, clickListener, getContext());

        recyclerView.setAdapter(customAdapter);

        return view;
    }

    public void setOnClickListener() {
        clickListener = new BookCustomAdapter.ClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intentToSpecificBook = new Intent(getContext(), SpecificBookActivity.class);
                intentToSpecificBook.putExtra("name", books.get(position).getName());
                intentToSpecificBook.putExtra("author", books.get(position).getAuthor());
               // intentToSpecificBook.putExtra("image", books.get(position).getImageUri());
                intentToSpecificBook.putExtra("comment", books.get(position).getComment());
                intentToSpecificBook.putExtra("pageNum", books.get(position).getComment());
                intentToSpecificBook.putExtra("id", ids.get(position));
                intentToSpecificBook.putExtra("saveTo", books.get(position).getSaveTo());
                intentToSpecificBook.putExtra("pageNum", books.get(position).getPageNum());
                intentToSpecificBook.putExtra("ph", books.get(position).getPh());
             //   intentToSpecificBook.putExtra("imageName", books.get(position).getImageName());

                startActivity(intentToSpecificBook);
            }
        };
    }

    public void readDataFromFireStore() {

        FirebaseUser currUser = mAuth.getCurrentUser();

        String collectionPath = currUser.getEmail();

        CollectionReference collectionReference = firestore.collection(collectionPath);

        String SAVE_TO = "todo";
        collectionReference.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("saveTo", SAVE_TO).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {

                        Map<String, Object> data = snapshot.getData();

                        if (data != null) {

                            String bookname = data.get("name").toString();
                            String email = data.get("email").toString();
                            String author = data.get("author").toString();

                            String comment = data.get("comment").toString();
                            String ph = data.get("ph").toString();
                            String pageNum = data.get("pageNum").toString();
                            String saveTo = data.get("saveTo").toString();

                            currDocId = snapshot.getId();
                            ids.add(currDocId);

                            Book newBook = new Book(bookname, author, comment, pageNum, ph, saveTo);

                            books.add(newBook);

                            customAdapter.notifyDataSetChanged();
                        }

                    }

                    countToDo = value.getDocuments().size();
                }
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.options_menu_in_main_page, menu);

    }


}