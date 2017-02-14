package cz.honzamrazek.sensorstreamer;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private class ResourceMenuItem {
        private String name;
        private long id;

        ResourceMenuItem(int resourceId) {
            Resources res = getResources();
            this.name = res.getString(resourceId);
            id = resourceId;
        }

        public String toString() {
            return name;
        }

        public long getId() {
            return id;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("CLICK", String.format("Pos: %d, Id: %d", position, id) );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        Resources res = getResources();

        mDrawerList.setAdapter(new ArrayAdapter<ResourceMenuItem>(this,
            R.layout.drawer_list_item, new ResourceMenuItem[] {
                new ResourceMenuItem(R.string.manage_connections),
                new ResourceMenuItem(R.string.manage_packets),
                new ResourceMenuItem(R.string.about)
            }) {
                @Override
                public long getItemId(int position) {
                    return getItem(position).getId();
                }
            });
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, R.string.drawer_open, R.string.drawer_close
            ) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setLogo(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
