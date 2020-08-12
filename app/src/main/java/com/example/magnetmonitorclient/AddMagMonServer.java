package com.example.magnetmonitorclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddMagMonServer extends AppCompatActivity {

    Button addBut, cancelBut;
    MagMonServer newServer = new MagMonServer();
    EditText nameEdit,ipEdit,portEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mag_mon_server);
        setTitle("Add MagMon Server");

        nameEdit = findViewById(R.id.name);
        ipEdit = findViewById(R.id.ip);
        portEdit = findViewById(R.id.port);

        addBut = findViewById(R.id.addnew);
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((!nameEdit.getText().toString().equals("")) & (!ipEdit.getText().toString().equals("")) & (!portEdit.getText().toString().equals(""))){
                    newServer.setName(nameEdit.getText().toString());
                    newServer.setPort(portEdit.getText().toString());
                    newServer.setIP(ipEdit.getText().toString());
                    MainActivity.serverAddBase(newServer);
                    startService(new Intent(AddMagMonServer.this,MagMonService.class));
                    MainActivity.adapter.notifyDataSetChanged();
                    finish();
                }else{
                    if(nameEdit.getText().toString().equals("")){
                        Toast toast = Toast.makeText(AddMagMonServer.this, "Name Empty", Toast.LENGTH_SHORT); toast.show();
                    }
                    if(portEdit.getText().toString().equals("")){
                        Toast toast = Toast.makeText(AddMagMonServer.this, "Port Empty", Toast.LENGTH_SHORT); toast.show();
                    }
                    if(ipEdit.getText().toString().equals("")){
                        Toast toast = Toast.makeText(AddMagMonServer.this, "IP Empty", Toast.LENGTH_SHORT); toast.show();
                    }
                }
            }
        });

        cancelBut = findViewById(R.id.cancel);
        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}