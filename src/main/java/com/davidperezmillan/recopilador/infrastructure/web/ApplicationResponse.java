package com.davidperezmillan.recopilador.infrastructure.web;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicationResponse<T> {

    private int count;
    private T content;

}
