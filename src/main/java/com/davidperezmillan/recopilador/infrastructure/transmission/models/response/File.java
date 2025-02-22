// File.java
package com.davidperezmillan.recopilador.infrastructure.transmission.models.response;

import lombok.Data;

@Data
public class File {
    private String name;
    private long length;
    private long bytesCompleted;


}