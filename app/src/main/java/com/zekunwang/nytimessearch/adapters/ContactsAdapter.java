    package com.zekunwang.nytimessearch.adapters;

import com.bumptech.glide.Glide;
import com.zekunwang.nytimessearch.R;
import com.zekunwang.nytimessearch.models.Article;
import com.zekunwang.nytimessearch.models.Doc;
import com.zekunwang.nytimessearch.models.Multimedium;
import com.zekunwang.nytimessearch.models.ViewHolderWithPic;
import com.zekunwang.nytimessearch.models.ViewHolderWithoutPic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Store a member variable for the contacts
    private List<Article> contacts;
    // Store the context for easy access
    private Context context;
    // Define listener member variable
    private static OnItemClickListener listener;
    private final int PIC = 0, TXT = 1;

    public static final double RATIO = 1 / 4.5;
    double width;
    int height;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public static OnItemClickListener getListener() {
        return listener;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

        // set size of each grid view item dynamically
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels * RATIO;
        height = metrics.heightPixels;
    }

    // Pass in the contact array into the constructor
    public ContactsAdapter(Context context, List<Article> contacts) {
        this.contacts = contacts;
        this.context = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }


    @Override
    public int getItemViewType(int position) {
        if (contacts.get(position).thumbNail != null) {
            return PIC;
        }
        return TXT;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // distinguish view by viewType
        if (viewType == PIC) {
            View contactView = inflater.inflate(R.layout.item_article_with_pic, parent, false);
            viewHolder = new ViewHolderWithPic(contactView);
        } else {
            View contactView = inflater.inflate(R.layout.item_article_without_pic, parent, false);
            viewHolder = new ViewHolderWithoutPic(contactView);
        }
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // get data item for position
        Article contact = contacts.get(position);
        // set data
        if (viewHolder.getItemViewType() == PIC) {
            ViewHolderWithPic viewHolderWithPic = (ViewHolderWithPic) viewHolder;
            viewHolderWithPic.getTvTitle().setText(contact.headLine);

            Glide.with(getContext()).load(contact.thumbNail)
                    .override((int) width, height)
                    .into(viewHolderWithPic.getIvImage());
        } else {
            ViewHolderWithoutPic viewHolderWithoutPic = (ViewHolderWithoutPic) viewHolder;
            viewHolderWithoutPic.getTvTitle().setWidth((int) width);
            viewHolderWithoutPic.getTvTitle().setText(contact.headLine);
            viewHolderWithoutPic.getTvContent().setText(contact.snippet);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void clear() {
        contacts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Article> list) {
        if (list != null) {
            contacts.addAll(list);
            notifyDataSetChanged();
        }
    }
}
