const Autenticacion = {
    usuario: null,

    inicializar() {
        document
            .getElementById("formLogin")
            .addEventListener("submit", this.iniciarSesion.bind(this));

        document
            .getElementById("formRegistro")
            .addEventListener("submit", this.registrarUsuario.bind(this));

        document
            .getElementById("mostrarRegistro")
            .addEventListener("click", () => this.mostrarFormulario("registro"));

        document
            .getElementById("mostrarLogin")
            .addEventListener("click", () => this.mostrarFormulario("login"));

        document
            .getElementById("btnCerrarSesion")
            .addEventListener("click", this.cerrarSesion.bind(this));

        this.comprobarSesion();
    },

    async comprobarSesion() {
        try {
            this.usuario = await API.get("/api/autenticacion/yo");
            this.mostrarAplicacion();
        } catch {
            this.mostrarAutenticacion();
        }
    },

    async iniciarSesion(evento) {
        evento.preventDefault();

        const correo = document.getElementById("loginCorreo").value.trim();
        const contrasena = document.getElementById("loginContrasena").value;
        const mensaje = document.getElementById("errorLogin");

        mensaje.textContent = "";

        try {
            this.usuario = await API.post("/api/autenticacion/login", {
                correo,
                contrasena
            });

            this.mostrarAplicacion();
        } catch (error) {
            mensaje.textContent = error.message;
        }
    },

    async registrarUsuario(evento) {
        evento.preventDefault();

        const nombre = document.getElementById("registroNombre").value.trim();
        const apellidos = document.getElementById("registroApellidos").value.trim();
        const correo = document.getElementById("registroCorreo").value.trim();
        const contrasena = document.getElementById("registroContrasena").value;
        const confirmacion = document.getElementById("registroConfirmacion").value;
        const mensaje = document.getElementById("errorRegistro");

        mensaje.textContent = "";

        if (contrasena !== confirmacion) {
            mensaje.textContent = "Las contraseñas no coinciden.";
            return;
        }

        try {
            await API.post("/api/usuarios/registro", {
                nombre,
                apellidos,
                correo,
                contrasena,
                zonaHoraria: "America/Mexico_City"
            });

            document.getElementById("loginCorreo").value = correo;
            document.getElementById("loginContrasena").value = "";
            this.mostrarFormulario("login");

            document.getElementById("errorLogin").textContent =
                "Cuenta creada. Ahora inicia sesión.";
        } catch (error) {
            mensaje.textContent = error.message;
        }
    },

    async cerrarSesion() {
        try {
			NotificacionesUI.detener();
            await API.post("/api/autenticacion/logout", {});
        } finally {
            this.usuario = null;
            this.mostrarAutenticacion();
        }
    },

    mostrarFormulario(tipo) {
        document.getElementById("formLogin").classList.toggle(
            "oculto",
            tipo !== "login"
        );

        document.getElementById("formRegistro").classList.toggle(
            "oculto",
            tipo !== "registro"
        );

        document.getElementById("errorLogin").textContent = "";
        document.getElementById("errorRegistro").textContent = "";
    },

    mostrarAutenticacion() {
        document.getElementById("pantallaAutenticacion").classList.remove("oculto");
        document.getElementById("aplicacion").classList.add("oculto");
        this.mostrarFormulario("login");
    },

    mostrarAplicacion() {
        document.getElementById("pantallaAutenticacion").classList.add("oculto");
        document.getElementById("aplicacion").classList.remove("oculto");

        if (this.usuario) {
            const nombreCompleto =
                `${this.usuario.nombre || ""} ${this.usuario.apellidos || ""}`.trim();

            document.getElementById("nombreUsuario").textContent =
                nombreCompleto || this.usuario.correo;

            document.getElementById("avatarUsuario").textContent =
                (this.usuario.nombre || "U").charAt(0).toUpperCase();
        }

        Aplicacion.inicializar();
    }
};

document.addEventListener("DOMContentLoaded", () => {
    Autenticacion.inicializar();
});