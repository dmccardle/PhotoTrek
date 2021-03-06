package ca.unb.mobiledev.phototrek;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class AlbumListActivity extends AppCompatActivity {

    private GridLayoutManager mAlbumLayoutManager;
    private AlbumListRecyclerAdapter mAlbumListRecyclerAdapter;
    //private String albumTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_new_photo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                addNewAlbum();
            }
        });

        displayAlbums();
    }

    private void addNewAlbum(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Name Album:");
        //alert.setMessage("Message");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // adding album with name input
                Album newAlbum = DataManager.getInstance().createAlbum(input.getText().toString());
                DataManager.getInstance().addAlbum(newAlbum);
                refreshActivity();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void refreshActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void displayAlbums() {
        RecyclerView mRecyclerAlbums = (RecyclerView) findViewById(R.id.album_list);
        mAlbumLayoutManager = new GridLayoutManager(this, 2);

        List<Album> albums = DataManager.getInstance().getAlbums();
        mAlbumListRecyclerAdapter = new AlbumListRecyclerAdapter(this, albums);

        mRecyclerAlbums.setLayoutManager(mAlbumLayoutManager);
        mRecyclerAlbums.setAdapter(mAlbumListRecyclerAdapter);
    }

    // Uses the res/menu/menu_maps.xml resource to populate the actions.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    // Handles clicks on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_mapview) {
            openFullmap();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFullmap() {
        Intent intent = new Intent(AlbumListActivity.this, MapActivity.class);
        startActivity(intent);
    }
}