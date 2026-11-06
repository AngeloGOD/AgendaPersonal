const ContactosUI = (() => {
    let contactos = [];
    let contenedor = null;
    let modal = null;
    let eliminando = false;

    const TIPOS = {
        telefono: ["MOVIL", "CASA", "TRABAJO", "OTRO"],
        correo: ["PERSONAL", "TRABAJO", "OTRO"],
        direccion: ["CASA", "TRABAJO", "OTRO"],
        organizacion: ["EMPRESA", "INSTITUCION", "OTRA"]
    };

    const escapar = valor => String(valor ?? "").replace(/[&<>"']/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
    })[c]);

    const nombreCompleto = c => `${c?.nombre || ""} ${c?.apellidos || ""}`.trim();
    const inicial = nombre => String(nombre || "?").trim().charAt(0).toUpperCase();
    const fechaBonita = fecha => {
        if (!fecha) return "—";
        const [a, m, d] = String(fecha).split("-");
        return `${d}/${m}/${a}`;
    };
    const textoTipo = tipo => ({
        MOVIL: "Móvil", CASA: "Casa", TRABAJO: "Trabajo", OTRO: "Otro",
        PERSONAL: "Personal", EMPRESA: "Empresa", INSTITUCION: "Institución", OTRA: "Otra"
    }[tipo] || tipo || "Otro");

    async function mostrar(contenedorRecibido) {
        contenedor = contenedorRecibido;
        if (!contenedor) return;

        contenedor.innerHTML = `
            <section class="contactos-pantalla">
                <div class="contactos-cabecera">
                    <div><h2>Contactos</h2><p>Administra y organiza tus contactos personales.</p></div>
                    <button class="contactos-boton-crear" id="crearContacto" type="button"><span>＋</span> Nuevo contacto</button>
                </div>
                <div class="contactos-herramientas">
                    <div class="contactos-buscador"><span>⌕</span><input type="search" id="buscarContacto" placeholder="Buscar por nombre o apodo..."></div>
                    <span id="totalContactos" class="contactos-total">Cargando contactos...</span>
                </div>
                <div class="contactos-tabla-contenedor">
                    <table class="contactos-tabla">
                        <thead><tr><th>Contacto</th><th>Apodo</th><th>Fecha de nacimiento</th><th>Favorito</th><th>Acciones</th></tr></thead>
                        <tbody id="listaContactos"><tr><td colspan="5" class="contactos-vacio">Cargando contactos...</td></tr></tbody>
                    </table>
                </div>
            </section>`;

        document.getElementById("crearContacto").onclick = () => abrirFormularioContacto();
        document.getElementById("buscarContacto").oninput = e => renderizarContactos(e.target.value);
        document.getElementById("listaContactos").onclick = e => {
            const boton = e.target.closest("[data-accion]");
            if (!boton) return;
            const id = Number(boton.dataset.id);
            if (boton.dataset.accion === "detalle") abrirDetalles(id);
            if (boton.dataset.accion === "editar") {
                const contacto = contactos.find(c => c.id === id);
                if (contacto) abrirFormularioContacto(contacto);
            }
            if (boton.dataset.accion === "eliminar") confirmarEliminarContacto(id);
        };
        await cargarContactos();
    }

    async function cargarContactos() {
        const lista = document.getElementById("listaContactos");
        const total = document.getElementById("totalContactos");
        if (!lista || !total) return;
        try {
            const respuesta = await API.get("/api/contactos");
            contactos = Array.isArray(respuesta) ? respuesta : [];
            renderizarContactos(document.getElementById("buscarContacto")?.value || "");
        } catch (error) {
            console.error("Error al cargar contactos:", error);
            lista.innerHTML = `<tr><td colspan="5" class="contactos-vacio">No fue posible cargar los contactos.</td></tr>`;
            total.textContent = "Error al cargar";
        }
    }

    function renderizarContactos(texto = "") {
        const lista = document.getElementById("listaContactos");
        const total = document.getElementById("totalContactos");
        if (!lista || !total) return;
        const termino = texto.trim().toLowerCase();
        const filtrados = contactos.filter(c =>
            nombreCompleto(c).toLowerCase().includes(termino) || String(c.apodo || "").toLowerCase().includes(termino)
        );
        total.textContent = `${filtrados.length} ${filtrados.length === 1 ? "contacto" : "contactos"}`;
        if (!filtrados.length) {
            lista.innerHTML = `<tr><td colspan="5" class="contactos-vacio">${termino ? "No se encontraron contactos." : "Todavía no tienes contactos registrados."}</td></tr>`;
            return;
        }
        lista.innerHTML = filtrados.map(c => `
            <tr>
                <td><button type="button" class="contacto-identidad contacto-enlace-detalle" data-accion="detalle" data-id="${c.id}">
                    <span class="contacto-avatar">${escapar(inicial(c.nombre))}</span>
                    <span class="contacto-nombre"><strong>${escapar(nombreCompleto(c))}</strong><span>${escapar(c.notas || "Sin notas")}</span></span>
                </button></td>
                <td>${escapar(c.apodo || "—")}</td>
                <td>${fechaBonita(c.fechaNacimiento)}</td>
                <td><span class="contacto-favorito ${c.favorito ? "es-favorito" : ""}">${c.favorito ? "★ Favorito" : "☆"}</span></td>
                <td><div class="contacto-acciones">
                    <button type="button" class="contacto-accion-detalle" data-accion="detalle" data-id="${c.id}" title="Ver detalles">⌕</button>
                    <button type="button" class="contacto-accion-editar" data-accion="editar" data-id="${c.id}" title="Editar contacto">✎</button>
                    <button type="button" class="contacto-accion-eliminar" data-accion="eliminar" data-id="${c.id}" title="Eliminar contacto">×</button>
                </div></td>
            </tr>`).join("");
    }

    function crearModal(contenido, clase = "contacto-modal") {
        cerrarModal();
        modal = document.createElement("div");
        modal.className = "contacto-modal-fondo";
        modal.innerHTML = `<div class="${clase}">${contenido}</div>`;
        document.body.appendChild(modal);
        modal.addEventListener("click", e => { if (e.target === modal) cerrarModal(); });
        modal.querySelectorAll("[data-cerrar-modal]").forEach(b => b.addEventListener("click", cerrarModal));
        return modal;
    }

    function cerrarModal() {
        if (modal) modal.remove();
        modal = null;
    }

    function encabezadoModal(titulo, subtitulo) {
        return `<div class="contacto-modal-cabecera"><div><h3>${escapar(titulo)}</h3><p>${escapar(subtitulo)}</p></div><button type="button" class="contacto-cerrar-modal" data-cerrar-modal>×</button></div>`;
    }

    async function abrirFormularioContacto(contacto = null) {
        const editando = !!contacto;
        let telefonos = [], correos = [], direcciones = [];
        let relacionesCategorias = [], categorias = [];
        let relacionesOrganizaciones = [], organizaciones = [];

        try {
            if (editando) {
                [telefonos, correos, direcciones, relacionesCategorias, categorias,
                    relacionesOrganizaciones, organizaciones] = await Promise.all([
                    API.get(`/api/contactos/${contacto.id}/telefonos`),
                    API.get(`/api/contactos/${contacto.id}/correos`),
                    API.get(`/api/contactos/${contacto.id}/direcciones`),
                    API.get(`/api/contactos/${contacto.id}/categorias`),
                    API.get('/api/categorias'),
                    API.get(`/api/contactos/${contacto.id}/organizaciones`),
                    API.get('/api/organizaciones')
                ]);
            } else {
                [categorias, organizaciones] = await Promise.all([
                    API.get('/api/categorias'), API.get('/api/organizaciones')
                ]);
            }
        } catch (error) {
            alert(error.message || 'No fue posible cargar los datos del contacto.');
            return;
        }

        const telefono = (telefonos || []).find(x => x.esPrincipal) || (telefonos || [])[0] || null;
        const correo = (correos || []).find(x => x.esPrincipal) || (correos || [])[0] || null;
        const direccion = (direcciones || []).find(x => x.esPrincipal) || (direcciones || [])[0] || null;
        const categoriasSeleccionadas = new Set((relacionesCategorias || []).map(x => String(x.categoriaId)));
        const organizacionesSeleccionadas = new Set((relacionesOrganizaciones || []).map(x => String(x.organizacionId)));

        const checksCatalogo = (items, seleccionados, nombre) => items.length
            ? `<div class="contacto-seleccion-catalogo">${items.map(item => `
                <label><input type="checkbox" name="${nombre}" value="${item.id}" ${seleccionados.has(String(item.id)) ? 'checked' : ''}>
                <span>${escapar(item.nombre)}</span></label>`).join('')}</div>`
            : `<p class="contacto-ayuda-formulario">Todavía no tienes ${nombre === 'categoriaIds' ? 'categorías' : 'organizaciones'} creadas. Puedes agregarlas después desde los detalles del contacto.</p>`;

        const m = crearModal(`${encabezadoModal(editando ? 'Editar contacto' : 'Nuevo contacto', editando
            ? 'Modifica los datos del contacto desde un solo formulario.'
            : 'Registra los datos personales y la información de contacto.')}
            <form id="formularioContacto" class="contacto-formulario-completo">
                <section class="contacto-form-seccion">
                    <h4>Información personal</h4>
                    <div class="contacto-campo"><label for="contactoNombre">Nombre *</label><input id="contactoNombre" name="nombre" maxlength="100" required value="${escapar(contacto?.nombre)}" placeholder="Nombre del contacto"></div>
                    <div class="contacto-campos-dobles">
                        <div class="contacto-campo"><label for="contactoApellidos">Apellidos</label><input id="contactoApellidos" name="apellidos" maxlength="150" value="${escapar(contacto?.apellidos)}" placeholder="Apellidos"></div>
                        <div class="contacto-campo"><label for="contactoApodo">Apodo</label><input id="contactoApodo" name="apodo" maxlength="100" value="${escapar(contacto?.apodo)}" placeholder="Cómo lo conoces"></div>
                    </div>
                    <div class="contacto-campo"><label for="contactoNacimiento">Fecha de nacimiento</label><input type="date" id="contactoNacimiento" name="fechaNacimiento" value="${escapar(contacto?.fechaNacimiento)}"></div>
                    <div class="contacto-campo"><label for="contactoNotas">Notas</label><textarea id="contactoNotas" name="notas" rows="2" maxlength="5000" placeholder="Información adicional...">${escapar(contacto?.notas)}</textarea></div>
                    <label class="contacto-check-favorito"><input type="checkbox" id="contactoFavorito" ${contacto?.favorito ? 'checked' : ''}><span>★</span> Marcar como favorito</label>
                </section>

                <section class="contacto-form-seccion">
                    <h4>Teléfono</h4>
                    <div class="contacto-campos-dobles">
                        <div class="contacto-campo"><label for="contactoTelefono">Número telefónico</label><input id="contactoTelefono" name="telefono" type="tel" maxlength="30" value="${escapar(telefono?.numero)}" placeholder="Ej. 686 123 4567"></div>
                        <div class="contacto-campo"><label for="contactoCodigoPais">Código de país</label><input id="contactoCodigoPais" name="codigoPais" maxlength="5" value="${escapar(telefono?.codigoPais)}" placeholder="Ej. +52"></div>
                    </div>
                    <div class="contacto-campos-dobles">
                        <div class="contacto-campo"><label for="contactoTipoTelefono">Tipo</label><select id="contactoTipoTelefono" name="tipoTelefono">${TIPOS.telefono.map(v => `<option value="${v}" ${v === (telefono?.tipo || 'MOVIL') ? 'selected' : ''}>${textoTipo(v)}</option>`).join('')}</select></div>
                        <label class="contacto-check-inline"><input type="checkbox" id="contactoTelefonoPrincipal" ${!telefono || telefono.esPrincipal ? 'checked' : ''}> Número principal</label>
                    </div>
                </section>

                <section class="contacto-form-seccion">
                    <h4>Correo electrónico</h4>
                    <div class="contacto-campos-dobles">
                        <div class="contacto-campo"><label for="contactoCorreo">Correo</label><input id="contactoCorreo" name="correo" type="email" maxlength="254" value="${escapar(correo?.correo)}" placeholder="correo@ejemplo.com"></div>
                        <div class="contacto-campo"><label for="contactoTipoCorreo">Tipo</label><select id="contactoTipoCorreo" name="tipoCorreo">${TIPOS.correo.map(v => `<option value="${v}" ${v === (correo?.tipo || 'PERSONAL') ? 'selected' : ''}>${textoTipo(v)}</option>`).join('')}</select></div>
                    </div>
                    <label class="contacto-check-inline"><input type="checkbox" id="contactoCorreoPrincipal" ${!correo || correo.esPrincipal ? 'checked' : ''}> Correo principal</label>
                </section>

                <section class="contacto-form-seccion">
                    <h4>Dirección</h4>
                    <div class="contacto-campo"><label for="contactoCalle">Calle</label><input id="contactoCalle" name="calle" maxlength="150" value="${escapar(direccion?.calle)}" placeholder="Nombre de la calle"></div>
                    <div class="contacto-campos-dobles">
                        <div class="contacto-campo"><label for="contactoNumeroExterior">Número exterior</label><input id="contactoNumeroExterior" name="numeroExterior" maxlength="20" value="${escapar(direccion?.numeroExterior)}"></div>
                        <div class="contacto-campo"><label for="contactoNumeroInterior">Número interior</label><input id="contactoNumeroInterior" name="numeroInterior" maxlength="20" value="${escapar(direccion?.numeroInterior)}"></div>
                    </div>
                    <div class="contacto-campo"><label for="contactoColonia">Colonia</label><input id="contactoColonia" name="colonia" maxlength="100" value="${escapar(direccion?.colonia)}"></div>
                    <div class="contacto-campos-dobles">
                        <div class="contacto-campo"><label for="contactoCiudad">Ciudad</label><input id="contactoCiudad" name="ciudad" maxlength="100" value="${escapar(direccion?.ciudad)}"></div>
                        <div class="contacto-campo"><label for="contactoEstado">Estado</label><input id="contactoEstado" name="estado" maxlength="100" value="${escapar(direccion?.estado)}"></div>
                    </div>
                    <div class="contacto-campos-dobles">
                        <div class="contacto-campo"><label for="contactoCodigoPostal">Código postal</label><input id="contactoCodigoPostal" name="codigoPostal" maxlength="15" value="${escapar(direccion?.codigoPostal)}"></div>
                        <div class="contacto-campo"><label for="contactoPais">País</label><input id="contactoPais" name="pais" maxlength="100" value="${escapar(direccion?.pais)}"></div>
                    </div>
                    <div class="contacto-campos-dobles">
                        <div class="contacto-campo"><label for="contactoTipoDireccion">Tipo</label><select id="contactoTipoDireccion" name="tipoDireccion">${TIPOS.direccion.map(v => `<option value="${v}" ${v === (direccion?.tipo || 'CASA') ? 'selected' : ''}>${textoTipo(v)}</option>`).join('')}</select></div>
                        <label class="contacto-check-inline"><input type="checkbox" id="contactoDireccionPrincipal" ${!direccion || direccion.esPrincipal ? 'checked' : ''}> Dirección principal</label>
                    </div>
                </section>

                <section class="contacto-form-seccion">
                    <h4>Categorías</h4>
                    ${checksCatalogo(categorias || [], categoriasSeleccionadas, 'categoriaIds')}
                </section>
                <section class="contacto-form-seccion">
                    <h4>Organizaciones</h4>
                    ${checksCatalogo(organizaciones || [], organizacionesSeleccionadas, 'organizacionIds')}
                </section>

                <p class="contacto-ayuda-formulario">Este formulario administra el teléfono, correo y dirección principales. Si tienes otros datos adicionales, puedes seguir administrándolos desde los detalles del contacto.</p>
                <p class="contacto-error" id="errorContacto"></p>
                <div class="contacto-modal-acciones"><button type="button" class="contacto-boton-cancelar" data-cerrar-modal>Cancelar</button><button type="submit" class="contacto-boton-guardar" id="guardarContacto">${editando ? 'Guardar cambios' : 'Crear contacto'}</button></div>
            </form>`, 'contacto-modal contacto-modal-amplio');

        m.querySelector('#formularioContacto').onsubmit = async e => {
            e.preventDefault();
            const f = e.currentTarget;
            const boton = m.querySelector('#guardarContacto');
            const error = m.querySelector('#errorContacto');
            const datos = {
                nombre: f.nombre.value.trim(),
                apellidos: f.apellidos.value.trim() || null,
                apodo: f.apodo.value.trim() || null,
                fechaNacimiento: f.fechaNacimiento.value || null,
                notas: f.notas.value.trim() || null,
                favorito: m.querySelector('#contactoFavorito').checked
            };
            if (!datos.nombre) { error.textContent = 'El nombre es obligatorio.'; return; }

            const telefonoDatos = {
                numero: f.telefono.value.trim(),
                codigoPais: f.codigoPais.value.trim() || null,
                tipo: f.tipoTelefono.value,
                esPrincipal: m.querySelector('#contactoTelefonoPrincipal').checked
            };
            const correoDatos = {
                correo: f.correo.value.trim(),
                tipo: f.tipoCorreo.value,
                esPrincipal: m.querySelector('#contactoCorreoPrincipal').checked
            };
            const direccionDatos = {
                calle: f.calle.value.trim() || null,
                numeroExterior: f.numeroExterior.value.trim() || null,
                numeroInterior: f.numeroInterior.value.trim() || null,
                colonia: f.colonia.value.trim() || null,
                ciudad: f.ciudad.value.trim() || null,
                estado: f.estado.value.trim() || null,
                codigoPostal: f.codigoPostal.value.trim() || null,
                pais: f.pais.value.trim() || null,
                tipo: f.tipoDireccion.value,
                esPrincipal: m.querySelector('#contactoDireccionPrincipal').checked
            };
            const tieneDireccion = Object.entries(direccionDatos).some(([k, v]) => !['tipo', 'esPrincipal'].includes(k) && v);
            if (correoDatos.correo && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correoDatos.correo)) {
                error.textContent = 'Escribe un correo electrónico válido.'; return;
            }

            const categoriaIds = [...m.querySelectorAll('input[name="categoriaIds"]:checked')].map(x => Number(x.value));
            const organizacionIds = [...m.querySelectorAll('input[name="organizacionIds"]:checked')].map(x => Number(x.value));
            boton.disabled = true;
            boton.textContent = 'Guardando...';
            error.textContent = '';

            try {
                let contactoGuardado;
                if (editando) contactoGuardado = await API.put(`/api/contactos/${contacto.id}`, datos);
                else contactoGuardado = await API.post('/api/contactos', datos);
                const contactoId = contactoGuardado?.id || contacto?.id;
                if (!contactoId) throw new Error('El contacto se guardó, pero la API no devolvió su identificador.');

                await guardarDatoPrincipal(contactoId, 'telefonos', telefono, telefonoDatos, !!telefonoDatos.numero);
                await guardarDatoPrincipal(contactoId, 'correos', correo, correoDatos, !!correoDatos.correo);
                await guardarDatoPrincipal(contactoId, 'direcciones', direccion, direccionDatos, tieneDireccion);
                await sincronizarRelaciones(contactoId, 'categorias', relacionesCategorias, categoriaIds, 'categoriaId');
                await sincronizarRelaciones(contactoId, 'organizaciones', relacionesOrganizaciones, organizacionIds, 'organizacionId');

                cerrarModal();
                await cargarContactos();
            } catch (err) {
                error.textContent = err.message || 'No fue posible guardar toda la información del contacto.';
                boton.disabled = false;
                boton.textContent = editando ? 'Guardar cambios' : 'Crear contacto';
            }
        };
        m.querySelector('#contactoNombre').focus();
    }

    async function guardarDatoPrincipal(contactoId, ruta, existente, datos, tieneContenido) {
        const endpoint = `/api/contactos/${contactoId}/${ruta}`;
        if (existente) {
            if (tieneContenido) await API.put(`${endpoint}/${existente.id}`, datos);
            else await API.delete(`${endpoint}/${existente.id}`);
        } else if (tieneContenido) {
            await API.post(endpoint, datos);
        }
    }

    async function sincronizarRelaciones(contactoId, ruta, relacionesActuales, idsNuevos, propiedadId) {
        const endpoint = `/api/contactos/${contactoId}/${ruta}`;
        const actuales = new Set((relacionesActuales || []).map(x => Number(x[propiedadId])));
        const nuevos = new Set(idsNuevos);
        for (const id of nuevos) {
            if (!actuales.has(id)) {
                await API.post(endpoint, ruta === 'categorias' ? { categoriaId: id } : { organizacionId: id });
            }
        }
        for (const id of actuales) {
            if (!nuevos.has(id)) await API.delete(`${endpoint}/${id}`);
        }
    }

    async function abrirDetalles(id) {
        const contacto = contactos.find(c => c.id === id);
        if (!contacto) return;
        const m = crearModal(`${encabezadoModal(nombreCompleto(contacto), "Información y datos asociados al contacto.")}
            <div class="contacto-detalle-resumen"><div class="contacto-avatar contacto-avatar-grande">${escapar(inicial(contacto.nombre))}</div><div><strong>${escapar(contacto.apodo || "Sin apodo")}</strong><span>${contacto.favorito ? "★ Contacto favorito" : "Contacto personal"}</span></div></div>
            <div class="contacto-detalle-datos"><div><span>Fecha de nacimiento</span><strong>${fechaBonita(contacto.fechaNacimiento)}</strong></div><div><span>Notas</span><strong>${escapar(contacto.notas || "Sin notas")}</strong></div></div>
            <div id="contactoDetalleContenido"><p class="contacto-cargando-detalle">Cargando información adicional...</p></div>
            <div class="contacto-modal-acciones"><button type="button" class="contacto-boton-cancelar" data-cerrar-modal>Cerrar</button><button type="button" class="contacto-boton-guardar" id="editarDesdeDetalle">Editar contacto</button></div>`);
        m.querySelector("#editarDesdeDetalle").onclick = () => abrirFormularioContacto(contacto);
        try {
            const [telefonos, correos, direcciones, relacionesCategorias, categorias, relacionesOrganizaciones, organizaciones] = await Promise.all([
                API.get(`/api/contactos/${id}/telefonos`), API.get(`/api/contactos/${id}/correos`),
                API.get(`/api/contactos/${id}/direcciones`), API.get(`/api/contactos/${id}/categorias`),
                API.get("/api/categorias"), API.get(`/api/contactos/${id}/organizaciones`), API.get("/api/organizaciones")
            ]);
            if (modal !== m) return;
            const categoriasContacto = (relacionesCategorias || []).map(r => categorias.find(c => c.id === r.categoriaId)).filter(Boolean);
            const organizacionesContacto = (relacionesOrganizaciones || []).map(r => organizaciones.find(o => o.id === r.organizacionId)).filter(Boolean);
            m.querySelector("#contactoDetalleContenido").innerHTML = `
                ${seccionDetalle("Teléfonos", "telefonos", (telefonos || []).map(t => `<div class="contacto-item"><div><strong>${escapar((t.codigoPais || "") + " " + t.numero)}</strong><span>${textoTipo(t.tipo)}${t.esPrincipal ? " · Principal" : ""}</span></div><div>${botonesItem("telefono", t.id)}</div></div>`).join(""))}
                ${seccionDetalle("Correos electrónicos", "correos", (correos || []).map(c => `<div class="contacto-item"><div><strong>${escapar(c.correo)}</strong><span>${textoTipo(c.tipo)}${c.esPrincipal ? " · Principal" : ""}</span></div><div>${botonesItem("correo", c.id)}</div></div>`).join(""))}
                ${seccionDetalle("Direcciones", "direcciones", (direcciones || []).map(d => `<div class="contacto-item"><div><strong>${escapar(formatearDireccion(d))}</strong><span>${textoTipo(d.tipo)}${d.esPrincipal ? " · Principal" : ""}</span></div><div>${botonesItem("direccion", d.id)}</div></div>`).join(""))}
                ${seccionDetalle("Categorías", "categorias", categoriasContacto.map(c => `<span class="contacto-etiqueta" style="--categoria-color:${escapar(c.color || "#64748b")}">${escapar(c.nombre)}<button type="button" data-quitar-categoria="${c.id}" title="Quitar categoría">×</button></span>`).join(""))}
                ${seccionDetalle("Organizaciones", "organizaciones", organizacionesContacto.map(o => `<span class="contacto-etiqueta contacto-etiqueta-organizacion">${escapar(o.nombre)}<button type="button" data-quitar-organizacion="${o.id}" title="Quitar organización">×</button></span>`).join(""))}`;
            m.querySelectorAll("[data-agregar]").forEach(b => b.onclick = () => {
                const tipo = ({ telefonos: "telefono", correos: "correo", direcciones: "direccion" })[b.dataset.agregar];
                if (tipo) abrirFormularioDato(id, tipo, null, () => abrirDetalles(id));
            });
            m.querySelectorAll("[data-editar-tipo]").forEach(b => b.onclick = () => abrirFormularioDato(id, b.dataset.editarTipo, Number(b.dataset.id), () => abrirDetalles(id)));
            m.querySelectorAll("[data-eliminar-tipo]").forEach(b => b.onclick = () => eliminarDato(id, b.dataset.eliminarTipo, Number(b.dataset.id), () => abrirDetalles(id)));
            m.querySelectorAll("[data-quitar-categoria]").forEach(b => b.onclick = async () => {
                if (!await confirmarAccion("¿Quitar categoría?", "La categoría dejará de estar asociada a este contacto.")) return;
                try { await API.delete(`/api/contactos/${id}/categorias/${b.dataset.quitarCategoria}`); await abrirDetalles(id); }
                catch (e) { mostrarErrorEnDetalle(m, e); }
            });
            m.querySelectorAll("[data-quitar-organizacion]").forEach(b => b.onclick = async () => {
                if (!await confirmarAccion("¿Quitar organización?", "La organización dejará de estar asociada a este contacto.")) return;
                try { await API.delete(`/api/contactos/${id}/organizaciones/${b.dataset.quitarOrganizacion}`); await abrirDetalles(id); }
                catch (e) { mostrarErrorEnDetalle(m, e); }
            });
            m.querySelectorAll("[data-asignar-categoria]").forEach(b => b.onclick = () => abrirAsignacion(id, "categoria"));
            m.querySelectorAll("[data-asignar-organizacion]").forEach(b => b.onclick = () => abrirAsignacion(id, "organizacion"));
        } catch (error) {
            console.error("Error al cargar los detalles del contacto:", error);
            const zona = m.querySelector("#contactoDetalleContenido");
            if (zona) zona.innerHTML = `<p class="contacto-error">No fue posible cargar todos los datos asociados. ${escapar(error.message)}</p>`;
        }
    }

    function seccionDetalle(titulo, tipo, contenido) {
        const asignacion = tipo === "categorias" ? `<button type="button" data-asignar-categoria>＋ Asignar</button>` : tipo === "organizaciones" ? `<button type="button" data-asignar-organizacion>＋ Asignar</button>` : `<button type="button" data-agregar="${tipo}">＋ Agregar</button>`;
        return `<section class="contacto-detalle-seccion"><div class="contacto-detalle-seccion-cabecera"><h4>${titulo}</h4>${asignacion}</div><div class="contacto-detalle-lista">${contenido || `<span class="contacto-sin-datos">Sin ${titulo.toLowerCase()} registrados.</span>`}</div></section>`;
    }

    function botonesItem(tipo, id) {
        return `<button type="button" class="contacto-mini-accion" data-editar-tipo="${tipo}" data-id="${id}" title="Editar">✎</button><button type="button" class="contacto-mini-accion contacto-mini-eliminar" data-eliminar-tipo="${tipo}" data-id="${id}" title="Eliminar">×</button>`;
    }

    function formCampo(etiqueta, nombre, valor = "", tipo = "text", extra = "") {
        return `<div class="contacto-campo"><label for="dato_${nombre}">${etiqueta}</label><input id="dato_${nombre}" name="${nombre}" type="${tipo}" value="${escapar(valor)}" ${extra}></div>`;
    }

    function opcionesSelect(nombre, valores, seleccionado) {
        return `<div class="contacto-campo"><label for="dato_${nombre}">Tipo</label><select id="dato_${nombre}" name="${nombre}">${valores.map(v => `<option value="${v}" ${v === seleccionado ? "selected" : ""}>${textoTipo(v)}</option>`).join("")}</select></div>`;
    }

    function abrirFormularioDato(contactoId, tipo, datoId, alTerminar) {
        const rutas = { telefono: "telefonos", correo: "correos", direccion: "direcciones" };
        const singular = { telefono: "teléfono", correo: "correo", direccion: "dirección" };
        const endpoint = `/api/contactos/${contactoId}/${rutas[tipo]}`;
        const cargar = async () => datoId ? await API.get(endpoint) : null;
        cargar().then(lista => {
            // Las colecciones se consultan para localizar el registro que se editará.
            const dato = datoId ? (Array.isArray(lista) ? lista.find(x => x.id === datoId) : null) : null;
            if (datoId && !dato) throw new Error("No se encontró el dato que deseas editar.");
            let campos = "";
            if (tipo === "telefono") campos = `${formCampo("Número *", "numero", dato?.numero || "", "tel", "maxlength=30 required")}${formCampo("Código de país", "codigoPais", dato?.codigoPais || "", "text", "maxlength=5 placeholder=\"Ej. +52\"")}${opcionesSelect("tipo", TIPOS.telefono, dato?.tipo || "MOVIL")}`;
            if (tipo === "correo") campos = `${formCampo("Correo electrónico *", "correo", dato?.correo || "", "email", "maxlength=254 required")}${opcionesSelect("tipo", TIPOS.correo, dato?.tipo || "PERSONAL")}`;
            if (tipo === "direccion") campos = `${formCampo("Calle", "calle", dato?.calle)}<div class="contacto-campos-dobles">${formCampo("Número exterior", "numeroExterior", dato?.numeroExterior)}${formCampo("Número interior", "numeroInterior", dato?.numeroInterior)}</div>${formCampo("Colonia", "colonia", dato?.colonia)}<div class="contacto-campos-dobles">${formCampo("Ciudad", "ciudad", dato?.ciudad)}${formCampo("Estado", "estado", dato?.estado)}</div><div class="contacto-campos-dobles">${formCampo("Código postal", "codigoPostal", dato?.codigoPostal)}${formCampo("País", "pais", dato?.pais)}</div>${opcionesSelect("tipo", TIPOS.direccion, dato?.tipo || "CASA")}`;
            const m = crearModal(`${encabezadoModal(datoId ? `Editar ${singular[tipo]}` : `Agregar ${singular[tipo]}`, "Completa la información correspondiente.")}<form id="formDato">${campos}<label class="contacto-check-favorito"><input type="checkbox" name="esPrincipal" ${dato?.esPrincipal ? "checked" : ""}><span>★</span> Marcar como principal</label><p class="contacto-error" id="errorDato"></p><div class="contacto-modal-acciones"><button type="button" class="contacto-boton-cancelar" data-cerrar-modal>Cancelar</button><button type="submit" class="contacto-boton-guardar" id="guardarDato">Guardar</button></div></form>`);
            m.querySelector("#formDato").onsubmit = async e => {
                e.preventDefault();
                const f = e.currentTarget;
                const datos = {};
                for (const [k, v] of new FormData(f).entries()) datos[k] = v;
                datos.esPrincipal = f.esPrincipal.checked;
                if (tipo === "telefono") { datos.numero = f.numero.value.trim(); datos.codigoPais = f.codigoPais.value.trim() || null; }
                if (tipo === "correo") datos.correo = f.correo.value.trim();
                if (tipo === "direccion") for (const k of ["calle", "numeroExterior", "numeroInterior", "colonia", "ciudad", "estado", "codigoPostal", "pais"]) datos[k] = f[k].value.trim() || null;
                const boton = m.querySelector("#guardarDato"); boton.disabled = true;
                try {
                    if (datoId) await API.put(`${endpoint}/${datoId}`, datos); else await API.post(endpoint, datos);
                    cerrarModal(); await alTerminar();
                } catch (err) { m.querySelector("#errorDato").textContent = err.message || "No fue posible guardar."; boton.disabled = false; }
            };
        }).catch(error => alert(error.message || "No fue posible abrir el formulario."));
    }

    async function eliminarDato(contactoId, tipo, datoId) {
        const rutas = { telefono: "telefonos", correo: "correos", direccion: "direcciones" };
        if (!await confirmarAccion(`¿Eliminar ${tipo}?`, "Esta acción no se puede deshacer.")) return;
        try { await API.delete(`/api/contactos/${contactoId}/${rutas[tipo]}/${datoId}`); await abrirDetalles(contactoId); }
        catch (e) { mostrarErrorEnDetalle(modal, e); }
    }

    function confirmarAccion(titulo, mensaje) {
        return new Promise(resolve => {
            const m = crearModal(`<div class="contacto-confirmacion-icono">!</div><h3>${escapar(titulo)}</h3><p>${escapar(mensaje)}</p><div class="contacto-modal-acciones"><button type="button" class="contacto-boton-cancelar" id="cancelarAccion">Cancelar</button><button type="button" class="contacto-boton-eliminar" id="aceptarAccion">Sí, continuar</button></div>`, "contacto-confirmacion");
            let resuelto = false;
            const terminar = valor => { if (resuelto) return; resuelto = true; cerrarModal(); resolve(valor); };
            m.querySelector("#cancelarAccion").onclick = () => terminar(false);
            m.querySelector("#aceptarAccion").onclick = () => terminar(true);
            m.addEventListener("click", e => { if (e.target === m) terminar(false); });
        });
    }

    function mostrarErrorEnDetalle(m, error) {
        if (!m || !m.isConnected) return;
        const zona = m.querySelector("#contactoDetalleContenido");
        if (zona) {
            let aviso = zona.querySelector(".contacto-error");
            if (!aviso) { aviso = document.createElement("p"); aviso.className = "contacto-error"; zona.prepend(aviso); }
            aviso.textContent = error?.message || "No fue posible completar la operación.";
        }
    }

    function formatearDireccion(d) {
        return [d.calle, d.numeroExterior, d.numeroInterior ? `Int. ${d.numeroInterior}` : null, d.colonia, d.ciudad, d.estado, d.codigoPostal, d.pais].filter(Boolean).join(", ") || "Dirección sin descripción";
    }

    async function abrirAsignacion(contactoId, tipo) {
        const esCategoria = tipo === "categoria";
        const endpoint = esCategoria ? "/api/categorias" : "/api/organizaciones";
        const relacionEndpoint = `/api/contactos/${contactoId}/${esCategoria ? "categorias" : "organizaciones"}`;
        try {
            const [catalogo, asignadas] = await Promise.all([API.get(endpoint), API.get(relacionEndpoint)]);
            const ids = new Set((asignadas || []).map(x => esCategoria ? x.categoriaId : x.organizacionId));
            const disponibles = (catalogo || []).filter(x => !ids.has(x.id));
            const m = crearModal(`${encabezadoModal(`Asignar ${esCategoria ? "categoría" : "organización"}`, "Selecciona un elemento para asociarlo al contacto.")}
                ${disponibles.length ? `<div class="contacto-campo"><label for="seleccionAsignacion">${esCategoria ? "Categoría" : "Organización"}</label><select id="seleccionAsignacion">${disponibles.map(x => `<option value="${x.id}">${escapar(x.nombre)}</option>`).join("")}</select></div>` : `<p class="contacto-sin-datos">No hay elementos disponibles para asignar.</p>`}
                <p class="contacto-error" id="errorAsignacion"></p>
                <div class="contacto-modal-acciones contacto-acciones-asignacion"><button type="button" class="contacto-boton-cancelar" data-cerrar-modal>Cancelar</button><button type="button" class="contacto-boton-secundario" id="crearCatalogo">＋ Crear ${esCategoria ? "categoría" : "organización"}</button>${disponibles.length ? `<button type="button" class="contacto-boton-guardar" id="confirmarAsignacion">Asignar</button>` : ""}</div>`);
            m.querySelector("#crearCatalogo").onclick = () => abrirFormularioCatalogo(contactoId, tipo);
            if (!disponibles.length) return;
            m.querySelector("#confirmarAsignacion").onclick = async () => {
                const id = Number(m.querySelector("#seleccionAsignacion").value);
                const payload = esCategoria ? { categoriaId: id } : { organizacionId: id };
                const boton = m.querySelector("#confirmarAsignacion"); boton.disabled = true;
                try { await API.post(relacionEndpoint, payload); cerrarModal(); await abrirDetalles(contactoId); }
                catch (e) { m.querySelector("#errorAsignacion").textContent = e.message || "No fue posible asignar."; boton.disabled = false; }
            };
        } catch (e) { alert(e.message || "No fue posible cargar los elementos."); }
    }


    function abrirFormularioCatalogo(contactoId, tipo) {
        const esCategoria = tipo === "categoria";
        const campos = esCategoria
            ? `${formCampo("Nombre de la categoría *", "nombre", "", "text", "maxlength=80 required")}${formCampo("Color", "color", "#64748B", "color") }<div class="contacto-campo"><label for="catalogoDescripcion">Descripción</label><textarea id="catalogoDescripcion" name="descripcion" rows="2" maxlength="250"></textarea></div>`
            : `${formCampo("Nombre de la organización *", "nombre", "", "text", "maxlength=180 required")}${opcionesSelect("tipo", TIPOS.organizacion, "EMPRESA")}${formCampo("Sitio web", "sitioWeb", "", "url", 'maxlength=255 placeholder="https://"')}${formCampo("Teléfono general", "telefonoGeneral", "", "tel", "maxlength=30")}<div class="contacto-campo"><label for="catalogoDescripcion">Descripción</label><textarea id="catalogoDescripcion" name="descripcion" rows="2"></textarea></div>`;
        const m = crearModal(`${encabezadoModal(`Nueva ${esCategoria ? "categoría" : "organización"}`, "Se guardará en tu catálogo y quedará asignada a este contacto.")}<form id="formCatalogo">${campos}<p class="contacto-error" id="errorCatalogo"></p><div class="contacto-modal-acciones"><button type="button" class="contacto-boton-cancelar" data-cerrar-modal>Cancelar</button><button type="submit" class="contacto-boton-guardar" id="guardarCatalogo">Crear y asignar</button></div></form>`);
        m.querySelector("#formCatalogo").onsubmit = async e => {
            e.preventDefault();
            const f = e.currentTarget;
            const datos = { nombre: f.nombre.value.trim() };
            if (esCategoria) {
                datos.color = f.color.value || "#64748B";
                datos.descripcion = f.descripcion.value.trim() || null;
            } else {
                datos.tipo = f.tipo.value;
                datos.sitioWeb = f.sitioWeb.value.trim() || null;
                datos.telefonoGeneral = f.telefonoGeneral.value.trim() || null;
                datos.descripcion = f.descripcion.value.trim() || null;
            }
            const boton = m.querySelector("#guardarCatalogo"); boton.disabled = true;
            try {
                const creado = await API.post(esCategoria ? "/api/categorias" : "/api/organizaciones", datos);
                await API.post(`/api/contactos/${contactoId}/${esCategoria ? "categorias" : "organizaciones"}`, esCategoria ? { categoriaId: creado.id } : { organizacionId: creado.id });
                cerrarModal(); await abrirDetalles(contactoId);
            } catch (error) {
                m.querySelector("#errorCatalogo").textContent = error.message || "No fue posible crear el elemento.";
                boton.disabled = false;
            }
        };
    }

    async function confirmarEliminarContacto(id) {
        if (eliminando) return;
        const contacto = contactos.find(c => c.id === id);
        if (!contacto) return;
        const m = crearModal(`<div class="contacto-confirmacion-icono">!</div><h3>¿Eliminar contacto?</h3><p>¿Estás seguro de que deseas eliminar a <strong>${escapar(nombreCompleto(contacto))}</strong>? Esta acción no se puede deshacer.</p><p class="contacto-error" id="errorEliminarContacto"></p><div class="contacto-modal-acciones"><button type="button" class="contacto-boton-cancelar" data-cerrar-modal>Cancelar</button><button type="button" class="contacto-boton-eliminar" id="confirmarEliminarContacto">Sí, eliminar</button></div>`, "contacto-confirmacion");
        m.querySelector("#confirmarEliminarContacto").onclick = async () => {
            if (eliminando) return;
            eliminando = true;
            const boton = m.querySelector("#confirmarEliminarContacto"); boton.disabled = true; boton.textContent = "Eliminando...";
            try { await API.delete(`/api/contactos/${id}`); cerrarModal(); await cargarContactos(); }
            catch (e) { m.querySelector("#errorEliminarContacto").textContent = e.message || "No se pudo eliminar el contacto."; boton.disabled = false; boton.textContent = "Sí, eliminar"; }
            finally { eliminando = false; }
        };
    }

    return { mostrar, cargarContactos };
})();
