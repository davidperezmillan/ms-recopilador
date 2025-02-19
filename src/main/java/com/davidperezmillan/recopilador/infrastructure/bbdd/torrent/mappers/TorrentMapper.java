package com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.mappers;

import com.davidperezmillan.recopilador.domain.models.Download;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.Torrent;
import org.modelmapper.ModelMapper;

import java.util.Arrays;

public class TorrentMapper {

    public static Torrent map(Download source) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Download.class, Torrent.class);

        return modelMapper.map(source, Torrent.class);
    }

    public static Torrent[] map(Download[] source) {
        return Arrays.stream(source)
                .map(TorrentMapper::map)
                .toArray(Torrent[]::new);
    }


    public static Download map(Torrent source) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Torrent.class, Download.class);

        return modelMapper.map(source, Download.class);
    }
}
