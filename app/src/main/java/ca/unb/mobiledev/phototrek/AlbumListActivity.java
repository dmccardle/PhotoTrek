package ca.unb.mobiledev.phototrek;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class AlbumListActivity extends AppCompatActivity {

    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataManager = new DataManager(this);

        FloatingActionButton fab = findViewById(R.id.fab_new_photo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewAlbum();
            }
        });

        displayAlbums();
    }

    @Override
    protected void onRestart() {
        this.refreshActivity();
        super.onRestart();
    }

    private void addNewAlbum() {
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlbumCreationDialog));
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_album, null);
        alert.setView(dialogView);
        EditText input = dialogView.findViewById(R.id.txtAlbumName);
        alert.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Album newAlbum = new Album(input.getText().toString());
                dataManager.addAlbum(newAlbum);
                refreshActivity();
            }
        });

        alert.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void refreshActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void displayAlbums() {
        RecyclerView recyclerAlbums = (RecyclerView) findViewById(R.id.album_list);
        TextView noAlbumsTextView = (TextView) findViewById(R.id.noAlbumsTextView);
        GridLayoutManager albumLayoutManager = new GridLayoutManager(this, 2);

        List<Album> albums = dataManager.getAllAlbums();
        if (albums.isEmpty()) {
            recyclerAlbums.setVisibility(View.GONE);
            noAlbumsTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerAlbums.setVisibility(View.VISIBLE);
            noAlbumsTextView.setVisibility(View.GONE);
        }

        AlbumListRecyclerAdapter albumListRecyclerAdapter = new AlbumListRecyclerAdapter(this, albums);

        recyclerAlbums.setLayoutManager(albumLayoutManager);
        recyclerAlbums.setAdapter(albumListRecyclerAdapter);
    }

    // Uses the res/menu/menu_albums.xml resource to populate the actions.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_albums, menu);
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