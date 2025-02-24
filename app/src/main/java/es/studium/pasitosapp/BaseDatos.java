package es.studium.pasitosapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BaseDatos extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pasitos.db";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla y columnas
    private static final String TABLE_NAME = "ubicaciones";
    private static final String COL_ID = "id";
    private static final String COL_LATITUD = "latitud";
    private static final String COL_LONGITUD = "longitud";
    private static final String COL_BATERIA = "bateria";
    private static final String COL_FECHA = "fecha";

    public BaseDatos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LATITUD + " REAL, " +
                COL_LONGITUD + " REAL, " +
                COL_BATERIA + " INTEGER, " +
                COL_FECHA + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Método para insertar datos
    public long insertarDatos(double latitud, double longitud, int bateria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LATITUD, latitud);
        values.put(COL_LONGITUD, longitud);
        values.put(COL_BATERIA, bateria);

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Método para obtener todos los datos guardados
    public Cursor obtenerDatos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Método para eliminar una ubicación por su ID
    public void eliminarUbicacion(int id) {
        Log.d("BaseDatos", "ID recibido para eliminar: " + id); // Agrega esta línea
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
    }
}
