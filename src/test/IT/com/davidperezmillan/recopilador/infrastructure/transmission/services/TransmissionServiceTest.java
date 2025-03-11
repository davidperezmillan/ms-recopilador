package com.davidperezmillan.recopilador.infrastructure.transmission.services;

import com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request.AddTransmissionRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request.ServerTransmission;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionTorrent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransmissionServiceTest {

    private TransmissionServerService transmissionServerService;

    @BeforeEach
    void setUp() {
        transmissionServerService = new TransmissionServerService();
    }


    void testAddTorrent_RealConnection() {
        AddTransmissionRequest request = new AddTransmissionRequest();
        request.setMangetLink("magnet:?xt=urn:btih:fea90c8809092e073512149c9a6e178438e41b81&dn=Mandy.Bright.ANAL.FUCK.WITH.BUSTY.MATURE.MANDY.BRIGHT.bigass.bigtits.blowjob.hardcore.hugetits.milf.mp4&xl=386471147&tr=udp%3A%2F%2Ftracker1.myporn.club%3A9337%2Fannounce&tr=udp%3A%2F%2Ftracker.opentrackr.org%3A1337%2Fannounce");

        ServerTransmission server = new ServerTransmission();
        server.setUrl("http://192.168.68.195:9069/transmission/rpc");
        server.setUsername("special");
        server.setPassword(""); // añadir la pass para pruebas

        request.setServer(server);

        TransmissionTorrent response = transmissionServerService.addTorrent(request);
        assertNotNull(response);
        System.out.println("Torrent añadido: " + response);
    }


}
