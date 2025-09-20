package com.davidperezmillan.recopilador.infrastructure.web.camara.dtos;




import com.davidperezmillan.recopilador.infrastructure.web.camara.controller.converters.CustomDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.Date;

@Data
public class EventRecord {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private String datetime;
    private String dirname;
}
