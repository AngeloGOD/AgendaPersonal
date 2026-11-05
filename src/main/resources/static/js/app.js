
const Aplicacion = {

    vistaActual: "inicio",

    inicializar() {

        // Configurar navegación lateral
        document.querySelectorAll(".nav-item").forEach(boton => {

            boton.onclick = () => {
                this.cambiarVista(boton.dataset.vista);
            };

        });

        // Botón para crear eventos
        document.getElementById("btnCrear").onclick = () => {
            EventosUI.abrir();
        };

        // Mostrar pantalla inicial
        this.cambiarVista(this.vistaActual);
    },

    cambiarVista(vista) {

        this.vistaActual = vista;

        // Actualizar botón seleccionado
        document.querySelectorAll(".nav-item").forEach(boton => {

            boton.classList.toggle(
                "activo",
                boton.dataset.vista === vista
            );

        });

        // Títulos de las pantallas
        const titulos = {
            inicio: "Inicio",
            calendario: "Calendario",
            contactos: "Contactos",
            recordatorios: "Recordatorios"
        };

        document.getElementById("tituloVista").textContent =
            titulos[vista] || "AgendaPersonal";

        this.mostrarContenido(vista);
    },

    mostrarContenido(vista) {

        const contenido = document.getElementById("contenidoVista");

		// INICIO
		if (vista === "inicio") {

		    InicioUI.mostrar(contenido);

		    return;
		}

        // CALENDARIO
        if (vista === "calendario") {

            Calendario.mostrar(contenido);

            return;
        }

        // CONTACTOS
        if (vista === "contactos") {

            ContactosUI.mostrar(contenido);

            return;
        }

        // RECORDATORIOS
        if (vista === "recordatorios") {

            RecordatoriosUI.mostrar(contenido);

            return;
        }

        // OTRAS SECCIONES
        contenido.innerHTML = `
	        <div class="vista-placeholder">
	            <h2>${titulos[vista] || "Inicio"}</h2>
	            <p>Esta sección estará disponible próximamente.</p>
	        </div>
	    `;

    }

};
