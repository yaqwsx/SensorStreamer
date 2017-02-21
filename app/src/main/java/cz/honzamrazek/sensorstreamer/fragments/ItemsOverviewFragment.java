package cz.honzamrazek.sensorstreamer.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import cz.honzamrazek.sensorstreamer.R;
import cz.honzamrazek.sensorstreamer.SharedStorageManager;
import cz.honzamrazek.sensorstreamer.adapters.ItemOverviewAdapter;
import cz.honzamrazek.sensorstreamer.models.Descriptionable;

abstract public class ItemsOverviewFragment<T extends  Descriptionable> extends Fragment {
    private SharedStorageManager<T> mConnectionManager;
    private ItemOverviewAdapter<T> mAdapter;

    private Class<T> mItemClass;
    private int mEmptyMessage;

    public ItemsOverviewFragment(Class<T> itemClass, int emptyMessage) {
        mItemClass = itemClass;
        mEmptyMessage = emptyMessage;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items_overview, container, false);

        TextView emptyText = (TextView) view.findViewById(android.R.id.empty);
        emptyText.setText(mEmptyMessage, TextView.BufferType.NORMAL);

        ListView mList = (ListView) view.findViewById(R.id.connection_list);
        mList.setEmptyView(emptyText);

        mConnectionManager = new SharedStorageManager<>(this.getContext(), mItemClass);
        mAdapter = new ItemOverviewAdapter<>(getActivity(), mConnectionManager.getItems());
        registerForContextMenu(mList);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onEditItem(position);
            }
        });

        FloatingActionButton mAddButton = (FloatingActionButton) view.findViewById(R.id.add_connection);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewItem();
            }
        });

        return view;
    }

    public abstract void editItem(int position);
    public abstract void createNewItem();

    @Override
    public void onResume() {
        super.onResume();
        mConnectionManager.reload();
        mAdapter.notifyDataSetChanged();
    }

    private void onEditItem(int position) {
        editItem(position);
        mConnectionManager.reload();
        mAdapter.notifyDataSetChanged();
    }

    private void onDeleteItem(int position) {
        mConnectionManager.remove(position);
        mConnectionManager.commit();
        mAdapter.notifyDataSetChanged();
    }

    private void onCreateNewItem() {
        createNewItem();
        mConnectionManager.reload();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch(v.getId()) {
            case R.id.connection_list: {
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.edit_delete, menu);
                break;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit: {
                AdapterView.AdapterContextMenuInfo info
                        = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                onEditItem(info.position);
                return true;
            }
            case R.id.delete: {
                AdapterView.AdapterContextMenuInfo info
                        = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                onDeleteItem(info.position);
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    }
}
