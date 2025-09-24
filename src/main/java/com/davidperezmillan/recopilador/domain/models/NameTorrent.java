package com.davidperezmillan.recopilador.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NameTorrent {

    private String name;
    private String torrentPath;
    private String proposedPath;

}
