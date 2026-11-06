
const EventosUI = (() => {

    let modal = null;
    let guardando = false;
    let eventoIdActual = null;
    let contactosDisponibles = [];
    let participantesSeleccionados = new Map();
    let reglaRecurrenciaExistente = false;

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

                    <div class="separador-evento"></div>

                    <section class="participantes-evento">
                        <div class="participantes-evento-cabecera">
                            <div>
                                <label for="buscarParticipanteEvento">Participantes</label>
                                <p>Agrega uno o varios contactos a este evento.</p>
                            </div>
                            <span id="contadorParticipantes" class="participantes-contador">0</span>
                        </div>

                        <div class="participantes-buscador">
                            <span aria-hidden="true">⌕</span>
                            <input type="search"
                                   id="buscarParticipanteEvento"
                                   placeholder="Buscar contacto por nombre o apodo..."
                                   autocomplete="off">
                        </div>

                        <div id="resultadosParticipantes" class="participantes-resultados">
                            <p class="participantes-mensaje">Cargando contactos...</p>
                        </div>

                        <div class="participantes-seleccionados-contenedor">
                            <span class="participantes-subtitulo">Contactos seleccionados</span>
                            <div id="participantesSeleccionados" class="participantes-seleccionados">
                                <p class="participantes-mensaje">Todavía no has agregado participantes.</p>
                            </div>
                        </div>
                    </section>

                    <div class="separador-evento"></div>


					<section class="recurrencia-evento">

					    <label class="recurrencia-interruptor" for="eventoSeRepite">
					        <input type="checkbox" id="eventoSeRepite">
					        <span class="recurrencia-interruptor-texto">
					            <strong>Repetir evento</strong>
					            <small>Configura cuándo y cómo se repetirá.</small>
					        </span>
					    </label>

					    <div id="configuracionRecurrencia"
					         class="recurrencia-configuracion"
					         hidden>

					        <div class="campo-evento">
					            <label for="recurrenciaFrecuencia">Repetir</label>
					            <select id="recurrenciaFrecuencia">
					                <option value="DIARIA">Diariamente</option>
					                <option value="SEMANAL">Semanalmente</option>
					                <option value="MENSUAL">Mensualmente</option>
					                <option value="ANUAL">Anualmente</option>
					            </select>
					        </div>

					        <div class="campo-evento">
					            <label for="recurrenciaIntervalo">Repetir cada</label>
					            <div class="recurrencia-intervalo">
					                <input type="number"
					                       id="recurrenciaIntervalo"
					                       min="1"
					                       max="365"
					                       value="1">
					                <span id="recurrenciaUnidad">día(s)</span>
					            </div>
					        </div>

					        <div id="recurrenciaDiasSemana"
					             class="campo-evento recurrencia-bloque"
					             hidden>

					            <label>Repetir los siguientes días</label>

					            <div class="recurrencia-dias">
					                <label><input type="checkbox" value="1"> Lun</label>
					                <label><input type="checkbox" value="2"> Mar</label>
					                <label><input type="checkbox" value="3"> Mié</label>
					                <label><input type="checkbox" value="4"> Jue</label>
					                <label><input type="checkbox" value="5"> Vie</label>
					                <label><input type="checkbox" value="6"> Sáb</label>
					                <label><input type="checkbox" value="7"> Dom</label>
					            </div>
					        </div>

					        <div id="recurrenciaDiaMes"
					             class="campo-evento recurrencia-bloque"
					             hidden>

					            <label for="recurrenciaDiaMesInput">Día del mes</label>

					            <input type="number"
					                   id="recurrenciaDiaMesInput"
					                   min="1"
					                   max="31"
					                   value="1">
					        </div>

					        <div class="campo-evento recurrencia-bloque">

					            <label>Finaliza</label>

					            <div class="recurrencia-finalizacion">

					                <label>
					                    <input type="radio"
					                           name="recurrenciaFin"
					                           value="NUNCA">
					                    Nunca
					                </label>

					                <label>
					                    <input type="radio"
					                           name="recurrenciaFin"
					                           value="FECHA">
					                    En una fecha
					                </label>

					                <label>
					                    <input type="radio"
					                           name="recurrenciaFin"
					                           value="CANTIDAD"
					                           checked>
					                    Después de
					                </label>

					            </div>

					            <div id="recurrenciaFinFechaContenedor" hidden>
					                <input type="date" id="recurrenciaFechaFin">
					            </div>

					            <div id="recurrenciaFinCantidadContenedor"
					                 class="recurrencia-cantidad">

					                <input type="number"
					                       id="recurrenciaNumeroOcurrencias"
					                       min="1"
					                       max="999"
					                       value="10">

					                <span>repeticiones en total</span>
					            </div>

					        </div>

					        <p class="recurrencia-ayuda" id="recurrenciaAyuda"></p>

					    </div>

					</section>


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

        document.getElementById("buscarParticipanteEvento")
            .addEventListener("input", renderizarParticipantes);

        document.getElementById("eventoSeRepite")
            .addEventListener("change", cambiarConfiguracionRecurrencia);

        document.getElementById("recurrenciaFrecuencia")
            .addEventListener("change", actualizarCamposRecurrencia);

        document.getElementById("recurrenciaDiaMesInput")
            .addEventListener("input", e => e.target.dataset.modificado = "1");

        document.querySelectorAll('input[name="recurrenciaFin"]')
            .forEach(radio => radio.addEventListener("change", actualizarFinalizacionRecurrencia));

        document.getElementById("eventoFechaInicio")
            .addEventListener("change", () => {
                establecerValoresRecurrenciaPorFecha();
                actualizarAyudaRecurrencia();
                actualizarFinalizacionRecurrencia();
            });

        document.getElementById("resultadosParticipantes")
            .addEventListener("click", e => {
                const boton = e.target.closest("[data-agregar-participante]");
                if (!boton) return;
                agregarParticipante(Number(boton.dataset.agregarParticipante));
            });

        document.getElementById("participantesSeleccionados")
            .addEventListener("click", e => {
                const boton = e.target.closest("[data-quitar-participante]");
                if (!boton) return;
                quitarParticipante(Number(boton.dataset.quitarParticipante));
            });

        modal.addEventListener("click", e => {
            if (e.target === modal) cerrar();
        });

        document.addEventListener("keydown", e => {
            if (e.key === "Escape" && modal.classList.contains("visible")) {
                cerrar();
            }
        });
    }

    // ==========================================
    // PARTICIPANTES DEL EVENTO
    // ==========================================

    function escaparHTML(valor) {
        return String(valor ?? "").replace(/[&<>\"']/g, caracter => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '\"': "&quot;",
            "'": "&#039;"
        })[caracter]);
    }

    function nombreContacto(contacto) {
        return `${contacto.nombre || ""} ${contacto.apellidos || ""}`.trim()
            || contacto.apodo
            || "Contacto sin nombre";
    }

    async function cargarContactosDisponibles() {
        const resultados = document.getElementById("resultadosParticipantes");
        if (!resultados) return;

        resultados.innerHTML = '<p class="participantes-mensaje">Cargando contactos...</p>';

        try {
            const respuesta = await API.get("/api/contactos");
            contactosDisponibles = Array.isArray(respuesta) ? respuesta : [];
            renderizarParticipantes();
        } catch (error) {
            console.error("Error al cargar contactos para participantes:", error);
            resultados.innerHTML = '<p class="participantes-mensaje participantes-error">No fue posible cargar tus contactos.</p>';
        }
    }

    function renderizarParticipantes() {
        const busqueda = document.getElementById("buscarParticipanteEvento");
        const resultados = document.getElementById("resultadosParticipantes");
        const seleccionados = document.getElementById("participantesSeleccionados");
        const contador = document.getElementById("contadorParticipantes");

        if (!busqueda || !resultados || !seleccionados || !contador) return;

        const termino = busqueda.value.trim().toLowerCase();
        const disponibles = contactosDisponibles.filter(contacto => {
            if (participantesSeleccionados.has(Number(contacto.id))) return false;
            const nombre = nombreContacto(contacto).toLowerCase();
            const apodo = String(contacto.apodo || "").toLowerCase();
            return nombre.includes(termino) || apodo.includes(termino);
        });

        if (disponibles.length === 0) {
            resultados.innerHTML = contactosDisponibles.length === 0
                ? '<p class="participantes-mensaje">Todavía no tienes contactos registrados.</p>'
                : '<p class="participantes-mensaje">No se encontraron contactos disponibles.</p>';
        } else {
            resultados.innerHTML = disponibles.slice(0, 8).map(contacto => `
                <button type="button" class="participante-resultado" data-agregar-participante="${Number(contacto.id)}">
                    <span class="participante-avatar">${escaparHTML(nombreContacto(contacto).charAt(0).toUpperCase())}</span>
                    <span class="participante-resultado-datos">
                        <strong>${escaparHTML(nombreContacto(contacto))}</strong>
                        ${contacto.apodo ? `<small>${escaparHTML(contacto.apodo)}</small>` : ""}
                    </span>
                    <span class="participante-agregar-icono">＋</span>
                </button>
            `).join("");
        }

        if (participantesSeleccionados.size === 0) {
            seleccionados.innerHTML = '<p class="participantes-mensaje">Todavía no has agregado participantes.</p>';
        } else {
            seleccionados.innerHTML = Array.from(participantesSeleccionados.values()).map(contacto => `
                <span class="participante-chip">
                    <span>${escaparHTML(nombreContacto(contacto))}</span>
                    <button type="button" data-quitar-participante="${Number(contacto.id)}" aria-label="Quitar ${escaparHTML(nombreContacto(contacto))}">×</button>
                </span>
            `).join("");
        }

        contador.textContent = participantesSeleccionados.size;
    }

    function agregarParticipante(contactoId) {
        const contacto = contactosDisponibles.find(item => Number(item.id) === contactoId);
        if (!contacto || participantesSeleccionados.has(contactoId)) return;
        participantesSeleccionados.set(contactoId, contacto);
        renderizarParticipantes();
    }

    function quitarParticipante(contactoId) {
        participantesSeleccionados.delete(contactoId);
        renderizarParticipantes();
    }

    async function cargarParticipantesEvento(eventoId) {
        const participantes = await API.get(`/api/eventos/${eventoId}/participantes`);
        participantesSeleccionados.clear();

        (Array.isArray(participantes) ? participantes : []).forEach(participante => {
            const contacto = contactosDisponibles.find(
                item => Number(item.id) === Number(participante.contactoId)
            );
            if (contacto) {
                participantesSeleccionados.set(Number(contacto.id), contacto);
            }
        });

        renderizarParticipantes();
    }

    async function sincronizarParticipantes(eventoId) {
        const actuales = await API.get(`/api/eventos/${eventoId}/participantes`);
        const idsActuales = new Set(
            (Array.isArray(actuales) ? actuales : []).map(item => Number(item.contactoId))
        );
        const idsDeseados = new Set(participantesSeleccionados.keys());

        // Quitar del evento los contactos que el usuario deseleccionó.
        for (const contactoId of idsActuales) {
            if (!idsDeseados.has(contactoId)) {
                await API.delete(`/api/eventos/${eventoId}/participantes/${contactoId}`);
            }
        }

        // Agregar solamente los contactos que todavía no están relacionados.
        for (const contactoId of idsDeseados) {
            if (!idsActuales.has(contactoId)) {
                await API.post(`/api/eventos/${eventoId}/participantes`, { contactoId });
            }
        }
    }

    // ==========================================
    // RECURRENCIA DEL EVENTO
    // ==========================================


    function cambiarConfiguracionRecurrencia() {
        const activa = document.getElementById("eventoSeRepite").checked;

        document.getElementById("configuracionRecurrencia").hidden = !activa;

        if (activa) {
            establecerValoresRecurrenciaPorFecha();
            actualizarCamposRecurrencia();
            actualizarFinalizacionRecurrencia();
        }
    }

    function establecerValoresRecurrenciaPorFecha() {
        const fecha = document.getElementById("eventoFechaInicio").value;

        if (!fecha) return;

        const dia = Number(fecha.split("-")[2]);

        const diaMes = document.getElementById("recurrenciaDiaMesInput");

        if (!diaMes.dataset.modificado) {
            diaMes.value = dia;
        }

        const fechaFin = document.getElementById("recurrenciaFechaFin");
        fechaFin.min = fecha;

        const diaSemana = new Date(`${fecha}T12:00:00`).getDay();
        const valorDia = diaSemana === 0 ? 7 : diaSemana;

        const checks = document.querySelectorAll(
            "#recurrenciaDiasSemana input[type='checkbox']"
        );

        if (![...checks].some(check => check.checked)) {
            checks.forEach(check => {
                check.checked = Number(check.value) === valorDia;
            });
        }
    }

    function actualizarCamposRecurrencia() {
        const frecuencia = document.getElementById("recurrenciaFrecuencia").value;

        const dias = document.getElementById("recurrenciaDiasSemana");
        const diaMes = document.getElementById("recurrenciaDiaMes");
        const unidad = document.getElementById("recurrenciaUnidad");

        dias.hidden = frecuencia !== "SEMANAL";
        diaMes.hidden = frecuencia !== "MENSUAL";

        unidad.textContent = ({
            DIARIA: "día(s)",
            SEMANAL: "semana(s)",
            MENSUAL: "mes(es)",
            ANUAL: "año(s)"
        })[frecuencia];

        actualizarAyudaRecurrencia();
    }

    function actualizarFinalizacionRecurrencia() {
        const opcion = document.querySelector(
            'input[name="recurrenciaFin"]:checked'
        )?.value || "CANTIDAD";

        document.getElementById("recurrenciaFinFechaContenedor").hidden =
            opcion !== "FECHA";

        document.getElementById("recurrenciaFinCantidadContenedor").hidden =
            opcion !== "CANTIDAD";

        const fechaInicio = document.getElementById("eventoFechaInicio").value;
        const fechaFin = document.getElementById("recurrenciaFechaFin");

        fechaFin.min = fechaInicio || "";

        if (opcion === "FECHA" &&
            (!fechaFin.value || fechaFin.value < fechaInicio)) {
            fechaFin.value = fechaInicio;
        }
    }

    function actualizarAyudaRecurrencia() {
        const ayuda = document.getElementById("recurrenciaAyuda");

        if (!ayuda) return;

        const frecuencia = document.getElementById("recurrenciaFrecuencia").value;

        const mensajes = {
            DIARIA: "El evento se repetirá cada cierto número de días.",
            SEMANAL: "Selecciona uno o varios días de la semana.",
            MENSUAL: "El evento se repetirá en el día del mes seleccionado.",
            ANUAL: "El evento se repetirá cada año en la misma fecha."
        };

        ayuda.textContent = mensajes[frecuencia] || "";
    }

    function cargarReglaEnFormulario(regla) {
        reglaRecurrenciaExistente = Boolean(regla);

        const activar = document.getElementById("eventoSeRepite");
        activar.checked = Boolean(regla);

        if (!regla) {
            cambiarConfiguracionRecurrencia();
            return;
        }

        document.getElementById("recurrenciaFrecuencia").value =
            regla.frecuencia || "DIARIA";

        document.getElementById("recurrenciaIntervalo").value =
            regla.intervalo || 1;

        document.getElementById("recurrenciaDiaMesInput").value =
            regla.diaMes || "";

        document.getElementById("recurrenciaDiaMesInput").dataset.modificado =
            regla.diaMes ? "1" : "";

        const diasSeleccionados = new Set(
            (regla.diasSemana || []).map(Number)
        );

        document.querySelectorAll(
            "#recurrenciaDiasSemana input[type='checkbox']"
        ).forEach(check => {
            check.checked = diasSeleccionados.has(Number(check.value));
        });

        let fin = "NUNCA";

        if (regla.fechaFin) {
            fin = "FECHA";
            document.getElementById("recurrenciaFechaFin").value =
                regla.fechaFin;
        } else if (regla.numeroOcurrencias) {
            fin = "CANTIDAD";

            document.getElementById("recurrenciaNumeroOcurrencias").value =
                regla.numeroOcurrencias;
        }

        document.querySelector(
            `input[name="recurrenciaFin"][value="${fin}"]`
        ).checked = true;

        cambiarConfiguracionRecurrencia();
        actualizarCamposRecurrencia();
        actualizarFinalizacionRecurrencia();
    }

    async function cargarReglaRecurrencia(eventoId) {
        try {
            const regla = await API.get(
                `/api/eventos/${eventoId}/recurrencia`
            );

            cargarReglaEnFormulario(regla);

        } catch (error) {
            if (error.status === 404) {
                cargarReglaEnFormulario(null);
                return;
            }

            throw error;
        }
    }

    function obtenerDatosRecurrencia() {
        const frecuencia =
            document.getElementById("recurrenciaFrecuencia").value;

        const intervalo =
            Number(document.getElementById("recurrenciaIntervalo").value);

        const opcionFin =
            document.querySelector(
                'input[name="recurrenciaFin"]:checked'
            )?.value || "CANTIDAD";

        const fechaInicio =
            document.getElementById("eventoFechaInicio").value;

        if (!Number.isInteger(intervalo) || intervalo < 1 || intervalo > 365) {
            throw new Error("El intervalo debe estar entre 1 y 365.");
        }

        const diasSemana = frecuencia === "SEMANAL"
            ? [...document.querySelectorAll(
                "#recurrenciaDiasSemana input:checked"
            )].map(check => Number(check.value))
            : null;

        if (frecuencia === "SEMANAL" && diasSemana.length === 0) {
            throw new Error("Selecciona al menos un día de la semana.");
        }

        let diaMes = null;

        if (frecuencia === "MENSUAL") {
            diaMes = Number(
                document.getElementById("recurrenciaDiaMesInput").value
            );

            if (!Number.isInteger(diaMes) || diaMes < 1 || diaMes > 31) {
                throw new Error("El día del mes debe estar entre 1 y 31.");
            }
        }

        let fechaFin = null;
        let numeroOcurrencias = null;

        if (opcionFin === "FECHA") {
            fechaFin = document.getElementById("recurrenciaFechaFin").value;

            if (!fechaFin || fechaFin < fechaInicio) {
                throw new Error(
                    "La fecha final debe ser igual o posterior al inicio."
                );
            }

        } else if (opcionFin === "CANTIDAD") {
            numeroOcurrencias = Number(
                document.getElementById("recurrenciaNumeroOcurrencias").value
            );

            if (!Number.isInteger(numeroOcurrencias) ||
                numeroOcurrencias < 1 ||
                numeroOcurrencias > 999) {
                throw new Error(
                    "El número de repeticiones debe estar entre 1 y 999."
                );
            }
        }

        return {
            frecuencia,
            intervalo,
            diasSemana,
            diaMes,
            fechaFin,
            numeroOcurrencias
        };
    }

    async function sincronizarRecurrencia(eventoId, datosValidados = null) {
        const activa = document.getElementById("eventoSeRepite").checked;

        const ruta = `/api/eventos/${eventoId}/recurrencia`;

        if (!activa) {
            if (reglaRecurrenciaExistente) {
                await API.delete(ruta);
                reglaRecurrenciaExistente = false;
            }

            return;
        }

        const datos = datosValidados || obtenerDatosRecurrencia();

        if (reglaRecurrenciaExistente) {
            await API.put(ruta, datos);
        } else {
            await API.post(ruta, datos);
        }

        reglaRecurrenciaExistente = true;
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

        participantesSeleccionados.clear();
        reglaRecurrenciaExistente = false;
        document.getElementById("eventoSeRepite").checked = false;
        document.getElementById("recurrenciaFrecuencia").value = "DIARIA";
        document.getElementById("recurrenciaIntervalo").value = "1";
        document.getElementById("recurrenciaDiaMesInput").value = "1";
        document.getElementById("recurrenciaDiaMesInput").dataset.modificado = "";
        document.querySelectorAll("#recurrenciaDiasSemana input[type='checkbox']").forEach(check => check.checked = false);
        document.querySelector('input[name="recurrenciaFin"][value="CANTIDAD"]').checked = true; document.getElementById("recurrenciaFechaFin").value = "";
        document.getElementById("recurrenciaNumeroOcurrencias").value = "10";
        cambiarConfiguracionRecurrencia();
        actualizarFinalizacionRecurrencia();
        const buscadorParticipantes = document.getElementById("buscarParticipanteEvento");
        if (buscadorParticipantes) buscadorParticipantes.value = "";
        renderizarParticipantes();

        const botonEliminar = document.getElementById("eliminarEvento");

        botonEliminar.hidden = true;
        botonEliminar.disabled = false;
        botonEliminar.textContent = "Eliminar";
        const boton = document.getElementById("guardarEvento");

        boton.disabled = false;
        boton.textContent = "Guardar evento";
    }

    function abrir(fechaTexto = null) {

        crearModal();

        eventoIdActual = null;
        guardando = false;

        limpiarFormulario();
        cargarContactosDisponibles();

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
            await cargarContactosDisponibles();
            await cargarParticipantesEvento(id);

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

            await cargarReglaRecurrencia(id);

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

        let datosRecurrencia = null;
        if (document.getElementById("eventoSeRepite").checked) {
            try {
                datosRecurrencia = obtenerDatosRecurrencia();
            } catch (errorValidacion) {
                error.textContent = errorValidacion.message;
                return;
            }
        }

        const boton = document.getElementById("guardarEvento");

        try {

            guardando = true;

            boton.disabled = true;
            boton.textContent = "Guardando...";

            let eventoGuardado;

            if (eventoIdActual) {
                eventoGuardado = await API.put(`/api/eventos/${eventoIdActual}`, datos);
            } else {
                eventoGuardado = await API.post("/api/eventos", datos);
            }

            // Guardamos el ID inmediatamente. Si falla la sincronización de
            // participantes, el siguiente intento actualizará este evento
            // en lugar de crear uno duplicado.
            eventoIdActual = eventoGuardado?.id || eventoIdActual;

            if (!eventoIdActual) {
                throw new Error("El evento se guardó, pero no se recibió su identificador.");
            }

            await sincronizarParticipantes(eventoIdActual);
            await sincronizarRecurrencia(eventoIdActual, datosRecurrencia);

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

            if (eventoIdActual) {
                document.getElementById("tituloModalEvento").textContent = "Editar evento";
                document.getElementById("eliminarEvento").hidden = false;
            }

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
