package com.davidperezmillan.recopilador.infrastructure.web.transmission.mappers;


import com.davidperezmillan.recopilador.domain.models.Download;
import com.davidperezmillan.recopilador.infrastructure.web.transmission.dtos.DownloadRequest;
import org.modelmapper.ModelMapper;

import java.util.Arrays;

public class DownloadRequestMapping {

    public static Download map(DownloadRequest source) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(DownloadRequest.class, Download.class);

        return modelMapper.map(source, Download.class);
    }

    public static Download[] map(DownloadRequest[] source) {
        return Arrays.stream(source)
                .map(DownloadRequestMapping::map)
                .toArray(Download[]::new);
    }
}
