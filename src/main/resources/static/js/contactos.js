
const ContactosUI = (() => {

    let contactos = [];
    let contenedor = null;
    let modal = null;
    let eliminando = false;

    // ==========================================
    // UTILIDADES
    // ==========================================

    function escapar(valor) {

        return String(valor ?? "").replace(/[&<>"']/g, caracter => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        })[caracter]);

    }

    function inicialMayuscula(nombre) {

        return String(nombre || "?")
            .trim()
            .charAt(0)
            .toUpperCase();

    }

    function nombreCompleto(contacto) {

        return `${contacto.nombre || ""} ${contacto.apellidos || ""}`.trim();

    }

    function mostrarFecha(fecha) {

        if (!fecha) return "—";

        const [anio, mes, dia] = fecha.split("-");

        return `${dia}/${mes}/${anio}`;

    }

    // ==========================================
    // MOSTRAR PANTALLA
    // ==========================================

    async function mostrar(contenedorRecibido) {

        contenedor = contenedorRecibido;

        if (!contenedor) {
            console.error("No se encontró el contenedor de contactos.");
            return;
        }

        contenedor.innerHTML = `
            <section class="contactos-pantalla">

                <div class="contactos-cabecera">

                    <div>
                        <h2>Contactos</h2>
                        <p>
                            Administra y organiza tus contactos personales.
                        </p>
                    </div>

                    <button
                        class="contactos-boton-crear"
                        id="crearContacto"
                        type="button"
                    >
                        <span>＋</span> Nuevo contacto
                    </button>

                </div>

                <div class="contactos-herramientas">

                    <div class="contactos-buscador">

                        <span>⌕</span>

                        <input
                            type="search"
                            id="buscarContacto"
                            placeholder="Buscar por nombre o apodo..."
                        >

                    </div>

                    <span id="totalContactos" class="contactos-total">
                        Cargando contactos...
                    </span>

                </div>

                <div class="contactos-tabla-contenedor">

                    <table class="contactos-tabla">

                        <thead>
                            <tr>
                                <th>Contacto</th>
                                <th>Apodo</th>
                                <th>Fecha de nacimiento</th>
                                <th>Favorito</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>

                        <tbody id="listaContactos">

                            <tr>
                                <td colspan="5" class="contactos-vacio">
                                    Cargando contactos...
                                </td>
                            </tr>

                        </tbody>

                    </table>

                </div>

            </section>
        `;

        configurarEventos();

        await cargarContactos();
    }

    // ==========================================
    // CONFIGURAR EVENTOS
    // ==========================================

    function configurarEventos() {

        document.getElementById("crearContacto").onclick = () => {
            abrirModal();
        };

        document.getElementById("buscarContacto").oninput = (e) => {
            renderizarContactos(e.target.value);
        };

        document.getElementById("listaContactos").onclick = (e) => {

            const boton = e.target.closest("[data-accion]");

            if (!boton) return;

            const id = Number(boton.dataset.id);
            const accion = boton.dataset.accion;

            if (accion === "editar") {
                editar(id);
            }

            if (accion === "eliminar") {
                eliminar(id);
            }

        };

    }

    // ==========================================
    // CARGAR CONTACTOS
    // ==========================================

    async function cargarContactos() {

        const lista = document.getElementById("listaContactos");
        const total = document.getElementById("totalContactos");

        if (!lista || !total) return;

        try {

            const respuesta = await API.get("/api/contactos");

            contactos = Array.isArray(respuesta) ? respuesta : [];

            renderizarContactos();

        } catch (error) {

            console.error("Error al cargar contactos:", error);

            lista.innerHTML = `
                <tr>
                    <td colspan="5" class="contactos-vacio">
                        No fue posible cargar los contactos.
                    </td>
                </tr>
            `;

            total.textContent = "Error al cargar";
        }

    }

    // ==========================================
    // RENDERIZAR TABLA
    // ==========================================

    function renderizarContactos(texto = "") {

        const lista = document.getElementById("listaContactos");
        const total = document.getElementById("totalContactos");

        if (!lista || !total) return;

        const termino = texto.trim().toLowerCase();

        const filtrados = contactos.filter(contacto => {

            const nombre = nombreCompleto(contacto).toLowerCase();
            const apodo = (contacto.apodo || "").toLowerCase();

            return nombre.includes(termino) || apodo.includes(termino);

        });

        total.textContent = `${filtrados.length} ${
            filtrados.length === 1 ? "contacto" : "contactos"
        }`;

        if (filtrados.length === 0) {

            lista.innerHTML = `
                <tr>
                    <td colspan="5" class="contactos-vacio">
                        ${
                            termino
                                ? "No se encontraron contactos."
                                : "Todavía no tienes contactos registrados."
                        }
                    </td>
                </tr>
            `;

            return;
        }

        lista.innerHTML = filtrados.map(contacto => `

            <tr>

                <td>

                    <div class="contacto-identidad">

                        <div class="contacto-avatar">
                            ${escapar(inicialMayuscula(contacto.nombre))}
                        </div>

                        <div class="contacto-nombre">

                            <strong>
                                ${escapar(nombreCompleto(contacto))}
                            </strong>

                            <span>
                                ${escapar(contacto.notas || "Sin notas")}
                            </span>

                        </div>

                    </div>

                </td>

                <td>
                    ${escapar(contacto.apodo || "—")}
                </td>

                <td>
                    ${mostrarFecha(contacto.fechaNacimiento)}
                </td>

                <td>

                    <span class="contacto-favorito ${
                        contacto.favorito ? "es-favorito" : ""
                    }">

                        ${contacto.favorito ? "★ Favorito" : "☆"}

                    </span>

                </td>

                <td>

                    <div class="contacto-acciones">

                        <button
                            type="button"
                            class="contacto-accion-editar"
                            data-accion="editar"
                            data-id="${contacto.id}"
                            title="Editar contacto"
                        >
                            ✎
                        </button>

                        <button
                            type="button"
                            class="contacto-accion-eliminar"
                            data-accion="eliminar"
                            data-id="${contacto.id}"
                            title="Eliminar contacto"
                        >
                            ×
                        </button>

                    </div>

                </td>

            </tr>

        `).join("");

    }

    // ==========================================
    // ABRIR MODAL DE REGISTRO / EDICIÓN
    // ==========================================

    function abrirModal(contacto = null) {

        cerrarModal();

        const editando = contacto !== null;

        modal = document.createElement("div");
        modal.className = "contacto-modal-fondo";

        modal.innerHTML = `

            <div class="contacto-modal">

                <div class="contacto-modal-cabecera">

                    <div>

                        <h3>
                            ${editando ? "Editar contacto" : "Nuevo contacto"}
                        </h3>

                        <p>
                            ${
                                editando
                                    ? "Modifica la información del contacto."
                                    : "Completa los datos de tu nuevo contacto."
                            }
                        </p>

                    </div>

                    <button
                        type="button"
                        class="contacto-cerrar-modal"
                        id="cerrarModalContacto"
                    >
                        ×
                    </button>

                </div>

                <form id="formularioContacto">

                    <div class="contacto-campo">

                        <label for="contactoNombre">Nombre *</label>

                        <input
                            id="contactoNombre"
                            name="nombre"
                            maxlength="100"
                            required
                            value="${escapar(contacto?.nombre)}"
                            placeholder="Nombre del contacto"
                        >

                    </div>

                    <div class="contacto-campos-dobles">

                        <div class="contacto-campo">

                            <label for="contactoApellidos">Apellidos</label>

                            <input
                                id="contactoApellidos"
                                name="apellidos"
                                maxlength="150"
                                value="${escapar(contacto?.apellidos)}"
                                placeholder="Apellidos"
                            >

                        </div>

                        <div class="contacto-campo">

                            <label for="contactoApodo">Apodo</label>

                            <input
                                id="contactoApodo"
                                name="apodo"
                                maxlength="100"
                                value="${escapar(contacto?.apodo)}"
                                placeholder="Cómo lo conoces"
                            >

                        </div>

                    </div>

                    <div class="contacto-campo">

                        <label for="contactoNacimiento">
                            Fecha de nacimiento
                        </label>

                        <input
                            type="date"
                            id="contactoNacimiento"
                            name="fechaNacimiento"
                            value="${escapar(contacto?.fechaNacimiento)}"
                        >

                    </div>

                    <div class="contacto-campo">

                        <label for="contactoNotas">Notas</label>

                        <textarea
                            id="contactoNotas"
                            name="notas"
                            rows="3"
                            placeholder="Información adicional..."
                        >${escapar(contacto?.notas)}</textarea>

                    </div>

                    <label class="contacto-check-favorito">

                        <input
                            type="checkbox"
                            id="contactoFavorito"
                            ${contacto?.favorito ? "checked" : ""}
                        >

                        <span>★</span>
                        Marcar como favorito

                    </label>

                    <p class="contacto-error" id="errorContacto"></p>

                    <div class="contacto-modal-acciones">

                        <button
                            type="button"
                            class="contacto-boton-cancelar"
                            id="cancelarContacto"
                        >
                            Cancelar
                        </button>

                        <button
                            type="submit"
                            class="contacto-boton-guardar"
                            id="guardarContacto"
                        >
                            ${editando ? "Guardar cambios" : "Crear contacto"}
                        </button>

                    </div>

                </form>

            </div>

        `;

        document.body.appendChild(modal);

        document.getElementById("cerrarModalContacto").onclick =
            cerrarModal;

        document.getElementById("cancelarContacto").onclick =
            cerrarModal;

        modal.addEventListener("click", e => {

            if (e.target === modal) {
                cerrarModal();
            }

        });

        document.getElementById("formularioContacto").onsubmit = e => {
            guardar(e, contacto?.id);
        };

        document.getElementById("contactoNombre").focus();
    }

    // ==========================================
    // CERRAR MODAL
    // ==========================================

    function cerrarModal() {

        if (modal) {

            modal.remove();
            modal = null;

        }

    }

    // ==========================================
    // GUARDAR CONTACTO
    // ==========================================

    async function guardar(e, id = null) {

        e.preventDefault();

        const formulario = e.target;

        const boton = document.getElementById("guardarContacto");
        const error = document.getElementById("errorContacto");

        const datos = {

            nombre: formulario.nombre.value.trim(),

            apellidos: formulario.apellidos.value.trim() || null,

            apodo: formulario.apodo.value.trim() || null,

            fechaNacimiento: formulario.fechaNacimiento.value || null,

            notas: formulario.notas.value.trim() || null,

            favorito: document.getElementById("contactoFavorito").checked

        };

        if (!datos.nombre) {

            error.textContent = "El nombre es obligatorio.";

            return;
        }

        try {

            boton.disabled = true;
            boton.textContent = "Guardando...";

            if (id) {

                await API.put(`/api/contactos/${id}`, datos);

            } else {

                await API.post("/api/contactos", datos);

            }

            cerrarModal();

            await cargarContactos();

        } catch (errorRespuesta) {

            console.error("Error al guardar contacto:", errorRespuesta);

            error.textContent =
                errorRespuesta.message || "No fue posible guardar el contacto.";

            boton.disabled = false;

            boton.textContent = id
                ? "Guardar cambios"
                : "Crear contacto";

        }

    }

    // ==========================================
    // EDITAR CONTACTO
    // ==========================================

    function editar(id) {

        const contacto = contactos.find(item => item.id === id);

        if (!contacto) return;

        abrirModal(contacto);
    }

    // ==========================================
    // CONFIRMAR Y ELIMINAR CONTACTO
    // ==========================================

    async function eliminar(id) {

        if (eliminando) return;

        const contacto = contactos.find(item => item.id === id);

        if (!contacto) return;

        const confirmar = document.createElement("div");

        confirmar.className = "contacto-modal-fondo";

        confirmar.innerHTML = `

            <div class="contacto-confirmacion">

                <div class="contacto-confirmacion-icono">
                    !
                </div>

                <h3>¿Eliminar contacto?</h3>

                <p>
                    ¿Estás seguro de que deseas eliminar a
                    <strong>${escapar(nombreCompleto(contacto))}</strong>?
                    Esta acción no se puede deshacer.
                </p>

                <p class="contacto-error" id="errorEliminarContacto"></p>

                <div class="contacto-modal-acciones">

                    <button
                        type="button"
                        class="contacto-boton-cancelar"
                        id="cancelarEliminarContacto"
                    >
                        Cancelar
                    </button>

                    <button
                        type="button"
                        class="contacto-boton-eliminar"
                        id="confirmarEliminarContacto"
                    >
                        Sí, eliminar
                    </button>

                </div>

            </div>

        `;

        document.body.appendChild(confirmar);

        const cerrar = () => {

            confirmar.remove();

        };

        document.getElementById("cancelarEliminarContacto").onclick = cerrar;

        confirmar.addEventListener("click", e => {

            if (e.target === confirmar) {
                cerrar();
            }

        });

        document.getElementById("confirmarEliminarContacto").onclick =
            async () => {

                if (eliminando) return;

                const boton = document.getElementById(
                    "confirmarEliminarContacto"
                );

                const error = document.getElementById(
                    "errorEliminarContacto"
                );

                try {

                    eliminando = true;

                    boton.disabled = true;
                    boton.textContent = "Eliminando...";

                    await API.delete(`/api/contactos/${id}`);

                    cerrar();

                    await cargarContactos();

                } catch (errorRespuesta) {

                    console.error(
                        "Error al eliminar contacto:",
                        errorRespuesta
                    );

                    error.textContent =
                        errorRespuesta.message ||
                        "No se pudo eliminar el contacto.";

                    boton.disabled = false;
                    boton.textContent = "Sí, eliminar";

                } finally {

                    eliminando = false;

                }

            };

    }

    // ==========================================
    // FUNCIONES DISPONIBLES
    // ==========================================

    return {

        mostrar,
        cargarContactos

    };

})();
