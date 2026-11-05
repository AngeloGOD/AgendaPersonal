
const RecordatoriosUI = (() => {

    let contenedor = null;
    let eventos = [];
    let recordatorios = [];
    let modal = null;

    const escaparHTML = valor =>
        String(valor ?? "").replace(/[&<>"']/g, caracter => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        })[caracter]);

    function formatoFecha(fecha) {
        if (!fecha) return "Sin fecha";

        return new Date(fecha).toLocaleString("es-MX", {
            day: "2-digit",
            month: "long",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });
    }

    function textoAnticipacion(minutos) {
        if (minutos === 0) return "Al momento del evento";

        if (minutos < 60) {
            return `${minutos} minutos antes`;
        }

        if (minutos < 1440) {
            const horas = minutos / 60;
            return `${horas} ${horas === 1 ? "hora" : "horas"} antes`;
        }

        const dias = minutos / 1440;
        return `${dias} ${dias === 1 ? "día" : "días"} antes`;
    }

    async function mostrar(contenedorRecibido) {
        contenedor = contenedorRecibido;

        contenedor.innerHTML = `
            <section class="recordatorios-pantalla">

                <div class="recordatorios-cabecera">
                    <div>
                        <h2>Recordatorios</h2>
                        <p>Organiza tus avisos y no olvides tus eventos importantes.</p>
                    </div>

                    <button class="recordatorios-boton-crear"
                            id="btnNuevoRecordatorio">
                        + Nuevo recordatorio
                    </button>
                </div>

                <div class="recordatorios-resumen" id="recordatoriosResumen">
                    <div class="recordatorio-resumen-tarjeta">
                        <span class="recordatorio-resumen-numero">...</span>
                        <span>Recordatorios activos</span>
                    </div>
                </div>

                <div class="recordatorios-herramientas">
                    <input
                        type="text"
                        id="buscarRecordatorio"
                        placeholder="Buscar por evento..."
                    >
                </div>

                <div id="recordatoriosContenido">
                    <div class="recordatorios-cargando">
                        Cargando recordatorios...
                    </div>
                </div>

            </section>
        `;

        document.getElementById("btnNuevoRecordatorio")
            .addEventListener("click", () => abrirModal());

        document.getElementById("buscarRecordatorio")
            .addEventListener("input", filtrar);

        await cargarDatos();
    }

    async function cargarDatos() {
        const contenido = document.getElementById("recordatoriosContenido");

        try {
            const ahora = new Date();

            const inicio = new Date(ahora);
            inicio.setFullYear(inicio.getFullYear() - 1);

            const fin = new Date(ahora);
            fin.setFullYear(fin.getFullYear() + 5);

            const parametros = new URLSearchParams({
                fechaInicio: inicio.toISOString(),
                fechaFin: fin.toISOString()
            });

            eventos = await API.get(
                `/api/eventos?${parametros.toString()}`
            );

            eventos = Array.isArray(eventos) ? eventos : [];

            const resultados = await Promise.all(
                eventos.map(async evento => {
                    try {
                        const lista = await API.get(
                            `/api/eventos/${evento.id}/recordatorios`
                        );

                        return (Array.isArray(lista) ? lista : [])
                            .map(recordatorio => ({
                                ...recordatorio,
                                evento
                            }));

                    } catch (error) {
                        console.error(
                            "Error al consultar recordatorios del evento:",
                            evento.id,
                            error
                        );

                        return [];
                    }
                })
            );

            recordatorios = resultados.flat();

            actualizarResumen();
            renderizar(recordatorios);

        } catch (error) {
            console.error("Error al cargar recordatorios:", error);

            contenido.innerHTML = `
                <div class="recordatorios-vacio">
                    <h3>No se pudieron cargar los recordatorios</h3>
                    <p>Comprueba tu conexión e inténtalo nuevamente.</p>
                    <button onclick="RecordatoriosUI.cargarDatos()">
                        Reintentar
                    </button>
                </div>
            `;
        }
    }

    function actualizarResumen() {
        const resumen = document.getElementById("recordatoriosResumen");

        if (!resumen) return;

        resumen.innerHTML = `
            <div class="recordatorio-resumen-tarjeta">
                <span class="recordatorio-resumen-numero">
                    ${recordatorios.length}
                </span>
                <span>Recordatorios activos</span>
            </div>
        `;
    }

    function renderizar(lista) {
        const contenido = document.getElementById("recordatoriosContenido");

        if (!contenido) return;

        if (lista.length === 0) {
            contenido.innerHTML = `
                <div class="recordatorios-vacio">
                    <div class="recordatorios-vacio-icono">◷</div>
                    <h3>No hay recordatorios</h3>
                    <p>
                        Todavía no tienes recordatorios registrados.
                        Puedes crear uno para cualquiera de tus eventos.
                    </p>
                </div>
            `;
            return;
        }

        const ordenados = [...lista].sort((a, b) =>
            new Date(a.evento.fechaInicio) -
            new Date(b.evento.fechaInicio)
        );

        contenido.innerHTML = `
            <div class="recordatorios-lista">

                ${ordenados.map(recordatorio => `
                    <article class="recordatorio-tarjeta">

                        <div class="recordatorio-indicador">
                            <span>◷</span>
                        </div>

                        <div class="recordatorio-informacion">
                            <h3>
                                ${escaparHTML(recordatorio.evento.titulo)}
                            </h3>

                            <p class="recordatorio-fecha">
                                ${formatoFecha(recordatorio.evento.fechaInicio)}
                            </p>

                            <span class="recordatorio-anticipacion">
                                ${textoAnticipacion(recordatorio.minutosAntes)}
                            </span>
                        </div>

                        <div class="recordatorio-acciones">

                            <button
                                class="recordatorio-boton-editar"
                                data-editar="${recordatorio.id}"
                                title="Editar">
                                Editar
                            </button>

                            <button
                                class="recordatorio-boton-eliminar"
                                data-eliminar="${recordatorio.id}"
                                title="Eliminar">
                                Eliminar
                            </button>

                        </div>

                    </article>
                `).join("")}

            </div>
        `;

        contenido.querySelectorAll("[data-editar]").forEach(boton => {
            boton.addEventListener("click", () => {
                const id = Number(boton.dataset.editar);
                editar(id);
            });
        });

        contenido.querySelectorAll("[data-eliminar]").forEach(boton => {
            boton.addEventListener("click", () => {
                const id = Number(boton.dataset.eliminar);
                confirmarEliminacion(id);
            });
        });
    }

    function filtrar() {
        const texto = document.getElementById("buscarRecordatorio")
            .value.trim().toLowerCase();

        const filtrados = recordatorios.filter(recordatorio =>
            recordatorio.evento.titulo.toLowerCase().includes(texto)
        );

        renderizar(filtrados);
    }

    function abrirModal(recordatorio = null) {
        if (eventos.length === 0) {
            mostrarAviso("Primero debes registrar un evento para crear un recordatorio.");
            return;
        }

        const editando = recordatorio !== null;

        modal?.remove();

        modal = document.createElement("div");
        modal.className = "recordatorio-modal-fondo";

        modal.innerHTML = `
            <div class="recordatorio-modal">

                <div class="recordatorio-modal-cabecera">
                    <div>
                        <h2>
                            ${editando ? "Editar recordatorio" : "Nuevo recordatorio"}
                        </h2>
                        <p>Configura cuándo quieres recibir tu aviso.</p>
                    </div>

                    <button type="button"
                            class="recordatorio-modal-cerrar"
                            id="cerrarModalRecordatorio">
                        ×
                    </button>
                </div>

                <form id="formRecordatorio">

                    <div class="recordatorio-campo">
                        <label for="recordatorioEvento">Evento</label>

                        <select id="recordatorioEvento" required
                                ${editando ? "disabled" : ""}>

                            ${eventos.map(evento => `
                                <option value="${evento.id}"
                                    ${editando && evento.id === recordatorio.eventoId
                                        ? "selected" : ""}>
                                    ${escaparHTML(evento.titulo)}
                                    — ${formatoFecha(evento.fechaInicio)}
                                </option>
                            `).join("")}

                        </select>
                    </div>

                    <div class="recordatorio-campo">
                        <label for="recordatorioMinutos">
                            Avisarme con anticipación
                        </label>

                        <select id="recordatorioMinutos" required>
                            <option value="0">En el momento del evento</option>
                            <option value="5">5 minutos antes</option>
                            <option value="10">10 minutos antes</option>
                            <option value="15">15 minutos antes</option>
                            <option value="30">30 minutos antes</option>
                            <option value="60">1 hora antes</option>
                            <option value="120">2 horas antes</option>
                            <option value="360">6 horas antes</option>
                            <option value="720">12 horas antes</option>
                            <option value="1440">1 día antes</option>
                            <option value="2880">2 días antes</option>
                            <option value="10080">1 semana antes</option>
                        </select>
                    </div>

                    <div class="recordatorio-error" id="errorRecordatorio"></div>

                    <div class="recordatorio-modal-acciones">
                        <button type="button"
                                class="recordatorio-cancelar"
                                id="cancelarRecordatorio">
                            Cancelar
                        </button>

                        <button type="submit"
                                class="recordatorio-guardar"
                                id="guardarRecordatorio">
                            ${editando ? "Guardar cambios" : "Crear recordatorio"}
                        </button>
                    </div>

                </form>
            </div>
        `;

        document.body.appendChild(modal);

        const selectorMinutos = modal.querySelector("#recordatorioMinutos");

        if (editando) {
            const valor = String(recordatorio.minutosAntes);

            if (![...selectorMinutos.options].some(opcion => opcion.value === valor)) {
                const opcion = document.createElement("option");
                opcion.value = valor;
                opcion.textContent = textoAnticipacion(recordatorio.minutosAntes);
                selectorMinutos.appendChild(opcion);
            }

            selectorMinutos.value = valor;
        }

        modal.querySelector("#cerrarModalRecordatorio")
            .addEventListener("click", cerrarModal);

        modal.querySelector("#cancelarRecordatorio")
            .addEventListener("click", cerrarModal);

        modal.addEventListener("click", evento => {
            if (evento.target === modal) cerrarModal();
        });

        modal.querySelector("#formRecordatorio")
            .addEventListener("submit", evento => {
                evento.preventDefault();
                guardar(recordatorio);
            });
    }

    async function guardar(recordatorioActual = null) {
        const eventoId = Number(
            document.getElementById("recordatorioEvento").value
        );

        const minutosAntes = Number(
            document.getElementById("recordatorioMinutos").value
        );

        const boton = document.getElementById("guardarRecordatorio");
        const error = document.getElementById("errorRecordatorio");

        boton.disabled = true;
        boton.textContent = "Guardando...";
        error.textContent = "";

        const datos = {
            minutosAntes,
            tipo: "NOTIFICACION",
            activo: true
        };

        try {
            if (recordatorioActual) {
                await API.put(
                    `/api/eventos/${recordatorioActual.eventoId}/recordatorios/${recordatorioActual.id}`,
                    datos
                );
            } else {
                await API.post(
                    `/api/eventos/${eventoId}/recordatorios`,
                    datos
                );
            }

            cerrarModal();
            await cargarDatos();

        } catch (errorPeticion) {
            console.error("Error al guardar recordatorio:", errorPeticion);

            error.textContent =
                errorPeticion.message || "No fue posible guardar el recordatorio.";

            boton.disabled = false;
            boton.textContent = recordatorioActual
                ? "Guardar cambios"
                : "Crear recordatorio";
        }
    }

    function editar(id) {
        const recordatorio = recordatorios.find(item => item.id === id);

        if (recordatorio) abrirModal(recordatorio);
    }

    function confirmarEliminacion(id) {
        const recordatorio = recordatorios.find(item => item.id === id);

        if (!recordatorio) return;

        const confirmacion = document.createElement("div");
        confirmacion.className = "recordatorio-modal-fondo";

        confirmacion.innerHTML = `
            <div class="recordatorio-confirmacion">

                <div class="recordatorio-confirmacion-icono">!</div>

                <h2>¿Eliminar recordatorio?</h2>

                <p>
                    Se eliminará el aviso de
                    <strong>${escaparHTML(recordatorio.evento.titulo)}</strong>.
                    Esta acción no se puede deshacer.
                </p>

                <div class="recordatorio-modal-acciones">
                    <button class="recordatorio-cancelar"
                            id="cancelarEliminarRecordatorio">
                        Cancelar
                    </button>

                    <button class="recordatorio-boton-eliminar"
                            id="aceptarEliminarRecordatorio">
                        Eliminar
                    </button>
                </div>

            </div>
        `;

        document.body.appendChild(confirmacion);

        confirmacion.querySelector("#cancelarEliminarRecordatorio")
            .addEventListener("click", () => confirmacion.remove());

        confirmacion.addEventListener("click", evento => {
            if (evento.target === confirmacion) confirmacion.remove();
        });

        confirmacion.querySelector("#aceptarEliminarRecordatorio")
            .addEventListener("click", async botonEvento => {
                const boton = botonEvento.currentTarget;
                boton.disabled = true;
                boton.textContent = "Eliminando...";

                try {
                    await API.delete(
                        `/api/eventos/${recordatorio.eventoId}/recordatorios/${recordatorio.id}`
                    );

                    confirmacion.remove();
                    await cargarDatos();

                } catch (error) {
                    console.error("Error al eliminar recordatorio:", error);

                    boton.disabled = false;
                    boton.textContent = "Eliminar";

                    const mensaje = document.createElement("p");
                    mensaje.className = "recordatorio-error";
                    mensaje.textContent =
                        error.message || "No fue posible eliminar el recordatorio.";

                    confirmacion.querySelector(".recordatorio-modal-acciones")
                        .before(mensaje);
                }
            });
    }

    function cerrarModal() {
        modal?.remove();
        modal = null;
    }

    function mostrarAviso(mensaje) {
        const aviso = document.createElement("div");
        aviso.className = "recordatorio-modal-fondo";

        aviso.innerHTML = `
            <div class="recordatorio-confirmacion">
                <h2>Atención</h2>
                <p>${escaparHTML(mensaje)}</p>
                <div class="recordatorio-modal-acciones">
                    <button class="recordatorio-guardar" id="cerrarAvisoRecordatorio">
                        Entendido
                    </button>
                </div>
            </div>
        `;

        document.body.appendChild(aviso);

        aviso.querySelector("#cerrarAvisoRecordatorio")
            .addEventListener("click", () => aviso.remove());
    }

    return {
        mostrar,
        cargarDatos
    };

})();
