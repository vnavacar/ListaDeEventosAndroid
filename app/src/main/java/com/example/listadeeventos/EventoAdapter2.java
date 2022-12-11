package com.example.listadeeventos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EventoAdapter2 extends ArrayAdapter<Evento> {


    public EventoAdapter2(MainActivity context, int resource, List<Evento> eventos) {
        super(context, resource, eventos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_evento, parent, false);
        }
        Evento evento = getItem(position);
        TextView nombre = view.findViewById(R.id.tvNombre);
        TextView descripcion = view.findViewById(R.id.tvDescripcion);
        TextView precio = view.findViewById(R.id.tvPrecio);

        nombre.setText(evento.getNombre());
        descripcion.setText(evento.getDescripcion());
        precio.setText(evento.getPrecioString());

        return view;
    }

}
