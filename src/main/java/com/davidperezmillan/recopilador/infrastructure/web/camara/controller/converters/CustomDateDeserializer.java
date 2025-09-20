package com.davidperezmillan.recopilador.infrastructure.web.camara.controller.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateDeserializer extends JsonDeserializer<String> {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("'Date: 'yyyy-MM-dd' Time: 'HH:mm");

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) {
        try {
            String value = p.getText();
            Date fecha = formatter.parse(value);
            // devolvemos la fecha como un string con el formato dd-mm-yyyy:HH
            return new SimpleDateFormat("dd-MM-yyyy:HH").format(fecha);
        } catch (Exception e) {
            return null;
        }
    }
}