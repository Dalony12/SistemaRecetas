package Backend;

public class Prescripcion {
    private Medicamento medicamento;
    private int cantidad;
    private String indicaciones;
    private int duracionDias;

    public Prescripcion(Medicamento medicamento, int cantidad, String indicaciones, int duracionDias) {
        this.medicamento = medicamento;
        this.cantidad = cantidad;
        this.indicaciones = indicaciones;
        this.duracionDias = duracionDias;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(int duracionDias) {
        this.duracionDias = duracionDias;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public String toString() {
        return medicamento.getDescripcion() + " (" + getCantidad() + " unidades, " +
                "duración: " + getDuracionDias() + " días, indicaciones: " + getIndicaciones() + ")";
    }
}

