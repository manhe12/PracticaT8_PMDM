package es.studium.pasitosapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<Ubicacion> listaUbicaciones;
    private Context context;

    public HistorialAdapter(List<Ubicacion> listaUbicaciones, Context context) {
        this.listaUbicaciones = listaUbicaciones;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ubicacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ubicacion ubicacion = listaUbicaciones.get(position);
        holder.txtLatLng.setText("Lat: " + ubicacion.getLatitud() + " | Lng: " + ubicacion.getLongitud());
        holder.txtBateria.setText("Batería: " + ubicacion.getBateria() + "%");
        holder.txtFecha.setText("Fecha: " + ubicacion.getFecha());

        // Configurar el clic largo en el ViewHolder
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    mostrarDialogoConfirmacion(adapterPosition);
                    return true; // Indica que el evento ha sido consumido
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaUbicaciones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtLatLng, txtBateria, txtFecha;

        public ViewHolder(View itemView) {
            super(itemView);
            txtLatLng = itemView.findViewById(R.id.txtLatLng);
            txtBateria = itemView.findViewById(R.id.txtBateria);
            txtFecha = itemView.findViewById(R.id.txtFecha);
        }
    }

    private void mostrarDialogoConfirmacion(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Eliminar Ubicación");
        builder.setMessage("¿Estás seguro de que quieres eliminar esta ubicación?");
        builder.setPositiveButton("Eliminar", (dialog, which) -> {
            eliminarUbicacion(position);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void eliminarUbicacion(int position) {
        // Obtener el ID de la ubicación
        int id = listaUbicaciones.get(position).getId();
        Log.d("HistorialAdapter", "ID a eliminar: " + id); // Agrega esta línea

        // 1. Eliminar la ubicación de la base de datos
        HistorialActivity.eliminarUbicacionDeBaseDatos(id);
        // 1. Eliminar la ubicación de la lista
        listaUbicaciones.remove(position);

        // 2. Notificar al RecyclerView que un elemento ha sido eliminado
        notifyItemRemoved(position);

        // 3. Opcional: Notificar al RecyclerView que los elementos han cambiado de posición
        notifyItemRangeChanged(position, listaUbicaciones.size());

        // 4. Opcional: Si necesitas persistir los datos, hazlo aquí
        // Por ejemplo, si usas Room, puedes llamar a una función de tu DAO aquí.
    }
}
