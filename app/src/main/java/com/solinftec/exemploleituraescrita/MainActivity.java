package com.solinftec.exemploleituraescrita;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.solinftec.exemploleituraescrita.util.CopyTaskParam;
import com.solinftec.exemploleituraescrita.util.Helper;
import com.solinftec.exemploleituraescrita.util.HomeCallback;
import com.solinftec.exemploleituraescrita.util.Permissao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements HomeCallback {


    private Button btnSalvar, btnSalvarPendrive, btnLerUsb;
    private Button btnSalvarExterno;
    private Button btnSalvarCache;
    private Button btnLeitura;
    private EditText edtTxtSalvar, edtCaminhoiUsb;
    private String content;

    private String TAG = "OTGViewer";
    private static final String ACTION_USB_PERMISSION = "com.solinftec.exemploleituraescrita.USB_PERMISSION";
    private List<UsbDevice> mDetectedDevices;
    private UsbManager mUsbManager;
    private UsbMassStorageDevice mUsbMSDevice;
    private PendingIntent mPermissionIntent;

    private Byte[] bytes;
    private static int TIMEOUT = 0;
    private boolean forceClaim = true;


    private CopyTaskParam param;
    FileSystem currentFs;


    //    private UsbDevice device = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
//                            device.getDeviceName()
                            connectDevice(device);
                        }
                    } else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mDetectedDevices = new ArrayList<UsbDevice>();

        inicializarComponentes();
        enablePermissions();
        buttonClick();
    }

    private void buttonClick() {

        //Leitura em arquivos cache
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



        btnSalvarExterno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = "Teste salvamento externo";
                File file;
                FileOutputStream outputStream;
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    try {
                        file = new File(Environment.getExternalStorageDirectory() + "/Trabalho/Solinftec/Logs", "Log" + Helper.retornarData()); //Nesta linha salva na raiz

//                        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "MyCache"); //Nesta linha salva no diretorio especifico

//                        file = new File(Environment.getExternalStoragePublicDirectory(getExternalFilesDir(Environment.MEDIA_MOUNTED)), "MyCache"); //Nesta linha salva no diretorio especifico

                        outputStream = new FileOutputStream(file);
                        //                        outputStream.write(content.getBytes());
                        outputStream.write(edtTxtSalvar.getText().toString().getBytes());
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

        btnSalvarPendrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {


//                Log.e(TAG,"TESTE: "+mUsbMSDevice.getUsbDevice().getDeviceName());
//                Log.e(TAG,"TESTE: "+mUsbMSDevice.getMassStorageDevices(getBaseContext()));
                    UsbFile root = currentFs.getRootDirectory();

                    UsbFile[] files = root.listFiles();
                    for (UsbFile file : files) {
                        Log.d(TAG, file.getName());
//                        if (file.isDirectory()) {
//                            Log.d(TAG, "Arquivo: " + file.getLength());
//
//                        }
                    }
                    UsbFile newDir;
                    UsbFile file = null;
//                    if (Objects.requireNonNull(root.search("Logs")).isDirectory()) {
                    newDir = root.createDirectory("Logs");
                    file = newDir.createFile("log" + Helper.retornarData() + ".txt");
//                    }


                    //escrever no arquivo
                    OutputStream os = new UsbFileOutputStream(file);

                    os.write("Rafa".getBytes());
                    os.close();


                    Toast.makeText(getApplicationContext(), "Salvo com sucesso !", Toast.LENGTH_LONG).show();
                    // Lendo o arquivo
                    InputStream is = new UsbFileInputStream(file);

                    StringBuffer b = new StringBuffer();
                    byte[] buffer = new byte[currentFs.getChunkSize()];
                    is.read(buffer);
                    b.append(buffer);

//                    param.from.read(1, ByteBuffer.wrap(buffer));




//                    Log.e("Rec", "Arquivo recuperado: " + buffer.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mUsbMSDevice.close();

            }
        });

        btnLerUsb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = null;
                BufferedReader input = null;

                try {
                    file = new File(String.valueOf(getUsbDevices().get(0).getDeviceName()), "/Logs");

                    input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = input.readLine()) != null) {
                        buffer.append(line);
                    }
                    Toast.makeText(getApplicationContext(), "Arquivo recuperado: " + buffer.toString(), Toast.LENGTH_LONG).show();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        });
    }


    private void connectDevice(UsbDevice device) {

        if (mUsbManager.hasPermission(device))
            Log.d(TAG, "Obteve permissão!");

        //Consultar se tem dispositivo de armazenamento disponíveis
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(this);
        if (devices.length > 0) {
            mUsbMSDevice = devices[0];

        }
        try {
            for (UsbMassStorageDevice devic : devices) {
                devic.init();
                currentFs = devic.getPartitions().get(0).getFileSystem();
                Log.d(TAG, "Lista de arquivos: " + currentFs.getRootDirectory().getAbsolutePath());

                Log.d(TAG, "Lista de arquivos: " + Arrays.toString(new boolean[]{Arrays.toString(currentFs.getRootDirectory().listFiles()).contains("Unip")}));
                Log.d(TAG, "Capacity: " + currentFs.getCapacity());
                Log.d(TAG, "Occupied Space: " + currentFs.getOccupiedSpace());
                Log.d(TAG, "Free Space: " + currentFs.getFreeSpace());
                Log.d(TAG, "Chunk size: " + currentFs.getChunkSize());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void checkUsbStatus() {

        mDetectedDevices.clear();
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        if (mUsbManager != null) {
            HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

            if (!deviceList.isEmpty()) {
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                while (deviceIterator.hasNext()) {
                    UsbDevice device = deviceIterator.next();
                    Toast.makeText(getApplicationContext(), "Device name: " + device.getDeviceName() + ", Product name: " + device.getProductName(), Toast.LENGTH_LONG).show();

                    mDetectedDevices.add(device);
                    mUsbManager.requestPermission(device, mPermissionIntent);
//                    edtTxtSalvar.setText(device.getProductName());
                    edtCaminhoiUsb.setText(device.getDeviceName());
                }
            }

        }

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
        btnSalvarPendrive = findViewById(R.id.btnSalvarPenDrive);
        btnSalvarCache = findViewById(R.id.btnSalvarCache);
        btnLeitura = findViewById(R.id.btnLeitura);
        edtTxtSalvar = findViewById(R.id.edtTxtSalvar);
        edtCaminhoiUsb = findViewById(R.id.edtCaminhoUsb);
        btnSalvarExterno = findViewById(R.id.btnSalvarExterno);
        btnLerUsb = findViewById(R.id.btnLerUsb);
    }

    @Override
    public List<UsbDevice> getUsbDevices() {
        return mDetectedDevices;
    }

//    @Override
//    public void requestPermission(int pos) {
//        mUsbManager.requestPermission(mDetectedDevices.get(pos), mPermissionIntent);
//    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);

        registerReceiver(usbReceiver, filter);
        checkUsbStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
        Helper.deleteCache(getCacheDir());
    }
}
