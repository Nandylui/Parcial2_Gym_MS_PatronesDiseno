package com.gimnasio.entrenamiento.negocio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Crea el Gson de los controladores. Por defecto Gson no entiende fechas ISO (2026-11-15)
 * al leer java.sql.Date, que es lo que envía el formulario del frontend (input type="date").
 * Aquí se acepta el formato ISO y también el formato anterior ("MMM d, yyyy").
 * La forma de escribir (respuestas JSON) no cambia.
 */
public class FechaGson {

    public static Gson crear() {
        JsonDeserializer<Date> lectorFecha = (json, tipo, contexto) -> {
            String texto = json.getAsString().trim();
            if (texto.isEmpty()) {
                return null;
            }
            try {
                // "2026-11-15" (o "2026-11-15T00:00:00...": se toma solo la fecha)
                return Date.valueOf(texto.length() > 10 ? texto.substring(0, 10) : texto);
            } catch (IllegalArgumentException e) {
                // sigue con el formato anterior
            }
            try {
                return new Date(new SimpleDateFormat("MMM d, yyyy").parse(texto).getTime());
            } catch (ParseException e) {
                throw new JsonParseException("Fecha inválida: " + texto);
            }
        };
        return new GsonBuilder().registerTypeAdapter(Date.class, lectorFecha).create();
    }
}
