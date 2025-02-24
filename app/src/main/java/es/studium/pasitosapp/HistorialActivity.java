package es.studium.pasitosapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private static BaseDatos databaseHelper;
    private RecyclerView recyclerView;
    private HistorialAdapter adapter;
    private List<Ubicacion> listaUbicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        databaseHelper = new BaseDatos(this);
        recyclerView = findViewById(R.id.recyclerViewHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener datos de SQLite
        listaUbicaciones = obtenerDatosGuardados();
        // Pasar el contexto al adapter
        adapter = new HistorialAdapter(listaUbicaciones, this);
        recyclerView.setAdapter(adapter);
    }

    private List<Ubicacion> obtenerDatosGuardados() {
        List<Ubicacion> lista = new ArrayList<>();
        Cursor cursor = databaseHelper.obtenerDatos();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                double latitud = cursor.getDouble(1);
                double longitud = cursor.getDouble(2);
                int bateria = cursor.getInt(3);
                String fecha = cursor.getString(4);

                lista.add(new Ubicacion(id, latitud, longitud, bateria, fecha));
            }
            cursor.close();
        }
        return lista;
    }

    // Método para eliminar una ubicación de la base de datos
    public static void eliminarUbicacionDeBaseDatos(int id) {
        Log.d("HistorialActivity", "ID recibido para eliminar: " + id); // Agrega esta línea
        databaseHelper.eliminarUbicacion(id);
    }
}
