
const EventosUI = (() => {

    let modal = null;
    let guardando = false;
    let eventoIdActual = null;

    function fechaLocal(fecha = new Date()) {
        const anio = fecha.getFullYear();
        const mes = String(fecha.getMonth() + 1).padStart(2, "0");
        const dia = String(fecha.getDate()).padStart(2, "0");

        return `${anio}-${mes}-${dia}`;
    }

    function horaLocal(fecha) {
        const horas = String(fecha.getHours()).padStart(2, "0");
        const minutos = String(fecha.getMinutes()).padStart(2, "0");

        return `${horas}:${minutos}`;
    }

    function crearModal() {

        if (modal) return;

        modal = document.createElement("div");
        modal.id = "modalEvento";
        modal.className = "modal-evento";

        modal.innerHTML = `
            <div class="modal-evento-contenido">

                <div class="modal-evento-cabecera">
                    <div>
                        <span class="modal-etiqueta">AGENDA PERSONAL</span>
                        <h2 id="tituloModalEvento">Crear evento</h2>
                    </div>

                    <button type="button"
                            class="modal-cerrar"
                            id="cerrarModalEvento">
                        &times;
                    </button>
                </div>

                <form id="formularioEvento">

                    <div class="campo-evento">
                        <label for="eventoTitulo">Título *</label>
                        <input type="text"
                               id="eventoTitulo"
                               maxlength="180"
                               placeholder="Ej. Reunión de trabajo"
                               required>
                    </div>

                    <div class="campo-evento">
                        <label for="eventoDescripcion">Descripción</label>
                        <textarea id="eventoDescripcion"
                                  rows="3"
                                  placeholder="Agrega detalles sobre el evento..."></textarea>
                    </div>

                    <div class="campos-evento-doble">

                        <div class="campo-evento">
                            <label for="eventoTipo">Tipo de evento</label>

                            <select id="eventoTipo">
                                <option value="CITA">Cita</option>
                                <option value="REUNION">Reunión</option>
                                <option value="CUMPLEANOS">Cumpleaños</option>
                                <option value="ANIVERSARIO">Aniversario</option>
                                <option value="TAREA">Tarea</option>
                                <option value="OTRO">Otro</option>
                            </select>
                        </div>

                        <div class="campo-evento">
                            <label for="eventoEstado">Estado</label>

                            <select id="eventoEstado">
                                <option value="PROGRAMADO">Programado</option>
                                <option value="COMPLETADO">Completado</option>
                                <option value="CANCELADO">Cancelado</option>
                            </select>
                        </div>

                    </div>

                    <div class="separador-evento"></div>

                    <label class="evento-todo-dia">
                        <input type="checkbox" id="eventoTodoElDia">
                        <span>Todo el día</span>
                    </label>

                    <div class="campos-evento-doble">

                        <div class="campo-evento">
                            <label for="eventoFechaInicio">Fecha de inicio *</label>
                            <input type="date"
                                   id="eventoFechaInicio"
                                   required>
                        </div>

                        <div class="campo-evento">
                            <label for="eventoHoraInicio">Hora de inicio</label>
                            <input type="time"
                                   id="eventoHoraInicio"
                                   value="09:00"
                                   required>
                        </div>

                    </div>

                    <div class="campos-evento-doble">

                        <div class="campo-evento">
                            <label for="eventoFechaFin">Fecha de finalización *</label>
                            <input type="date"
                                   id="eventoFechaFin"
                                   required>
                        </div>

                        <div class="campo-evento">
                            <label for="eventoHoraFin">Hora de finalización</label>
                            <input type="time"
                                   id="eventoHoraFin"
                                   value="10:00"
                                   required>
                        </div>

                    </div>

                    <div class="campo-evento">
                        <label for="eventoUbicacion">Ubicación</label>
                        <input type="text"
                               id="eventoUbicacion"
                               maxlength="255"
                               placeholder="Agregar ubicación">
                    </div>

                    <p id="errorFormularioEvento"
                       class="error-formulario-evento"></p>

                    <div class="modal-evento-acciones">

                        <button type="button"
                                class="boton-eliminar-evento"
                                id="eliminarEvento"
                                hidden>
                            Eliminar
                        </button>

                        <button type="button"
                                class="boton-cancelar-evento"
                                id="cancelarEvento">
                            Cancelar
                        </button>

                        <button type="submit"
                                class="boton-guardar-evento"
                                id="guardarEvento">
                            Guardar evento
                        </button>

                    </div>

                </form>
            </div>
        `;

        document.body.appendChild(modal);

        document.getElementById("cerrarModalEvento")
            .addEventListener("click", cerrar);

        document.getElementById("cancelarEvento")
            .addEventListener("click", cerrar);

        document.getElementById("formularioEvento")
            .addEventListener("submit", guardar);

        document.getElementById("eventoTodoElDia")
            .addEventListener("change", cambiarTodoElDia);

        document.getElementById("eventoFechaInicio")
            .addEventListener("change", sincronizarFechaFin);

        document.getElementById("eliminarEvento")
            .addEventListener("click", eliminar);

        modal.addEventListener("click", e => {
            if (e.target === modal) cerrar();
        });

        document.addEventListener("keydown", e => {
            if (e.key === "Escape" && modal.classList.contains("visible")) {
                cerrar();
            }
        });
    }

    function cambiarTodoElDia() {

        const todoElDia =
            document.getElementById("eventoTodoElDia").checked;

        const horaInicio =
            document.getElementById("eventoHoraInicio");

        const horaFin =
            document.getElementById("eventoHoraFin");

        horaInicio.disabled = todoElDia;
        horaFin.disabled = todoElDia;

        if (todoElDia) {
            horaInicio.value = "00:00";
            horaFin.value = "23:59";
        } else {
            horaInicio.value = "09:00";
            horaFin.value = "10:00";
        }
    }

    function sincronizarFechaFin() {

        const inicio = document.getElementById("eventoFechaInicio");
        const fin = document.getElementById("eventoFechaFin");

        if (!fin.value || fin.value < inicio.value) {
            fin.value = inicio.value;
        }

        fin.min = inicio.value;
    }

    function limpiarFormulario() {

        document.getElementById("formularioEvento").reset();

        document.getElementById("eventoTitulo").value = "";
        document.getElementById("eventoDescripcion").value = "";
        document.getElementById("eventoTipo").value = "OTRO";
        document.getElementById("eventoEstado").value = "PROGRAMADO";
        document.getElementById("eventoUbicacion").value = "";

        document.getElementById("eventoTodoElDia").checked = false;

        cambiarTodoElDia();

        document.getElementById("errorFormularioEvento").textContent = "";

        document.getElementById("eliminarEvento").hidden = true;

        const boton = document.getElementById("guardarEvento");

        boton.disabled = false;
        boton.textContent = "Guardar evento";
    }

    function abrir(fechaTexto = null) {

        crearModal();

        eventoIdActual = null;
        guardando = false;

        limpiarFormulario();

        const fecha = fechaTexto || fechaLocal();

        const fechaInicio = new Date(`${fecha}T09:00:00`);
        const fechaFin = new Date(fechaInicio.getTime() + 60 * 60 * 1000);

        document.getElementById("eventoFechaInicio").value =
            fechaLocal(fechaInicio);

        document.getElementById("eventoFechaFin").value =
            fechaLocal(fechaFin);

        document.getElementById("eventoHoraInicio").value =
            horaLocal(fechaInicio);

        document.getElementById("eventoHoraFin").value =
            horaLocal(fechaFin);

        document.getElementById("eventoFechaFin").min =
            fechaLocal(fechaInicio);

        document.getElementById("tituloModalEvento").textContent =
            "Crear evento";

        modal.classList.add("visible");

        setTimeout(() => {
            document.getElementById("eventoTitulo").focus();
        }, 100);
    }

    async function editar(id) {

        crearModal();

        if (guardando) return;

        guardando = true;

        const boton = document.getElementById("guardarEvento");

        boton.disabled = true;
        boton.textContent = "Cargando...";

        document.getElementById("errorFormularioEvento").textContent = "";

        modal.classList.add("visible");

        try {

            const evento = await API.get(`/api/eventos/${id}`);

            eventoIdActual = id;

            limpiarFormulario();

            const inicio = new Date(evento.fechaInicio);
            const fin = new Date(evento.fechaFin);

            document.getElementById("eventoTitulo").value =
                evento.titulo || "";

            document.getElementById("eventoDescripcion").value =
                evento.descripcion || "";

            document.getElementById("eventoTipo").value =
                evento.tipo || "OTRO";

            document.getElementById("eventoEstado").value =
                evento.estado || "PROGRAMADO";

            document.getElementById("eventoUbicacion").value =
                evento.ubicacion || "";

            document.getElementById("eventoFechaInicio").value =
                fechaLocal(inicio);

            document.getElementById("eventoFechaFin").value =
                fechaLocal(fin);

            document.getElementById("eventoTodoElDia").checked =
                Boolean(evento.todoElDia);

            cambiarTodoElDia();

            if (!evento.todoElDia) {

                document.getElementById("eventoHoraInicio").value =
                    horaLocal(inicio);

                document.getElementById("eventoHoraFin").value =
                    horaLocal(fin);
            }

            document.getElementById("eventoFechaFin").min =
                fechaLocal(inicio);

            document.getElementById("tituloModalEvento").textContent =
                "Editar evento";

            document.getElementById("eliminarEvento").hidden = false;

            boton.disabled = false;
            boton.textContent = "Guardar cambios";

            setTimeout(() => {
                document.getElementById("eventoTitulo").focus();
            }, 100);

        } catch (error) {

            console.error("Error al cargar el evento:", error);

            document.getElementById("errorFormularioEvento").textContent =
                error.message || "No se pudo cargar el evento.";

            boton.disabled = false;
            boton.textContent = "Reintentar";

            guardando = false;
        }

        guardando = false;
    }

    function cerrar() {

        if (!modal) return;

        modal.classList.remove("visible");

        guardando = false;
        eventoIdActual = null;
    }

    function convertirFecha(fecha, hora, todoElDia, esFinal) {

        const horaFinal = todoElDia
            ? (esFinal ? "23:59:00" : "00:00:00")
            : `${hora}:00`;

        return new Date(`${fecha}T${horaFinal}`).toISOString();
    }

    async function guardar(e) {

        e.preventDefault();

        if (guardando) return;

        const titulo =
            document.getElementById("eventoTitulo").value.trim();

        const descripcion =
            document.getElementById("eventoDescripcion").value.trim();

        const fechaInicio =
            document.getElementById("eventoFechaInicio").value;

        const fechaFin =
            document.getElementById("eventoFechaFin").value;

        const horaInicio =
            document.getElementById("eventoHoraInicio").value;

        const horaFin =
            document.getElementById("eventoHoraFin").value;

        const todoElDia =
            document.getElementById("eventoTodoElDia").checked;

        const error =
            document.getElementById("errorFormularioEvento");

        error.textContent = "";

        if (!titulo) {
            error.textContent = "Escribe el título del evento.";
            return;
        }

        if (!fechaInicio || !fechaFin) {
            error.textContent = "Selecciona las fechas del evento.";
            return;
        }

        if (fechaFin < fechaInicio) {
            error.textContent =
                "La fecha de finalización no puede ser anterior al inicio.";
            return;
        }

        if (!todoElDia && fechaInicio === fechaFin && horaFin <= horaInicio) {
            error.textContent =
                "La hora de finalización debe ser posterior a la de inicio.";
            return;
        }

        const datos = {

            titulo,
            descripcion: descripcion || null,

            fechaInicio: convertirFecha(
                fechaInicio,
                horaInicio,
                todoElDia,
                false
            ),

            fechaFin: convertirFecha(
                fechaFin,
                horaFin,
                todoElDia,
                true
            ),

            ubicacion:
                document.getElementById("eventoUbicacion").value.trim() || null,

            tipo: document.getElementById("eventoTipo").value,

            estado: document.getElementById("eventoEstado").value,

            todoElDia
        };

        const boton = document.getElementById("guardarEvento");

        try {

            guardando = true;

            boton.disabled = true;
            boton.textContent = "Guardando...";

            if (eventoIdActual) {
                await API.put(`/api/eventos/${eventoIdActual}`, datos);
            } else {
                await API.post("/api/eventos", datos);
            }

            cerrar();

            if (typeof Calendario !== "undefined") {
                await Calendario.actualizar();
            }

        } catch (errorRespuesta) {

            console.error("Error al guardar el evento:", errorRespuesta);

            error.textContent =
                errorRespuesta.message ||
                "No se pudo guardar el evento. Inténtalo nuevamente.";

            boton.disabled = false;

            boton.textContent = eventoIdActual
                ? "Guardar cambios"
                : "Guardar evento";

            guardando = false;
        }
    }

    function confirmarEliminacion() {

        return new Promise(resolve => {

            const modalConfirmacion = document.createElement("div");

            modalConfirmacion.id = "confirmacionEliminarEvento";
            modalConfirmacion.className = "confirmacion-evento";

            modalConfirmacion.innerHTML = `
	            <div class="confirmacion-evento-contenido">

	                <div class="confirmacion-evento-icono">
	                    <span>!</span>
	                </div>

	                <h3>¿Eliminar este evento?</h3>

	                <p>
	                    Esta acción no se puede deshacer.
	                    El evento se eliminará permanentemente de tu agenda.
	                </p>

	                <div class="confirmacion-evento-acciones">

	                    <button type="button"
	                            id="cancelarEliminarEvento"
	                            class="confirmacion-boton-cancelar">
	                        Cancelar
	                    </button>

	                    <button type="button"
	                            id="aceptarEliminarEvento"
	                            class="confirmacion-boton-eliminar">
	                        Sí, eliminar
	                    </button>

	                </div>

	            </div>
	        `;

            document.body.appendChild(modalConfirmacion);

            function finalizar(resultado) {

                modalConfirmacion.remove();

                document.removeEventListener("keydown", manejarTecla);

                resolve(resultado);
            }

            function manejarTecla(e) {

                if (e.key === "Escape") {
                    finalizar(false);
                }
            }

            modalConfirmacion.addEventListener("click", e => {

                if (e.target === modalConfirmacion) {
                    finalizar(false);
                }
            });

            modalConfirmacion.querySelector("#cancelarEliminarEvento")
                .addEventListener("click", () => finalizar(false));

            modalConfirmacion.querySelector("#aceptarEliminarEvento")
                .addEventListener("click", () => finalizar(true));

            document.addEventListener("keydown", manejarTecla);

            modalConfirmacion.querySelector("#cancelarEliminarEvento").focus();

        });
    }

    async function eliminar() {

        if (!eventoIdActual || guardando) return;

        const confirmar = await confirmarEliminacion();

        if (!confirmar) return;

        const boton = document.getElementById("eliminarEvento");

        const error = document.getElementById("errorFormularioEvento");

        try {

            guardando = true;

            boton.disabled = true;
            boton.textContent = "Eliminando...";

            await API.delete(`/api/eventos/${eventoIdActual}`);

            cerrar();

            if (typeof Calendario !== "undefined") {
                await Calendario.actualizar();
            }

        } catch (errorRespuesta) {

            console.error("Error al eliminar el evento:", errorRespuesta);

            error.textContent =
                errorRespuesta.message ||
                "No se pudo eliminar el evento.";

            boton.disabled = false;
            boton.textContent = "Eliminar";

            guardando = false;
        }
    }

    return {
        abrir,
        editar,
        cerrar
    };

})();
