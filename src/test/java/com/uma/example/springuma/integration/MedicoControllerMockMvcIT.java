package com.uma.example.springuma.integration;

import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Medico;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MedicoControllerMockMvcIT extends AbstractIntegration{

	@Autowired
	private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("/medico/-1 nos devuelve null ya que no habra ningun medico con ese id")
    void medicoController_getMedico_By_Id_No_Valido() throws Exception{
        this.mockMvc.perform(get("/medico/1"))
        .andDo(print())
        .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("le pasamos un medico al post /medico se guarda")
    void medicoController_saveMedico() throws Exception{

        Medico medico = new Medico("545454M", "medico", "creo que es medico");
        medico.setId(1);

        this.mockMvc.perform(post("/medico")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(medico)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/medico/"+medico.getId()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(medico.getId()));
    }

    @Test
    @DisplayName("le pasamos un medico al put /medico se actualiza el medico")
    void medicoController_updateMedico() throws Exception{

        Medico medico = new Medico("545454M", "medico", "creo que es medico");
        medico.setId(1);

        this.mockMvc.perform(post("/medico")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(medico)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/medico/"+medico.getId()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.dni").value(medico.getDni()))
        .andExpect(jsonPath("$.nombre").value(medico.getNombre()))
        .andExpect(jsonPath("$.especialidad").value(medico.getEspecialidad()));

        medico.setDni("0");
        medico.setEspecialidad("sin trabajo");
        medico.setNombre("mohamed");

        this.mockMvc.perform(put("/medico")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(medico)))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/medico/"+medico.getId()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.dni").value(medico.getDni()))
        .andExpect(jsonPath("$.nombre").value(medico.getNombre()))
        .andExpect(jsonPath("$.especialidad").value(medico.getEspecialidad()));
    }

    @Test
    @DisplayName("le pasamos un medico al delete /medico un id y se elimina el medico")
    void medicoController_deleteMedico() throws Exception{

        Medico medico = new Medico("545454M", "medico", "creo que es medico");
        medico.setId(1);

        this.mockMvc.perform(post("/medico")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(medico)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/medico/"+medico.getId()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(medico.getId()));

        this.mockMvc.perform(delete("/medico/"+medico.getId()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/medico/"+medico.getId()))
        .andDo(print())
        .andExpect(status().is5xxServerError());
    }
    
    @Test
    @DisplayName("le pasamos un medico al get /medico/dni/ un dni y te devuelve si hay un medico con ese dni como objeto ResponseEntity<Medico>")
    void medicoController_getMedicoByDni() throws Exception{

        Medico medico = new Medico("545454M", "medico", "creo que es medico");
        medico.setId(1);

        this.mockMvc.perform(post("/medico")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(medico)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/medico/dni/"+medico.getDni()))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(medico.getId()));
    }
}