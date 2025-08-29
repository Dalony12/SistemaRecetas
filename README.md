# 📌 Proyecto#1: Sistema. Prescripción y Despacho de Recetas
**Curso:** EIF206 - Programación 3 (2025-II)  
**Universidad Nacional - Facultad de Ciencias Exactas y Naturales - Escuela de Informática**

## 📖 Descripción
Aplicación de escritorio desarrollada en **Java** para un hospital estatal que permite a los médicos prescribir recetas digitales y a la farmacia gestionar su despacho de medicamentos.

El sistema sigue una **arquitectura por capas** y utiliza el patrón **Modelo-Vista-Controlador (MVC)** en la interfaz gráfica.  

<img width="402" height="454" alt="image" src="https://github.com/user-attachments/assets/1cdb8810-3650-46a2-bef1-3b131a6ad329" />

La persistencia de datos se realiza mediante **archivos XML**.

---

## 🎯 Objetivos
- Facilitar la **prescripción digital** de medicamentos por parte de los médicos.
- Optimizar el **proceso de despacho** de recetas en la farmacia.
- Mantener un **registro centralizado** y seguro de pacientes, médicos, farmaceutas y medicamentos.
- Proporcionar herramientas de **búsqueda, consulta y estadísticas** para los distintos usuarios del sistema.

---

## 🛠️ Tecnologías utilizadas
- **Lenguaje:** Java  
- **GUI:** JavaFX
- **Patrón de diseño:** MVC  
- **Almacenamiento:** Archivos XML  
- **Arquitectura:** Multicapa (Presentación, Lógica de negocio, Persistencia)  

---

## 👥 Tipos de usuario
- **Administrador**  
  Gestiona médicos, farmaceutas, pacientes y medicamentos.
- **Médico**  
  Prescribe recetas digitales para pacientes.
- **Farmaceuta**  
  Despacha medicamentos y gestiona el flujo de recetas.
  
---

## 📌 Funcionalidades principales
1. **Ingreso (Login) y cambio de clave**  
   Autenticación por ID y contraseña para médicos, farmaceutas y administradores.
   
2. **Prescripción de recetas** *(Médico)*  
   - Selección de paciente y medicamentos con búsqueda aproximada.  
   - Definición de cantidad, indicaciones y duración del tratamiento.  
   - Edición o eliminación de medicamentos antes del registro definitivo.  
   - Control de estados: “Confeccionada”.

3. **Despacho de recetas** *(Farmaceuta)*  
   - Control de estados: “Proceso”, “Lista” y “Entregada”.
   - Verificación de fecha de retiro válida (±3 días de la fecha actual).

4. **Gestión de médicos** *(Administrador)*  
   Alta, baja, modificación, búsqueda y consulta de datos.

5. **Gestión de farmaceutas** *(Administrador)*

6. **Gestión de pacientes** *(Administrador)*

7. **Catálogo de medicamentos** *(Administrador)*

8. **Dashboard (Indicadores)**  
   - Gráfico de línea: cantidad de medicamentos prescritos por mes.  
   - Gráfico de pastel: cantidad de recetas por estado.

9. **Histórico de recetas**  
   Consulta y visualización de recetas sin modificación.

---

## 📊 Estados de las recetas
- **Confeccionada:** Creada por el médico y lista para despacho en la fecha indicada.  
- **Proceso:** Farmaceuta inició la preparación.  
- **Lista:** Medicamentos preparados para entrega.  
- **Entregada:** Paciente recibió los medicamentos.

---
