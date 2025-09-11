package com.example.sistemarecetas.datos.recetas;


import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.datos.LocalDateAdapter;
import com.example.sistemarecetas.datos.pacientes.PacienteEntity;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
public class RecetaEntity {

    @XmlElement(name = "paciente")
    private PacienteEntity paciente;

    @XmlElementWrapper(name = "medicamentos")
    @XmlElement(name = "prescripcion")
    private List<Prescripcion> medicamentos = new ArrayList<>();

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaConfeccion;

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaRetiro;

    private int confeccionado;
    private String estado;

    public RecetaEntity() {}

    public RecetaEntity(PacienteEntity paciente, List<Prescripcion> medicamentos,
                        LocalDate fechaConfeccion, LocalDate fechaRetiro,
                        int confeccionado, String estado) {
        this.paciente = paciente;
        this.medicamentos = medicamentos;
        this.fechaConfeccion = fechaConfeccion;
        this.fechaRetiro = fechaRetiro;
        this.confeccionado = confeccionado;
        this.estado = estado;
    }

    // Getters y setters
    public PacienteEntity getPaciente() { return paciente; }
    public void setPaciente(PacienteEntity paciente) { this.paciente = paciente; }

    public List<Prescripcion> getMedicamentos() { return medicamentos; }
    public void setMedicamentos(List<Prescripcion> medicamentos) { this.medicamentos = medicamentos; }

    public LocalDate getFechaConfeccion() { return fechaConfeccion; }
    public void setFechaConfeccion(LocalDate fechaConfeccion) { this.fechaConfeccion = fechaConfeccion; }

    public LocalDate getFechaRetiro() { return fechaRetiro; }
    public void setFechaRetiro(LocalDate fechaRetiro) { this.fechaRetiro = fechaRetiro; }

    public int getConfeccionado() { return confeccionado; }
    public void setConfeccionado(int confeccionado) { this.confeccionado = confeccionado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

