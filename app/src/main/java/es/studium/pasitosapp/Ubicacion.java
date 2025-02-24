package es.studium.pasitosapp;

public class Ubicacion {
    private int id;
    private double latitud;
    private double longitud;
    private int bateria;
    private String fecha;

    public Ubicacion(int id, double latitud, double longitud, int bateria, String fecha) {
        this.id = id;
        this.latitud = latitud;
        this.longitud = longitud;
        this.bateria = bateria;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public int getBateria() { return bateria; }
    public String getFecha() { return fecha; }
}
