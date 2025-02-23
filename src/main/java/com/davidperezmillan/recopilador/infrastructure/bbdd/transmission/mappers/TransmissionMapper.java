package com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.mappers;

import com.davidperezmillan.recopilador.domain.models.Server;
import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.models.Transmission;
import org.modelmapper.ModelMapper;

public class TransmissionMapper {

    public static Server map(Transmission source) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Transmission.class, Server.class);

        return modelMapper.map(source, Server.class);
    }
}
