package com.davidperezmillan.recopilador.infrastructure.health.models;

public enum StatusHealthyEnum {

    /*
    HEALTHY (saludable)
    UNHEALTHY (no saludable)
    BOOTING (iniciando)
    DEGRADED (degradado, funciona pero con problemas)
    UNKNOWN (desconocido, no se puede determinar el estado)
     */

    HEALTHY, UNHEALTHY, BOOTING, DEGRADED, UNKNOWN
}
