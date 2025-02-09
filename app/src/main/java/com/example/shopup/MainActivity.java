package com.example.shopup;

import android.os.Bundle;

import android.content.Context;
import android.content.DialogInterface;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private Button               scanButton;
    private Button               addItemButton;
    private EditText             itemEditText;
    private ListView             itemListView;
    private ArrayList<String>    itemList;
    private ArrayAdapter<String> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanButton    = findViewById(R.id.scanButton);
        addItemButton = findViewById(R.id.addItemButton);
        itemEditText  = findViewById(R.id.itemEditText);
        itemListView  = findViewById(R.id.itemListView);

        itemList      = new ArrayList<>();
        itemAdapter   = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        itemListView.setAdapter(itemAdapter);

        // Add a shopping item when the button is clicked...
        addItemButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SaveItemToList();
            }
        });

        // Activate Barcode/QR Code scanner...
        scanButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                UseScanner();
            }
        });


        // Delete item with long press...
        itemListView.setOnItemLongClickListener((parent, view, position, id) ->
        {
            // Display toast for when item is removed...
            Context context = getApplicationContext();
            Toast.makeText(context, "Item Removed", Toast.LENGTH_LONG).show();

            itemList.remove(position);
            itemAdapter.notifyDataSetChanged();
            return true;
        });
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->
    {
        String contents = result.getContents();
        if (contents != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Scan Successful");
            builder.setMessage("You are using trolley - " + contents + "\n\n Transferring shopping list...");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });


    private void UseScanner()
    {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Press volume up to turn on flash.");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureActivity.class);
        barLauncher.launch(options);
    }

    private void SaveItemToList()
    {
        String item = itemEditText.getText().toString().trim();
        if (!item.isEmpty()) {
            itemList.add(item);
            itemAdapter.notifyDataSetChanged();  // Refresh the displayed list
            itemEditText.setText("");            // Clear the input field
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_ENTER: // Save item to list on pressing Enter...
                SaveItemToList();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }

    }


}
