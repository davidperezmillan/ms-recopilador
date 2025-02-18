package com.davidperezmillan.recopilador.infrastructure.web.media.mappers;

import com.davidperezmillan.recopilador.domain.models.Serie;
import com.davidperezmillan.recopilador.infrastructure.web.media.dtos.SerieResponse;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class SerieResponseMapper {

    public static SerieResponse map(Serie serie) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Serie.class, SerieResponse.class);

        return modelMapper.map(serie, SerieResponse.class);
    }

    public static List<SerieResponse> map(List<Serie> series) {
        return series.stream()
                .map(SerieResponseMapper::map)
                .collect(Collectors.toList());
    }
}
