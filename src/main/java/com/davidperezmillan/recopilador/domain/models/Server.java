package com.davidperezmillan.recopilador.domain.models;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class Server {

    private String name;
    private String url;
    private String username;
    private String password;
}
