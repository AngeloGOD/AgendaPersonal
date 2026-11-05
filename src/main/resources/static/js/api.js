const API = {
    async solicitar(ruta, opciones = {}) {
        const configuracion = {
            method: opciones.method || "GET",
            credentials: "same-origin",
            headers: {
                "Content-Type": "application/json",
                ...(opciones.headers || {})
            }
        };

        if (opciones.body !== undefined) {
            configuracion.body = JSON.stringify(opciones.body);
        }

        const respuesta = await fetch(ruta, configuracion);

        if (respuesta.status === 204) {
            return null;
        }

        const contenido = await respuesta.text();
        let datos = null;

        if (contenido) {
            try {
                datos = JSON.parse(contenido);
            } catch {
                datos = contenido;
            }
        }

        if (!respuesta.ok) {
            const mensaje =
                datos?.message ||
                datos?.mensaje ||
                (typeof datos === "string" ? datos : null) ||
                `Error en la solicitud (${respuesta.status})`;

            throw new Error(mensaje);
        }

        return datos;
    },

    get(ruta) {
        return this.solicitar(ruta);
    },

    post(ruta, datos) {
        return this.solicitar(ruta, {
            method: "POST",
            body: datos
        });
    },

    put(ruta, datos) {
        return this.solicitar(ruta, {
            method: "PUT",
            body: datos
        });
    },

    delete(ruta) {
        return this.solicitar(ruta, {
            method: "DELETE"
        });
    }
};