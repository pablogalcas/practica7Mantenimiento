package com.uma.example.springuma.integration;

import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Imagen;
import com.uma.example.springuma.model.Medico;
import com.uma.example.springuma.model.MedicoService;
import com.uma.example.springuma.model.Paciente;
import com.uma.example.springuma.model.PacienteService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImagenControllerWebTestClientIT extends AbstractIntegration{

    @LocalServerPort
    private int port;

    WebTestClient client;

    @Autowired
    MedicoService medicoService;

    @Autowired
    PacienteService pacienteService;

    @BeforeEach
    void setup(){
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    private Paciente crearPaciente(int id) throws Exception {
        Medico med = new Medico("2345" + id + "A", "Medico", "Cirujano");
        med.setId(1);
        medicoService.addMedico(med);
        Paciente pac = new Paciente("1234" + id + "A", 22, "01/01/2026", "", med);
        pac.setId(id);
        pacienteService.addPaciente(pac);
        return pac;
    }

    private void postImagen(int id) throws Exception {
        Paciente pac = crearPaciente(id);

        String pacJson = new ObjectMapper().writeValueAsString(pac);

        ClassPathResource imageResource = new ClassPathResource("healthy.png");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image", imageResource)
            .filename("healthy.png")
            .contentType(MediaType.valueOf("image/png"));
        builder.part("paciente", pacJson)
            .contentType(MediaType.valueOf("application/json"));

        client.post()
            .uri("imagen")
            .contentType(MediaType.valueOf("multipart_form/data"))
            .body(BodyInserters.fromMultipartData(builder.build()))
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);
    }



    @Test
    @DisplayName("Subir imagen de un paciente de forma correcta")
    void imagen_subirImagenCorrectamente() throws Exception {
        postImagen(1);
    }

    @Test
    @DisplayName("Realizar una prediccion de una imagen de un paciente")
    void imagen_prediccionCorrecta() throws Exception {
        int id = 1;
        postImagen(id);

        List<Imagen> imagenes = client.get()
            .uri("/imagen/paciente/{id}", id)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Imagen.class)
            .returnResult()
            .getResponseBody();

        client.get()
            .uri("/imagen/predict/{id}", imagenes.get(0).getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(prediccion -> {
                assertTrue(prediccion.contains("'status': 'Cancer'") || prediccion.contains("'status': 'Not cancer'"));
                assertTrue(prediccion.contains("'score':"));
            });
    }

}
