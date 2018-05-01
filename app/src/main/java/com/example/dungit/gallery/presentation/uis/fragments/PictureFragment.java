package com.example.dungit.gallery.presentation.uis.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.entities.EMODE;
import com.example.dungit.gallery.presentation.entities.ListPhotoSameDate;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.activities.MainActivity;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterInnerRecyclerView;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterRecyclerView;
import com.example.dungit.gallery.presentation.uis.callbacks.PictureFragCallback;

import java.util.ArrayList;

/**
 * Created by DUNGIT on 4/22/2018.
 */
public class PictureFragment extends Fragment implements PictureFragCallback ,SearchView.OnQueryTextListener{
    private static final String KEY_LIST_PHOTO = "list_photo";
    private static final String KEY_MODE = "mode_preview_photo";
    private ArrayList<ListPhotoSameDate> lstPhotoSameDate;
    private ArrayList<Photo> lstPhoto;
    private EMODE mode;

    private MainActivity main;
    private RecyclerView rvWrapper;
    private AdapterRecyclerView adapterRecyclerView;
    private AdapterInnerRecyclerView adapterInnerRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private boolean gridMode=false;

    SearchView searchView;

    public static PictureFragment newInstance(ArrayList<ListPhotoSameDate> lstPhoto) {
        PictureFragment fragment = new PictureFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_LIST_PHOTO, lstPhoto);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity) getActivity();
        lstPhotoSameDate = (ArrayList<ListPhotoSameDate>) getArguments().getSerializable(KEY_LIST_PHOTO);

        lstPhoto = new ArrayList<>();
        for (ListPhotoSameDate listPhotoSameDate : lstPhotoSameDate) {
            lstPhoto.addAll(listPhotoSameDate.getLstPhotoHaveSameDate());
        }
        mode = EMODE.MODE_BY_DATE;

        linearLayoutManager = new LinearLayoutManager(main);
        gridLayoutManager = new GridLayoutManager(main, 4);
        adapterRecyclerView = new AdapterRecyclerView(main, lstPhotoSameDate);
        adapterInnerRecyclerView = new AdapterInnerRecyclerView(main, lstPhoto);

        main.getDBHelper().addObserver(adapterInnerRecyclerView);
        main.getDBHelper().addObserver(adapterRecyclerView);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_fragment, container, false);

        rvWrapper = view.findViewById(R.id.rv_wrapper);
        rvWrapper.setHasFixedSize(true);

        rvWrapper.setLayoutManager(linearLayoutManager);
        rvWrapper.setAdapter(adapterRecyclerView);

        return view;
    }

    @Override
    public void onChangeView(EMODE mode) {
        this.mode = mode;
        switch (mode) {
            case MODE_BY_DATE:
                gridMode=false;
                searchView.setQueryHint("Tìm kiếm theo ngày tháng năm");
                rvWrapper.setLayoutManager(linearLayoutManager);
                rvWrapper.setAdapter(adapterRecyclerView);
                break;
            case MODE_GRID:
                gridMode=true;
                searchView.setQueryHint("Tìm kiếm theo tên");
                boolean isSwitched = adapterRecyclerView.getViewType();
                if(isSwitched)
                    rvWrapper.setLayoutManager(gridLayoutManager);
                else
                    rvWrapper.setLayoutManager(linearLayoutManager);
                rvWrapper.setAdapter(adapterInnerRecyclerView);
                break;
        }
    }

    @Override
    public void onChangeDataView(ArrayList<ListPhotoSameDate> listPhotoByDate, ArrayList<Photo> lstPhoto) {
        this.lstPhotoSameDate = listPhotoByDate;
        this.lstPhoto = lstPhoto;
        adapterRecyclerView.setData(this.lstPhotoSameDate);
        adapterInnerRecyclerView.setData(this.lstPhoto);

        switch (mode) {
            case MODE_BY_DATE:
                adapterRecyclerView.notifyDataSetChanged();
                break;
            case MODE_GRID:
                adapterInnerRecyclerView.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu_picture, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        if(gridMode) {

        }
        else {
            searchView.setQueryHint("Tìm kiếm theo ngày");
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.act_viewType:
                onChangeViewType();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Ham cho changeviewtype
    public void onChangeViewType()
    {
        boolean isSwitched = adapterRecyclerView.toggle();
        adapterRecyclerView.setLayout(isSwitched);
        adapterRecyclerView.NotifyChange();
        if(gridMode)
        {
            if(isSwitched)
                rvWrapper.setLayoutManager(gridLayoutManager);
            else
                rvWrapper.setLayoutManager(linearLayoutManager);
        }

    }

    //Hàm cho filter

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        if(gridMode) {
            adapterInnerRecyclerView.getFilter().filter(newText);
        }
        else {
            adapterRecyclerView.getFilter().filter(newText);
        }
        return false;
    }


}