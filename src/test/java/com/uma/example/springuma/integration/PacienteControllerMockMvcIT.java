package com.uma.example.springuma.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Medico;
import com.uma.example.springuma.model.MedicoService;
import com.uma.example.springuma.model.Paciente;


@SpringBootTest
@AutoConfigureMockMvc
public class PacienteControllerMockMvcIT extends AbstractIntegration{
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MedicoService medicoService;

    private Medico crearMedico(int id) throws Exception{
        Medico med = new Medico("1211211" + id + "A", "Medico", "Cirujano");
        med.setId(id);
        medicoService.addMedico(med);
        return med;
    }

    private Paciente crearPaciente(int id, Medico med) throws Exception{
        Paciente pac = new Paciente("Paciente", 22, "01/01/2026", "1112223" + id + "A", med);
        pac.setId(id);
        
        this.mockMvc.perform(post("/paciente")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(pac)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(status().is2xxSuccessful());

        return pac;
    }

    @Test
    @DisplayName("Crear paciente y hacer get correctamente")
    void paciente_isObtainedWithGet() throws Exception{
        Medico med = crearMedico(1);
        Paciente pac = crearPaciente(1, med);

        this.mockMvc.perform(get("/paciente/"+ pac.getId()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(pac.getId()));
    }

    @Test
    @DisplayName("Crear dos pacientes del mismo medico y hacer get de la lista del medico correctamente")
    void listaPacientes_isObtainedWithGetList() throws Exception{
        Medico med = crearMedico(1);
        Paciente pac1 = crearPaciente(1, med);
        Paciente pac2 = crearPaciente(2, med);

        this.mockMvc.perform(get("/paciente/medico/" + med.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]").value(pac1))
        .andExpect(jsonPath("$[1]").value(pac2));
    }

    @Test
    @DisplayName("Crear un paciente, editarlo y hacer get correctamente")
    void paciente_isObtainedWithGetAndUpdated() throws Exception{
        Medico med = crearMedico(1);
        Paciente pac = crearPaciente(1, med);
        
        this.mockMvc.perform(get("/paciente/" + pac.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.medico.id").value(med.getId()))
        .andExpect(jsonPath("$.medico.dni").value(med.getDni()));
        
        Medico med2 = crearMedico(2);
        pac.setMedico(med2);

        this.mockMvc.perform(put("/paciente")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(pac)))
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/paciente/" + pac.getId()))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.medico.id").value(med2.getId()))
        .andExpect(jsonPath("$.medico.dni").value(med2.getDni()));
    }
}