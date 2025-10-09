package com.davidperezmillan.recopilador.domain.utils;

import com.davidperezmillan.recopilador.domain.models.Camaras;

public class CamarasUtils {


    public static String getUrlCamara(String camaraValue) throws IllegalArgumentException{
        for (Camaras camara : Camaras.values()) {
            if (camara.getNombre().equalsIgnoreCase(camaraValue)) {
                return camara.getUrl();
            }
        }
        Camaras camara = Camaras.valueOf(camaraValue.toUpperCase());
        return camara.getUrl();
    }
}
