package com.proyecto.facilgimapp.util;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador Gson para serializar y deserializar objetos {@link LocalDate} usando
 * el formato ISO_LOCAL_DATE (yyyy-MM-dd).
 * <p>
 * Implementa {@link JsonSerializer} para convertir un {@link LocalDate} a {@link JsonElement}
 * y {@link JsonDeserializer} para convertir una representación JSON de fecha a {@link LocalDate}.
 * </p>
 *
 * Autor: Francisco Santana
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    /**
     * Formateador de fechas que utiliza el estándar ISO_LOCAL_DATE ("yyyy-MM-dd").
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Serializa un {@link LocalDate} a un elemento JSON con el formato ISO_LOCAL_DATE.
     *
     * @param src       Instancia de {@link LocalDate} a serializar.
     * @param typeOfSrc Tipo del objeto, siempre {@code LocalDate.class}.
     * @param context   Contexto de serialización proporcionado por Gson.
     * @return {@link JsonElement} que contiene la fecha formateada como cadena.
     */
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(FORMATTER));
    }

    /**
     * Deserializa un elemento JSON que contiene una fecha en formato ISO_LOCAL_DATE
     * a un objeto {@link LocalDate}.
     *
     * @param json    {@link JsonElement} que contiene la cadena de fecha ("yyyy-MM-dd").
     * @param typeOfT Tipo de destino, siempre {@code LocalDate.class}.
     * @param context Contexto de deserialización proporcionado por Gson.
     * @return Instancia de {@link LocalDate} parseada a partir de la cadena JSON.
     * @throws JsonParseException Si la cadena no cumple el formato ISO_LOCAL_DATE.
     */
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDate.parse(json.getAsString(), FORMATTER);
    }
}
