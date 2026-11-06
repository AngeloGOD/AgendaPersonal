
const Calendario = (() => {

    let fechaActual = new Date();
    let vistaActual = "mes";
    let eventos = [];
    let contenedor = null;

    const DIAS = ["Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"];

    const MESES = [
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    ];

    function inicioDelDia(fecha) {
        const copia = new Date(fecha);
        copia.setHours(0, 0, 0, 0);
        return copia;
    }

    function finDelDia(fecha) {
        const copia = new Date(fecha);
        copia.setHours(23, 59, 59, 999);
        return copia;
    }

    function sumarDias(fecha, cantidad) {
        const copia = new Date(fecha);
        copia.setDate(copia.getDate() + cantidad);
        return copia;
    }

    function claveFecha(fecha) {
        const anio = fecha.getFullYear();
        const mes = String(fecha.getMonth() + 1).padStart(2, "0");
        const dia = String(fecha.getDate()).padStart(2, "0");

        return `${anio}-${mes}-${dia}`;
    }

    function escaparHTML(valor) {
        return String(valor ?? "").replace(/[&<>"']/g, caracter => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        })[caracter]);
    }

    function obtenerRango() {

        let inicio;
        let fin;

        if (vistaActual === "mes") {

            const primerDia = new Date(
                fechaActual.getFullYear(),
                fechaActual.getMonth(),
                1
            );

            inicio = sumarDias(primerDia, -primerDia.getDay());

            const ultimoDia = new Date(
                fechaActual.getFullYear(),
                fechaActual.getMonth() + 1,
                0
            );

            fin = sumarDias(ultimoDia, 6 - ultimoDia.getDay());

        } else if (vistaActual === "semana") {

            inicio = sumarDias(
                inicioDelDia(fechaActual),
                -fechaActual.getDay()
            );

            fin = sumarDias(inicio, 6);

        } else {

            inicio = inicioDelDia(fechaActual);
            fin = inicio;
        }

        return {
            inicio: inicioDelDia(inicio),
            fin: finDelDia(fin)
        };
    }

    async function cargarEventos() {

        const rango = obtenerRango();

        const parametros = new URLSearchParams({
            fechaInicio: rango.inicio.toISOString(),
            fechaFin: rango.fin.toISOString()
        });

        try {

            const respuesta = await API.get(
                `/api/eventos?${parametros.toString()}`
            );

            eventos = Array.isArray(respuesta) ? respuesta : [];

            // Cargar los participantes de cada evento para mostrarlos
            // también en la vista del calendario, sin abrir la edición.
            if (eventos.length > 0) {
                try {
                    const contactosRespuesta = await API.get("/api/contactos");
                    const contactos = Array.isArray(contactosRespuesta)
                        ? contactosRespuesta
                        : [];

                    const contactosPorId = new Map(
                        contactos.map(contacto => [Number(contacto.id), contacto])
                    );

                    await Promise.all(eventos.map(async evento => {
                        try {
                            const respuestaParticipantes = await API.get(
                                `/api/eventos/${Number(evento.id)}/participantes`
                            );

                            const participantes = Array.isArray(respuestaParticipantes)
                                ? respuestaParticipantes
                                : [];

                            evento.participantesNombres = participantes.map(participante => {
                                const contacto = contactosPorId.get(Number(participante.contactoId));

                                if (!contacto) return null;

                                const nombre = `${contacto.nombre || ""} ${contacto.apellidos || ""}`.trim();
                                return nombre || contacto.apodo || "Contacto sin nombre";
                            }).filter(Boolean);

                        } catch (errorParticipantes) {
                            console.warn(
                                `No se pudieron cargar los participantes del evento ${evento.id}:`,
                                errorParticipantes
                            );
                            evento.participantesNombres = [];
                        }
                    }));

                } catch (errorContactos) {
                    console.warn("No se pudieron cargar los nombres de los contactos:", errorContactos);
                    eventos.forEach(evento => evento.participantesNombres = []);
                }
            }

        } catch (error) {

            console.error("No se pudieron cargar los eventos:", error);

            eventos = [];

            mostrarError("No fue posible cargar los eventos.");
        }
    }

    function mostrarError(mensaje) {

        const aviso = document.getElementById("errorCalendario");

        if (aviso) {
            aviso.textContent = mensaje;
            aviso.classList.remove("oculto");
        }
    }

    function tituloPeriodo() {

        if (vistaActual === "mes") {
            return `${MESES[fechaActual.getMonth()]} ${fechaActual.getFullYear()}`;
        }

        if (vistaActual === "dia") {
            return fechaActual.toLocaleDateString("es-MX", {
                weekday: "long",
                day: "numeric",
                month: "long",
                year: "numeric"
            });
        }

        const inicio = obtenerRango().inicio;
        const fin = obtenerRango().fin;

        if (inicio.getMonth() === fin.getMonth()) {
            return `${inicio.getDate()} - ${fin.getDate()} de ${MESES[fin.getMonth()]} ${fin.getFullYear()}`;
        }

        return `${inicio.getDate()} ${MESES[inicio.getMonth()]} - ${fin.getDate()} ${MESES[fin.getMonth()]} ${fin.getFullYear()}`;
    }

    function eventosDelDia(fecha) {

        const clave = claveFecha(fecha);

        return eventos.filter(evento => {

            if (!evento.fechaInicio || !evento.fechaFin) {
                return false;
            }

            const fechaInicio = claveFecha(
                new Date(evento.fechaInicio)
            );

            const fechaFin = claveFecha(
                new Date(evento.fechaFin)
            );

            // El evento aparece desde su día de inicio
            // hasta su día de finalización, ambos incluidos.
            return fechaInicio <= clave && fechaFin >= clave;

        }).sort((a, b) => {

            return new Date(a.fechaInicio) - new Date(b.fechaInicio);

        });
    }

    function claseEvento(tipo) {

        const clases = {
            CITA: "evento-cita",
            REUNION: "evento-reunion",
            CUMPLEANOS: "evento-cumpleanos",
            ANIVERSARIO: "evento-aniversario",
            TAREA: "evento-tarea",
            OTRO: "evento-otro"
        };

        return clases[tipo] || "evento-otro";
    }

    function horaEvento(evento) {

        if (evento.todoElDia) return "Todo el día";

        return new Date(evento.fechaInicio).toLocaleTimeString("es-MX", {
            hour: "2-digit",
            minute: "2-digit",
            hour12: true
        });
    }

    function informacionTiempoDia(evento, fecha) {

        if (evento.todoElDia) {
            return "Todo el día";
        }

        const fechaInicio = claveFecha(new Date(evento.fechaInicio));
        const fechaFin = claveFecha(new Date(evento.fechaFin));
        const fechaConsultada = claveFecha(fecha);

        const horaFin = new Date(evento.fechaFin).toLocaleTimeString("es-MX", {
            hour: "2-digit",
            minute: "2-digit",
            hour12: true
        });

        if (fechaInicio === fechaFin) {
            return `${horaEvento(evento)} - ${horaFin}`;
        }

        if (fechaConsultada === fechaInicio) {
            return `Inicia ${horaEvento(evento)}`;
        }

        if (fechaConsultada === fechaFin) {
            return `Finaliza ${horaFin}`;
        }

        return "Todo el día";
    }

    function htmlEvento(evento, fecha) {

        const fechaInicio = claveFecha(new Date(evento.fechaInicio));
        const fechaFin = claveFecha(new Date(evento.fechaFin));
        const fechaActual = claveFecha(fecha);

        let informacionTiempo = "";

        if (evento.todoElDia) {

            informacionTiempo = "Todo el día";

        } else if (fechaActual === fechaInicio && fechaActual === fechaFin) {

            // Evento que comienza y termina el mismo día.
            informacionTiempo = `${horaEvento(evento)} - ${new Date(evento.fechaFin).toLocaleTimeString("es-MX", {
                hour: "2-digit",
                minute: "2-digit",
                hour12: true
            })
                }`;

        } else if (fechaActual === fechaInicio) {

            // Primer día.
            informacionTiempo = `Inicia ${horaEvento(evento)}`;

        } else if (fechaActual === fechaFin) {

            // Último día.
            informacionTiempo = `Finaliza ${new Date(evento.fechaFin).toLocaleTimeString("es-MX", {
                hour: "2-digit",
                minute: "2-digit",
                hour12: true
            })}`;

        } else {

            // Días intermedios.
            informacionTiempo = "Continúa";

        }

        return `
	        <div class="cal-evento ${claseEvento(evento.tipo)}"
	             data-evento-id="${Number(evento.id)}"
	             role="button"
	             tabindex="0"
	             title="Editar: ${escaparHTML(evento.titulo)}">

	            <span>${informacionTiempo}</span>

	            <strong>${escaparHTML(evento.titulo)}</strong>

	        </div>
	    `;
    }


    function seleccionarFecha(fechaTexto) {

        const [anio, mes, dia] = fechaTexto.split("-").map(Number);

        // Actualizamos la fecha seleccionada sin cambiar la vista actual.
        fechaActual = new Date(anio, mes - 1, dia);

        // Abrimos el formulario con la fecha seleccionada.
        EventosUI.abrir(fechaTexto);

    }


    function configurarInteracciones() {

        const contenido = contenedor.querySelector("#calContenido");

        if (!contenido) return;

        // Clic sobre un evento: abrir edición.
        contenido.querySelectorAll("[data-evento-id]").forEach(elemento => {

            const abrirEdicion = e => {

                e.preventDefault();
                e.stopPropagation();

                const id = Number(elemento.dataset.eventoId);

                if (id) {
                    EventosUI.editar(id);
                }
            };

            elemento.addEventListener("click", abrirEdicion);

            elemento.addEventListener("keydown", e => {

                if (e.key === "Enter" || e.key === " ") {
                    abrirEdicion(e);
                }
            });
        });

        // Clic sobre un día vacío: abrir creación.
        contenido.querySelectorAll("[data-fecha]").forEach(elemento => {

            elemento.addEventListener("click", () => {

                const fecha = elemento.dataset.fecha;

                if (fecha) {
                    seleccionarFecha(fecha);
                }
            });

            elemento.addEventListener("keydown", e => {

                if (e.key === "Enter" || e.key === " ") {

                    e.preventDefault();

                    const fecha = elemento.dataset.fecha;

                    if (fecha) {
                        seleccionarFecha(fecha);
                    }
                }
            });
        });
    }

    function renderizarMes() {

        const primerDia = new Date(
            fechaActual.getFullYear(),
            fechaActual.getMonth(),
            1
        );

        const inicio = sumarDias(primerDia, -primerDia.getDay());

        const hoy = claveFecha(new Date());

        let html = `
            <div class="cal-dias-semana">
                ${DIAS.map(dia => `<div>${dia}</div>`).join("")}
            </div>

            <div class="cal-cuadricula">
        `;

        for (let i = 0;i < 42;i++) {

            const fecha = sumarDias(inicio, i);
            const clave = claveFecha(fecha);

            const esOtroMes =
                fecha.getMonth() !== fechaActual.getMonth();

            const esHoy = clave === hoy;

            const listaEventos = eventosDelDia(fecha);

            html += `
                <div
                    class="cal-celda ${esOtroMes ? "otro-mes" : ""} ${esHoy ? "es-hoy" : ""}"
                    data-fecha="${clave}"
                    role="button"
                    tabindex="0"
                    aria-label="Ver día ${fecha.getDate()} de ${MESES[fecha.getMonth()]}"
                >

                    <div class="cal-numero-dia">
                        ${esHoy
                    ? `<span class="cal-circulo-hoy">${fecha.getDate()}</span>`
                    : fecha.getDate()
                }
                    </div>

                    <div class="cal-eventos-dia">

                        ${listaEventos.slice(0, 3).map(evento => htmlEvento(evento, fecha)).join("")}

                        ${listaEventos.length > 3
                    ? `<span class="cal-mas-eventos">+${listaEventos.length - 3} más</span>`
                    : ""
                }

                    </div>

                </div>
            `;
        }

        html += "</div>";

        return html;
    }

    function renderizarSemana() {

        const inicio = obtenerRango().inicio;
        const hoy = claveFecha(new Date());

        let html = `<div class="cal-vista-semana">`;

        for (let i = 0;i < 7;i++) {

            const fecha = sumarDias(inicio, i);
            const clave = claveFecha(fecha);

            html += `
                <div
                    class="cal-columna-semana ${clave === hoy ? "es-hoy" : ""}"
                    data-fecha="${clave}"
                    role="button"
                    tabindex="0"
                    aria-label="Ver día ${fecha.getDate()} de ${MESES[fecha.getMonth()]}"
                >

                    <div class="cal-encabezado-dia">
                        <span>${DIAS[fecha.getDay()]}</span>
                        <strong>${fecha.getDate()}</strong>
                    </div>

                    <div class="cal-lista-eventos">
                        ${eventosDelDia(fecha).map(evento => htmlEvento(evento, fecha)).join("")}
                    </div>

                </div>
            `;
        }

        html += "</div>";

        return html;
    }

    function renderizarDia() {

        const lista = eventosDelDia(fechaActual);

        if (lista.length === 0) {

            return `
                <div class="cal-dia-vacio">
                    <span>No hay eventos para este día.</span>
                </div>
            `;
        }

        return `
            <div class="cal-lista-dia">

                ${lista.map(evento => `

                    <div class="cal-item-dia"
                         data-evento-id="${Number(evento.id)}"
                         role="button"
                         tabindex="0"
                         title="Editar evento">

						 <div class="cal-hora-dia">
						     ${informacionTiempoDia(evento, fechaActual)}
						 </div>

                        <div class="cal-detalle-dia ${claseEvento(evento.tipo)}">

                            <strong>${escaparHTML(evento.titulo)}</strong>

                            <span>${escaparHTML(evento.tipo || "OTRO")}</span>

                            ${evento.descripcion
                ? `<small class="cal-descripcion-dia">${escaparHTML(evento.descripcion)}</small>`
                : ""
            }

                            ${evento.ubicacion
                ? `<small>${escaparHTML(evento.ubicacion)}</small>`
                : ""
            }

                            ${Array.isArray(evento.participantesNombres) && evento.participantesNombres.length > 0
                ? `<div class="cal-participantes-dia">
                                       <span class="cal-participantes-etiqueta">Participantes:</span>
                                       <span>${evento.participantesNombres.map(nombre => escaparHTML(nombre)).join(", ")}</span>
                                   </div>`
                : ""
            }

                        </div>

                    </div>

                `).join("")}

            </div>
        `;
    }

    function actualizarContenido() {

        if (!contenedor) return;

        const contenido = contenedor.querySelector("#calContenido");
        const titulo = contenedor.querySelector("#calTituloPeriodo");

        if (!contenido || !titulo) return;

        titulo.textContent = tituloPeriodo();

        if (vistaActual === "mes") {
            contenido.innerHTML = renderizarMes();
        } else if (vistaActual === "semana") {
            contenido.innerHTML = renderizarSemana();
        } else {
            contenido.innerHTML = renderizarDia();
        }

        contenedor.querySelectorAll("[data-vista-cal]").forEach(boton => {

            boton.classList.toggle(
                "activo",
                boton.dataset.vistaCal === vistaActual
            );
        });

        configurarInteracciones();
    }

    async function actualizar() {

        if (!contenedor) return;

        const contenido = contenedor.querySelector("#calContenido");

        const aviso = contenedor.querySelector("#errorCalendario");

        if (aviso) {
            aviso.textContent = "";
            aviso.classList.add("oculto");
        }

        if (contenido) {
            contenido.innerHTML = `
                <div class="cal-cargando">Cargando eventos...</div>
            `;
        }

        await cargarEventos();

        actualizarContenido();
    }

    function navegar(direccion) {

        if (vistaActual === "mes") {

            fechaActual = new Date(
                fechaActual.getFullYear(),
                fechaActual.getMonth() + direccion,
                1
            );

        } else if (vistaActual === "semana") {

            fechaActual = sumarDias(fechaActual, direccion * 7);

        } else {

            fechaActual = sumarDias(fechaActual, direccion);
        }

        actualizar();
    }

    function cambiarVista(vista) {

        if (!["mes", "semana", "dia"].includes(vista)) return;

        vistaActual = vista;

        actualizar();
    }

    function mostrar(elemento) {

        contenedor = elemento;

        fechaActual = new Date();
        vistaActual = "mes";

        contenedor.innerHTML = `

            <section class="cal-seccion">

                <div class="cal-barra">

                    <div class="cal-navegacion">

                        <button type="button" id="calAnterior" aria-label="Anterior">
                            ‹
                        </button>

                        <button type="button" id="calSiguiente" aria-label="Siguiente">
                            ›
                        </button>

                        <button type="button" id="calHoy">
                            Hoy
                        </button>

                        <h2 id="calTituloPeriodo"></h2>

                    </div>

                    <div class="cal-selector-vista">

                        <button type="button" data-vista-cal="mes" class="activo">
                            Mes
                        </button>

                        <button type="button" data-vista-cal="semana">
                            Semana
                        </button>

                        <button type="button" data-vista-cal="dia">
                            Día
                        </button>

                    </div>

                </div>

                <div id="errorCalendario" class="cal-error oculto"></div>

                <div id="calContenido" class="cal-contenido"></div>

            </section>

        `;

        contenedor.querySelector("#calAnterior").addEventListener(
            "click",
            () => navegar(-1)
        );

        contenedor.querySelector("#calSiguiente").addEventListener(
            "click",
            () => navegar(1)
        );

        contenedor.querySelector("#calHoy").addEventListener("click", () => {

            fechaActual = new Date();

            actualizar();
        });

        contenedor.querySelectorAll("[data-vista-cal]").forEach(boton => {

            boton.addEventListener("click", () => {
                cambiarVista(boton.dataset.vistaCal);
            });
        });

        actualizar();
    }

    return {
        mostrar,
        actualizar
    };

})();
