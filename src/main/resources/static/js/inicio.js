const InicioUI = (() => {

    let eventos = [];
    let recordatorios = [];

    const contenedorActual = () => document.getElementById("contenidoVista");

    // Evitar que los datos del usuario se interpreten como HTML.
    function escapar(texto) {
        return String(texto ?? "").replace(/[&<>"']/g, caracter => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        })[caracter]);
    }

    function inicioDelDia() {
        const fecha = new Date();
        fecha.setHours(0, 0, 0, 0);
        return fecha;
    }

    function formatoFecha(fechaTexto) {
        const fecha = new Date(fechaTexto);

        return fecha.toLocaleDateString("es-MX", {
            weekday: "long",
            day: "numeric",
            month: "long"
        });
    }

    function formatoHora(fechaTexto, todoElDia = false) {
        if (todoElDia) return "Todo el día";

        return new Date(fechaTexto).toLocaleTimeString("es-MX", {
            hour: "2-digit",
            minute: "2-digit"
        });
    }

    function obtenerFechaISO(fecha) {
        return fecha.toISOString();
    }

    function mostrar(contenedor) {

        contenedor.innerHTML = `
            <section class="inicio-pantalla">

                <div class="inicio-bienvenida">
                    <div>
                        <p class="inicio-fecha" id="inicioFecha"></p>
                        <h2 id="inicioSaludo">¡Bienvenido!</h2>
                        <p class="inicio-subtitulo">
                            Aquí tienes un resumen de lo que está por venir.
                        </p>
                    </div>

                    <button class="inicio-boton-crear" id="inicioCrearEvento">
                        + Crear evento
                    </button>
                </div>

                <div class="inicio-resumen">

                    <article class="inicio-resumen-tarjeta">
                        <div class="inicio-resumen-icono">📅</div>
                        <div>
                            <span>Eventos de hoy</span>
                            <strong id="inicioTotalHoy">0</strong>
                        </div>
                    </article>

                    <article class="inicio-resumen-tarjeta">
                        <div class="inicio-resumen-icono">🗓️</div>
                        <div>
                            <span>Próximos eventos</span>
                            <strong id="inicioTotalEventos">0</strong>
                        </div>
                    </article>

                    <article class="inicio-resumen-tarjeta">
                        <div class="inicio-resumen-icono">🔔</div>
                        <div>
                            <span>Recordatorios próximos</span>
                            <strong id="inicioTotalRecordatorios">0</strong>
                        </div>
                    </article>

                </div>

                <div class="inicio-secciones">

                    <section class="inicio-panel">
                        <div class="inicio-panel-cabecera">
                            <div>
                                <h3>Próximos eventos</h3>
                                <p>Tus siguientes compromisos.</p>
                            </div>

                            <button class="inicio-enlace" id="inicioVerCalendario">
                                Ver calendario →
                            </button>
                        </div>

                        <div id="inicioListaEventos">
                            <p class="inicio-cargando">Cargando eventos...</p>
                        </div>
                    </section>

                    <section class="inicio-panel">
                        <div class="inicio-panel-cabecera">
                            <div>
                                <h3>Recordatorios</h3>
                                <p>Lo que está próximo a notificarse.</p>
                            </div>

                            <button class="inicio-enlace" id="inicioVerRecordatorios">
                                Ver todos →
                            </button>
                        </div>

                        <div id="inicioListaRecordatorios">
                            <p class="inicio-cargando">Cargando recordatorios...</p>
                        </div>
                    </section>

                </div>

            </section>
        `;

        document.getElementById("inicioFecha").textContent =
            new Date().toLocaleDateString("es-MX", {
                weekday: "long",
                day: "numeric",
                month: "long",
                year: "numeric"
            });

        document.getElementById("inicioCrearEvento").onclick = () => {
            EventosUI.abrir();
        };

        document.getElementById("inicioVerCalendario").onclick = () => {
            Aplicacion.cambiarVista("calendario");
        };

        document.getElementById("inicioVerRecordatorios").onclick = () => {
            Aplicacion.cambiarVista("recordatorios");
        };

        cargarDatos();
		NotificacionesUI.inicializar();
    }

    async function cargarDatos() {

        const listaEventos = document.getElementById("inicioListaEventos");
        const listaRecordatorios = document.getElementById("inicioListaRecordatorios");

        if (!listaEventos || !listaRecordatorios) return;

        try {

            const ahora = new Date();
            const inicio = inicioDelDia();

            const fin = new Date(inicio);
            fin.setDate(fin.getDate() + 30);

            const [usuario, datosEventos] = await Promise.all([
                API.get("/api/autenticacion/yo"),
                API.get(
                    `/api/eventos?fechaInicio=${encodeURIComponent(obtenerFechaISO(inicio))}` +
                    `&fechaFin=${encodeURIComponent(obtenerFechaISO(fin))}`
                )
            ]);

            eventos = Array.isArray(datosEventos) ? datosEventos : [];

            document.getElementById("inicioSaludo").textContent =
                `¡Hola, ${usuario.nombre}!`;

            // Eventos que comienzan hoy, según la fecha local del navegador.
            const hoy = eventos.filter(evento => {
                const fecha = new Date(evento.fechaInicio);

                return fecha.getFullYear() === ahora.getFullYear()
                    && fecha.getMonth() === ahora.getMonth()
                    && fecha.getDate() === ahora.getDate();
            });

            // Próximos eventos que todavía no han terminado.
            const proximos = eventos
                .filter(evento => new Date(evento.fechaFin) >= ahora)
                .sort((a, b) =>
                    new Date(a.fechaInicio) - new Date(b.fechaInicio)
                );

            // Consultar recordatorios de cada evento.
            const resultadosRecordatorios = await Promise.all(
                eventos.map(async evento => {

                    try {

                        const datos = await API.get(
                            `/api/eventos/${evento.id}/recordatorios`
                        );

                        return (Array.isArray(datos) ? datos : [])
                            .filter(recordatorio => recordatorio.activo)
                            .map(recordatorio => {

                                const fechaAviso = new Date(
                                    new Date(evento.fechaInicio).getTime()
                                    - recordatorio.minutosAntes * 60000
                                );

                                return {
                                    ...recordatorio,
                                    evento: evento,
                                    fechaAviso: fechaAviso
                                };
                            });

                    } catch (error) {
                        console.error(
                            `No se pudieron cargar los recordatorios del evento ${evento.id}`,
                            error
                        );

                        return [];
                    }

                })
            );

            recordatorios = resultadosRecordatorios
                .flat()
                .filter(recordatorio =>
                    recordatorio.fechaAviso >= ahora
                    && new Date(recordatorio.evento.fechaFin) >= ahora
                )
                .sort((a, b) =>
                    a.fechaAviso - b.fechaAviso
                );

            document.getElementById("inicioTotalHoy").textContent = hoy.length;
            document.getElementById("inicioTotalEventos").textContent = proximos.length;
            document.getElementById("inicioTotalRecordatorios").textContent =
                recordatorios.length;

            renderizarEventos(proximos);
            renderizarRecordatorios();

        } catch (error) {

            console.error("Error al cargar el Dashboard:", error);

            listaEventos.innerHTML = `
                <p class="inicio-error">
                    No se pudieron cargar los eventos. Intenta actualizar la página.
                </p>
            `;

            listaRecordatorios.innerHTML = `
                <p class="inicio-error">
                    No se pudieron cargar los recordatorios.
                </p>
            `;
        }
    }

    function renderizarEventos(datos) {

        const contenedor = document.getElementById("inicioListaEventos");

        if (!contenedor) return;

        const proximos = datos.slice(0, 5);

        if (proximos.length === 0) {

            contenedor.innerHTML = `
                <div class="inicio-vacio">
                    <span>📅</span>
                    <p>No tienes próximos eventos.</p>
                    <small>Cuando registres uno, aparecerá aquí.</small>
                </div>
            `;

            return;
        }

        contenedor.innerHTML = proximos.map(evento => `
            <article class="inicio-evento">

                <div class="inicio-evento-fecha">
                    <strong>${new Date(evento.fechaInicio).getDate()}</strong>
                    <span>
                        ${new Date(evento.fechaInicio).toLocaleDateString("es-MX", {
                            month: "short"
                        })}
                    </span>
                </div>

                <div class="inicio-evento-info">
                    <strong>${escapar(evento.titulo)}</strong>
                    <span>
                        ${escapar(formatoFecha(evento.fechaInicio))}
                        ·
                        ${escapar(formatoHora(evento.fechaInicio, evento.todoElDia))}
                    </span>
                </div>

                <span class="inicio-evento-tipo">
                    ${escapar(evento.tipo || "EVENTO")}
                </span>

            </article>
        `).join("");
    }

    function renderizarRecordatorios() {

        const contenedor = document.getElementById("inicioListaRecordatorios");

        if (!contenedor) return;

        const proximos = recordatorios.slice(0, 5);

        if (proximos.length === 0) {

            contenedor.innerHTML = `
                <div class="inicio-vacio">
                    <span>🔔</span>
                    <p>No tienes recordatorios próximos.</p>
                    <small>Los recordatorios activos aparecerán aquí.</small>
                </div>
            `;

            return;
        }

        contenedor.innerHTML = proximos.map(recordatorio => `
            <article class="inicio-recordatorio">

                <div class="inicio-recordatorio-icono">🔔</div>

                <div class="inicio-recordatorio-info">
                    <strong>${escapar(recordatorio.evento.titulo)}</strong>
                    <span>
                        ${escapar(formatoFecha(recordatorio.fechaAviso))}
                        ·
                        ${escapar(formatoHora(recordatorio.fechaAviso))}
                    </span>
                </div>

                <span class="inicio-recordatorio-anticipacion">
                    ${escapar(formatearAnticipacion(recordatorio.minutosAntes))}
                </span>

            </article>
        `).join("");
    }

    function formatearAnticipacion(minutos) {

        if (minutos === 0) return "A la hora del evento";
        if (minutos < 60) return `${minutos} min antes`;

        if (minutos % 1440 === 0) {
            const dias = minutos / 1440;
            return `${dias} día${dias > 1 ? "s" : ""} antes`;
        }

        if (minutos % 60 === 0) {
            const horas = minutos / 60;
            return `${horas} h antes`;
        }

        return `${minutos} min antes`;
    }

    return {
        mostrar,
        cargarDatos
    };

})();