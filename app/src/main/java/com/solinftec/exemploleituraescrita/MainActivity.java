package com.solinftec.exemploleituraescrita;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {


    Button btnSalvar;
    Button btnSalvarExterno;
    Button btnSalvarCache;
    Button btnLeitura;
    EditText edtTxtSalvar;
    String content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponentes();

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = "MyFile";
                content = edtTxtSalvar.getText().toString();

                FileOutputStream outputStream = null;
                try {
                    outputStream = openFileOutput(fileName, Context.MODE_PRIVATE); //Modo private para que so esse aplicativo tenha acesso ao aplicativo salvando apenas localmente
                    outputStream.write(content.getBytes());
                    outputStream.close();
                    Toast.makeText(getApplicationContext(), "Salvo com sucesso !", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnSalvarCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file;
                content = edtTxtSalvar.getText().toString();

                FileOutputStream outputStream = null;
                try {

                    file = new File(getCacheDir(), "MyCache");

                    outputStream = new FileOutputStream(file);
                    outputStream.write(content.getBytes());
                    outputStream.close();
                    Toast.makeText(getApplicationContext(), "Salvo com sucesso !", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnLeitura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = null;
                BufferedReader input = null;

                try {
                    file = new File(getCacheDir(), "MyCache");

                    input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = input.readLine()) != null) {
                        buffer.append(line);
                    }
                    Log.d("BUFFER", buffer.toString());
                    Toast.makeText(getApplicationContext(), "Arquivo recuperado: " + buffer.toString(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnSalvarExterno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = "hello world";
                File file;
                FileOutputStream outputStream;
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    try {
                        file = new File(Environment.getExternalStorageDirectory(), "MyCache");

                        outputStream = new FileOutputStream(file);
                        outputStream.write(content.getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Dispositivo de armazenamento não está disponivel ! " , Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void inicializarComponentes() {
        btnSalvar = findViewById(R.id.btnSalvar);
        btnSalvarCache = findViewById(R.id.btnSalvarCache);
        btnLeitura = findViewById(R.id.btnLeitura);
        edtTxtSalvar = findViewById(R.id.edtTxtSalvar);
        btnSalvarExterno = findViewById(R.id.btnSalvarExterno);
    }
}
