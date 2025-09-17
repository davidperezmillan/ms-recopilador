package com.davidperezmillan.recopilador.infrastructure.web.camara.controller.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateDeserializer extends JsonDeserializer<Date> {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("'Date: 'yyyy-MM-dd' Time: 'HH:mm");

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) {
        try {
            String value = p.getText();
            return formatter.parse(value);
        } catch (Exception e) {
            return null;
        }
    }
}