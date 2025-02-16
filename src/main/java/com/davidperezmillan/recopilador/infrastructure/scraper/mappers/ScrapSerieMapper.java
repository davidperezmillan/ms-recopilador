package com.davidperezmillan.recopilador.infrastructure.scraper.mappers;

import com.davidperezmillan.recopilador.domain.models.Serie;
import com.davidperezmillan.recopilador.infrastructure.scraper.models.ScrapSerie;
import org.modelmapper.ModelMapper;

import java.util.Arrays;

public class ScrapSerieMapper {

    public static Serie map(ScrapSerie source) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(ScrapSerie.class, Serie.class);

        return modelMapper.map(source, Serie.class);
    }

    public static Serie[] map(ScrapSerie[] source) {
        return Arrays.stream(source)
                .map(ScrapSerieMapper::map)
                .toArray(Serie[]::new);
    }
}
