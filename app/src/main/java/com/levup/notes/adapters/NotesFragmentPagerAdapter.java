package com.levup.notes.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.levup.notes.fragments.NotesFragment;
import com.levup.notes.model.Note;

import java.util.List;

/**
 * Created by Smaug on 23.11.2016.
 */

public class NotesFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<Note> mDataSource = null;
    public NotesFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        long id = mDataSource.get(position).getId();
        return NotesFragment.newInstance(id);
    }

    @Override
    public int getCount() {
        return mDataSource == null ? 0 : mDataSource.size();
    }

    public void setDataSource(List<Note> dataSource) {
        mDataSource = dataSource;
        notifyDataSetChanged();

    }
}
