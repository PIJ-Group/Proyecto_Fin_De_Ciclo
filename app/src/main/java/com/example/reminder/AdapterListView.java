package com.example.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AdapterListView  extends ArrayAdapter<DataModal> {

    //Constructor para el adapter
    public AdapterListView(@NonNull Context context, ArrayList<DataModal> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Inflamos el layour para el item de list view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_note,parent,false);
        }

        //Obtenemos los datos de nuestro arraylist dentro de nuestra clase modal.
        DataModal dataModal = getItem(position);

        //Iniciamos los componentes de la lista de items.
        TextView titleItem = listItemView.findViewById(R.id.item_title);
        TextView hourItem = listItemView.findViewById(R.id.item_hour);

        //Obtenemos el texto a incluir desde las variables de la clase modal.
        titleItem.setText(dataModal.getTitle());
        hourItem.setText(dataModal.getNoteHour());

        return listItemView;
    }
}
