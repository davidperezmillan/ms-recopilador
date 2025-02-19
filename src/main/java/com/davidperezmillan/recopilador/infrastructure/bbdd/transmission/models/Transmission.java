package com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "`transmission`")
public class Transmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String url;
    private String username;
    private String password;

}
