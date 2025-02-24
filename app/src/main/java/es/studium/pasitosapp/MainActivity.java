package es.studium.pasitosapp;

import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import androidx.core.app.ActivityCompat;
import java.util.HashMap;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnStart, btnHistorial;
    private LocationManager locationManager;
    private BaseDatos databaseHelper;
    private Handler handler = new Handler(Looper.getMainLooper());
    private HashMap<Integer, Marker> marcadoresMapa = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener el fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        // Inicializar la base de datos
        databaseHelper = new BaseDatos(this);

        // Botón para iniciar la recolección de datos
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarRecolecciónDatos();
            }
        });

        // Inicializar el administrador de ubicación
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        cargarMarcadoresDesdeBD();
    }

    private void iniciarRecolecciónDatos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                guardarDatos(location);
                verificarDatosGuardados();  // 🔍 Verificar si se están insertando datos duplicados
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        });

        Toast.makeText(this, "Recolección de datos activada cada 5 minutos", Toast.LENGTH_SHORT).show();
    }

    private void guardarDatos(Location location) {
        int nivelBateria = obtenerNivelBateria();
        long id = databaseHelper.insertarDatos(location.getLatitude(), location.getLongitude(), nivelBateria);
        Toast.makeText(this, "Datos guardados en SQLite", Toast.LENGTH_SHORT).show();

        // Agregar marcador en el mapa
        LatLng nuevaUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
        // 1. Cargar la imagen como un Bitmap (reemplaza "mi_imagen" con el nombre de tu archivo)
        Bitmap imagenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icono_marcador);

        // 2. Redimensionar el Bitmap
        int nuevoAncho = 200; // Define el nuevo ancho deseado (en píxeles)
        int nuevoAlto = 200; // Define el nuevo alto deseado (en píxeles)
        Bitmap imagenRedimensionada = Bitmap.createScaledBitmap(imagenBitmap, nuevoAncho, nuevoAlto, false);

        // 3. Crear un BitmapDescriptor a partir del Bitmap redimensionado
        BitmapDescriptor iconoPersonalizado = BitmapDescriptorFactory.fromBitmap(imagenRedimensionada);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(nuevaUbicacion)
                .title("Ubicación registrada")
                .icon(iconoPersonalizado);
        Marker marker = mMap.addMarker(markerOptions);
        marcadoresMapa.put((int) id, marker);
    }

    private int obtenerNivelBateria() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int nivel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        return nivel;
    }

    private void cargarMarcadoresDesdeBD() {
        Cursor cursor = databaseHelper.obtenerDatos();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                double latitud = cursor.getDouble(1);
                double longitud = cursor.getDouble(2);
                LatLng ubicacion = new LatLng(latitud, longitud);
                // 1. Cargar la imagen como un Bitmap (reemplaza "mi_imagen" con el nombre de tu archivo)
                Bitmap imagenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icono_marcador);

                // 2. Redimensionar el Bitmap
                int nuevoAncho = 200; // Define el nuevo ancho deseado (en píxeles)
                int nuevoAlto = 200; // Define el nuevo alto deseado (en píxeles)
                Bitmap imagenRedimensionada = Bitmap.createScaledBitmap(imagenBitmap, nuevoAncho, nuevoAlto, false);

                // 3. Crear un BitmapDescriptor a partir del Bitmap redimensionado
                BitmapDescriptor iconoPersonalizado = BitmapDescriptorFactory.fromBitmap(imagenRedimensionada);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(ubicacion)
                        .title("Registro Guardado")
                        .icon(iconoPersonalizado);
                Marker marker = mMap.addMarker(markerOptions);
                marcadoresMapa.put(id, marker);
            }
            cursor.close();

    }
    }
    private void verificarDatosGuardados() {
        Cursor cursor = databaseHelper.obtenerDatos();
        if (cursor != null) {
            Log.d("SQLite", "Registros en la base de datos:");
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                double latitud = cursor.getDouble(1);
                double longitud = cursor.getDouble(2);
                int bateria = cursor.getInt(3);
                String fecha = cursor.getString(4);

                Log.d("SQLite", "ID: " + id + " | Lat: " + latitud + " | Lng: " + longitud + " | Batería: " + bateria + "% | Fecha: " + fecha);
            }
            cursor.close();
        } else {
            Log.d("SQLite", "No hay datos en la base de datos");
        }
    }
    // Método para eliminar una ubicación y su marcador
    public void eliminarUbicacionYMarcador(int id) {
        // Eliminar de la base de datos
        databaseHelper.eliminarUbicacion(id);

        // Eliminar el marcador del mapa
        Marker marker = marcadoresMapa.get(id);
        if (marker != null) {
            marker.remove();
            marcadoresMapa.remove(id);
        }
    }
}