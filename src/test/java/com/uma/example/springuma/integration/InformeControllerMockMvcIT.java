package com.uma.example.springuma.integration;

import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Imagen;
import com.uma.example.springuma.model.ImagenService;
import com.uma.example.springuma.model.Informe;
import com.uma.example.springuma.model.Medico;
import com.uma.example.springuma.model.MedicoService;
import com.uma.example.springuma.model.Paciente;
import com.uma.example.springuma.model.PacienteService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InformeControllerMockMvcIT extends AbstractIntegration{

	@Autowired
	private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ImagenService imagenService;

    @Test
    @DisplayName("/informe/{id} nos devuelve un Informe si el id es valido y hay un Objeto informe en la base de datos con el id pasado por parametro, en este caso da error ya que el id es no valido")
    void informeController_getInforme() throws Exception{
        this.mockMvc.perform(get("/informe/-1"))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("le pasamos un informe al post /informe se guarda")
    void informeController_saveInforme() throws Exception{
        Medico medico = new Medico("4r9033", "nombre", "ninguna");
        medico.setId(1);
        medicoService.addMedico(medico);

        Paciente paciente = new Paciente("pepe", 1, "no tiene", "23u423u", medico);
        paciente.setId(1);
        pacienteService.addPaciente(paciente);

        Imagen imagen = new Imagen(null, paciente);
        imagen.setId(1);
        imagenService.addImagen(imagen);

        Informe informe = new Informe("prediciendo", "contenido", imagen);
        informe.setId(1);

        this.mockMvc.perform(post("/informe")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(informe)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/informe/"+informe.getId()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(informe.getId()));
    }

    @Test
    @DisplayName("le pasamos un id al get /informe/imagen/{id} y nos devuelve la imagen asociada con el informe con el id pasado por parametro")
    void informeController_getImagenInforme() throws Exception{
        Medico medico = new Medico("4r9033", "nombre", "ninguna");
        medico.setId(1);
        medicoService.addMedico(medico);
        Paciente paciente = new Paciente("pepe", 1, "no tiene", "23u423u", medico);
        paciente.setId(1);
        pacienteService.addPaciente(paciente);
        Imagen imagen = new Imagen(null, paciente);
        imagen.setId(1);
        imagenService.addImagen(imagen);

        Informe informe = new Informe("prediciendo", "contenido", imagen);
        informe.setId(1);

        this.mockMvc.perform(post("/informe")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(informe)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/informe/imagen/"+informe.getId()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$[0].id").value(imagen.getId()));
    }

    @Test
    @DisplayName("le pasamos un id al delete /informe/{id} y elimina el informe con el id pasado por parametro")
    void informeController_deleteInforme() throws Exception{
        Medico medico = new Medico("4r9033", "nombre", "ninguna");
        medico.setId(1);
        medicoService.addMedico(medico);
        Paciente paciente = new Paciente("pepe", 1, "no tiene", "23u423u", medico);
        paciente.setId(1);
        pacienteService.addPaciente(paciente);
        Imagen imagen = new Imagen(null, paciente);
        imagen.setId(1);
        imagenService.addImagen(imagen);

        Informe informe = new Informe("prediciendo", "contenido", imagen);
        informe.setId(1);

        this.mockMvc.perform(post("/informe")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(informe)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/informe/imagen/"+informe.getId()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$[0].id").value(imagen.getId()));

        this.mockMvc.perform(delete("/informe/"+informe.getId()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/informe/"+informe.getId()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$").doesNotExist());
    }
}