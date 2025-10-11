package com.jason.microstream.ui.contact;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.jason.microstream.R;
import com.jason.microstream.ui.compenent.recyclerview.BasicAdapter;

import java.util.ArrayList;

public class ContactAdapter extends BasicAdapter<ContactHolder, ContactHolder.Item> {


    public ContactAdapter(ArrayList<ContactHolder.Item> items, ItemClickListener<ContactHolder.Item> itemClickListener) {
        super(items, itemClickListener);
    }

    @Override
    public ContactHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactHolder(parent, R.layout.item_contact_user, itemClickListener, itemChildClickListener);
    }

}
