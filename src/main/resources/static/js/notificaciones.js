const NotificacionesUI = (() => {

    let intervalo = null;
    let usuarioId = null;
    let comprobando = false;

    const INTERVALO_COMPROBACION = 30000;
    const TIEMPO_TOLERANCIA = 5 * 60 * 1000;

    function claveAlmacenamiento() {
        return `agenda_notificaciones_${usuarioId}`;
    }

    function obtenerNotificacionesEnviadas() {
        try {
            return JSON.parse(
                localStorage.getItem(claveAlmacenamiento()) || "[]"
            );
        } catch {
            return [];
        }
    }

    function guardarNotificacionEnviada(clave) {

        const enviadas = obtenerNotificacionesEnviadas();

        enviadas.push(clave);

        // Conservar únicamente las últimas 300 notificaciones.
        localStorage.setItem(
            claveAlmacenamiento(),
            JSON.stringify(enviadas.slice(-300))
        );
    }

    function mostrarPanel() {

        const pantalla = document.querySelector(".inicio-pantalla");

        if (!pantalla || document.getElementById("panelNotificaciones")) {
            return;
        }

        const panel = document.createElement("div");
        panel.id = "panelNotificaciones";
        panel.className = "inicio-notificaciones";

        if (!("Notification" in window)) {

            panel.innerHTML = `
                <span>Tu navegador no admite notificaciones.</span>
            `;

        } else if (Notification.permission === "default") {

            panel.innerHTML = `
                <div>
                    <strong>Activa las notificaciones</strong>
                    <p>
                        Recibe avisos cuando se acerquen tus eventos.
                    </p>
                </div>

                <button id="btnActivarNotificaciones">
                    Activar notificaciones
                </button>
            `;

        } else if (Notification.permission === "denied") {

            panel.innerHTML = `
                <div>
                    <strong>Notificaciones bloqueadas</strong>
                    <p>
                        Permite las notificaciones de este sitio
                        desde la configuración de tu navegador.
                    </p>
                </div>
            `;

        } else {

            panel.innerHTML = `
                <div>
                    <strong>Notificaciones activadas</strong>
                    <p>
                        AgendaPersonal comprobará tus recordatorios
                        automáticamente.
                    </p>
                </div>
                <span class="inicio-notificaciones-activo">Activadas</span>
            `;
        }

        const resumen = pantalla.querySelector(".inicio-resumen");

        if (resumen) {
            pantalla.insertBefore(panel, resumen);
        } else {
            pantalla.prepend(panel);
        }

        const boton = document.getElementById("btnActivarNotificaciones");

        if (boton) {
            boton.onclick = solicitarPermiso;
        }
    }

    async function solicitarPermiso() {

        if (!("Notification" in window)) {
            alert("Tu navegador no admite notificaciones.");
            return;
        }

        try {

            const permiso = await Notification.requestPermission();

            if (permiso === "granted") {

                mostrarPanel();

                await iniciar();

            } else {

                mostrarPanel();
            }

        } catch (error) {

            console.error("Error al solicitar permiso:", error);
        }
    }

    async function iniciar() {

        if (intervalo || !("Notification" in window)) {
            return;
        }

        if (Notification.permission !== "granted") {
            return;
        }

        try {

            const usuario = await API.get("/api/autenticacion/yo");

            usuarioId = usuario.id;

            // Primera comprobación inmediata.
            await comprobarRecordatorios();

            // Después comprobar cada 30 segundos.
            intervalo = setInterval(
                comprobarRecordatorios,
                INTERVALO_COMPROBACION
            );

        } catch (error) {

            console.error(
                "No se pudieron iniciar las notificaciones:",
                error
            );
        }
    }

    async function comprobarRecordatorios() {

        if (comprobando || !usuarioId) {
            return;
        }

        comprobando = true;

        try {

            const ahora = new Date();

            // Se consultan eventos de los próximos 8 días.
            // Esto cubre el máximo de anticipación de 7 días.
            const fechaFin = new Date(
                ahora.getTime() + 8 * 24 * 60 * 60 * 1000
            );

            const eventos = await API.get(
                `/api/eventos?fechaInicio=${encodeURIComponent(ahora.toISOString())}` +
                `&fechaFin=${encodeURIComponent(fechaFin.toISOString())}`
            );

            if (!Array.isArray(eventos)) {
                return;
            }

            const resultados = await Promise.all(
                eventos.map(async evento => {

                    try {

                        const datos = await API.get(
                            `/api/eventos/${evento.id}/recordatorios`
                        );

                        return (Array.isArray(datos) ? datos : [])
                            .filter(recordatorio => recordatorio.activo)
                            .map(recordatorio => ({
                                evento,
                                recordatorio
                            }));

                    } catch (error) {

                        console.error(
                            `Error consultando recordatorios del evento ${evento.id}:`,
                            error
                        );

                        return [];
                    }
                })
            );

            const todos = resultados.flat();

            for (const elemento of todos) {

                const evento = elemento.evento;
                const recordatorio = elemento.recordatorio;

                const fechaEvento = new Date(evento.fechaInicio);

                // No avisar de eventos que ya comenzaron.
                if (fechaEvento <= ahora) {
                    continue;
                }

                const fechaAviso = new Date(
                    fechaEvento.getTime()
                    - Number(recordatorio.minutosAntes) * 60000
                );

                const diferencia = ahora.getTime() - fechaAviso.getTime();

                // El aviso debe estar dentro de los últimos 5 minutos.
                if (
                    diferencia < 0 ||
                    diferencia > TIEMPO_TOLERANCIA
                ) {
                    continue;
                }

                const clave =
                    `${evento.id}_${recordatorio.id}_${fechaAviso.getTime()}`;

                const enviadas = obtenerNotificacionesEnviadas();

                if (enviadas.includes(clave)) {
                    continue;
                }

                // Registrar antes de mostrar para evitar duplicados.
                guardarNotificacionEnviada(clave);

                const notificacion = new Notification(
                    "AgendaPersonal",
                    {
                        body:
                            `${evento.titulo}\n` +
                            `Tu evento comienza a las ` +
                            `${fechaEvento.toLocaleTimeString("es-MX", {
                                hour: "2-digit",
                                minute: "2-digit"
                            })}.`,
                        tag: clave
                    }
                );

                notificacion.onclick = () => {

                    window.focus();

                    if (typeof Aplicacion !== "undefined") {
                        Aplicacion.cambiarVista("calendario");
                    }

                    notificacion.close();
                };
            }

        } catch (error) {

            console.error(
                "Error al comprobar recordatorios:",
                error
            );

        } finally {

            comprobando = false;
        }
    }

    function detener() {

        if (intervalo) {
            clearInterval(intervalo);
            intervalo = null;
        }

        usuarioId = null;
        comprobando = false;
    }

    function inicializar() {

        mostrarPanel();

        if (
            "Notification" in window &&
            Notification.permission === "granted"
        ) {
            iniciar();
        }
    }

    return {
        inicializar,
        iniciar,
        detener,
        solicitarPermiso
    };

})();