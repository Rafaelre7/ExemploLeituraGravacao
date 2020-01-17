package com.solinftec.exemploleituraescrita;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.solinftec.exemploleituraescrita.util.Permissao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    Button btnSalvar;
    Button btnSalvarExterno;
    Button btnSalvarCache;
    Button btnLeitura;
    Button btnCaminhoUSB;
    EditText edtTxtSalvar;
    String content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponentes();
        enablePermissions();

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
                String content = "Teste salvamento externo";
                File file;
                FileOutputStream outputStream;
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    try {
                        file = new File(Environment.getExternalStorageDirectory(), "MyCache"); //Nesta linha salva na raiz

//                        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "MyCache"); //Nesta linha salva no diretorio especifico

//                        file = new File(Environment.getExternalStoragePublicDirectory(getExternalFilesDir(Environment.MEDIA_MOUNTED)), "MyCache"); //Nesta linha salva no diretorio especifico

                        outputStream = new FileOutputStream(file);
                        outputStream.write(content.getBytes());
                        outputStream.close();

                        Toast.makeText(getApplicationContext(), "Salvo com sucesso !", Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Dispositivo de armazenamento não está disponivel ! ", Toast.LENGTH_LONG).show();
                }


            }
        });

        btnCaminhoUSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file;

                file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                MediaScannerConnection.scanFile(getApplicationContext(),
                        new String[]{file.toString()}, null,
                        new MediaScannerConnection.MediaScannerConnectionClient() {
                            @Override
                            public void onMediaScannerConnected() {

                            }

                            @Override
                            public void onScanCompleted(String s, Uri uri) {
                                Toast.makeText(getApplicationContext(), "External Storage path: "+ uri + s, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void enablePermissions() {

        String permissoes[] = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        Permissao.permissao(this, 0, permissoes);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Aceite as permissões para o aplicativo funcionar corretamete", Toast.LENGTH_LONG).show();
                finish();

                break;
            }
        }

    }

    private void inicializarComponentes() {
        btnSalvar = findViewById(R.id.btnSalvar);
        btnSalvarCache = findViewById(R.id.btnSalvarCache);
        btnLeitura = findViewById(R.id.btnLeitura);
        edtTxtSalvar = findViewById(R.id.edtTxtSalvar);
        btnSalvarExterno = findViewById(R.id.btnSalvarExterno);
        btnCaminhoUSB = findViewById(R.id.btnCaminhoUSB);
    }
}
