package com.example.sendsms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class MainActivity extends AppCompatActivity {

    //Instanciacion
    EditText etCel;
    Button btnEnviar;
    Button btnGps;
    TextView tvLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Carga del layout al activity (front)
        setContentView(R.layout.activity_main);

        //Asignación de las variables
        etCel = findViewById(R.id.editTextTextPersonName2);
        btnEnviar = findViewById(R.id.button);
        tvLocation = findViewById(R.id.tvUbicacion);
        btnGps = findViewById(R.id.button2);

        //Permisos para enviar SMS y utilizar GPS
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, 1);
        }

        //Activar GPS
        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Listener que responda al momento de haber un cambio en la ubicación
                LocationListener locationListener = new LocationListener() {
                    @SuppressLint("SetTextI18n")
                    public void onLocationChanged(Location location) {
                        tvLocation.setText("Latitud: " + location.getLatitude() + " / Longitud: " + location.getLongitude());
                    }
                };

                // Verificacion de los permisos
                try {
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                    LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
                    // Registro del listener sobre el LocationManager para obtener la nueva ubicación
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }catch(Exception e){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }

            }
        });

        //Enviar mensaje
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message=tvLocation.getText().toString();

                DoBackgroundTask b1 = new DoBackgroundTask();
                b1.execute(message);

            }
        });

    }
public void send_data(View v){
        String message=tvLocation.getText().toString();

        DoBackgroundTask b1 = new DoBackgroundTask();
        b1.execute(message);


}

class DoBackgroundTask extends AsyncTask<String, Void, Void>
{     Socket s;
     PrintWriter writer;

    @Override
    protected Void doInBackground(String... voids){
        //Tcp
        String message = voids[0];
        try{
            s= new Socket("192.168.1.62",9090);
            writer = new PrintWriter(s.getOutputStream());
            writer.write(message);
            writer.flush();
            writer.close();
            s.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        //Aquí UDP
        try {
            String messageStr = voids[0];
            int server_port = 9090;
            InetAddress local = InetAddress.getByName("192.168.1.62");
            int msg_length = messageStr.length();
            byte[] messageu = messageStr.getBytes();


            DatagramSocket su = new DatagramSocket();
            //

            DatagramPacket p = new DatagramPacket(messageu, msg_length, local, server_port);
            su.send(p);//properly able to send data. i receive data to server
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}


}