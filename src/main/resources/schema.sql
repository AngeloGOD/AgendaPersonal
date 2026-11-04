
BEGIN;

-- =====================================================
-- 1. USUARIO
-- =====================================================

CREATE TABLE usuario (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    correo VARCHAR(254) NOT NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    zona_horaria VARCHAR(60) NOT NULL DEFAULT 'America/Mexico_City',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_usuario_correo
ON usuario (LOWER(correo));


-- =====================================================
-- 2. CONTACTO
-- =====================================================

CREATE TABLE contacto (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150),
    apodo VARCHAR(100),
    fecha_nacimiento DATE,
    notas TEXT,
    favorito BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_contacto_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_contacto_id_usuario UNIQUE (id, usuario_id)
);


-- =====================================================
-- 3. ORGANIZACION
-- =====================================================

CREATE TABLE organizacion (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nombre VARCHAR(180) NOT NULL,
    tipo VARCHAR(30) NOT NULL DEFAULT 'EMPRESA',
    sitio_web VARCHAR(255),
    telefono_general VARCHAR(30),
    descripcion TEXT,

    CONSTRAINT fk_organizacion_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_organizacion_id_usuario UNIQUE (id, usuario_id),

    CONSTRAINT chk_organizacion_tipo
        CHECK (tipo IN ('EMPRESA', 'INSTITUCION', 'OTRA'))
);


-- =====================================================
-- 4. CATEGORIA_CONTACTO
-- =====================================================

CREATE TABLE categoria_contacto (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    color VARCHAR(7) DEFAULT '#64748B',
    descripcion VARCHAR(250),

    CONSTRAINT fk_categoria_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_categoria_id_usuario UNIQUE (id, usuario_id),

    CONSTRAINT chk_categoria_color
        CHECK (color ~ '^#[0-9A-Fa-f]{6}$')
);


-- =====================================================
-- 5. EVENTO
-- =====================================================

CREATE TABLE evento (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    titulo VARCHAR(180) NOT NULL,
    descripcion TEXT,
    fecha_inicio TIMESTAMPTZ NOT NULL,
    fecha_fin TIMESTAMPTZ NOT NULL,
    ubicacion VARCHAR(255),
    tipo VARCHAR(30) NOT NULL DEFAULT 'OTRO',
    estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADO',
    todo_el_dia BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_evento_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_evento_id_usuario UNIQUE (id, usuario_id),

    CONSTRAINT chk_evento_fechas
        CHECK (fecha_fin >= fecha_inicio),

    CONSTRAINT chk_evento_tipo
        CHECK (tipo IN (
            'CITA', 'REUNION', 'CUMPLEANOS',
            'ANIVERSARIO', 'TAREA', 'OTRO'
        )),

    CONSTRAINT chk_evento_estado
        CHECK (estado IN (
            'PROGRAMADO', 'COMPLETADO', 'CANCELADO'
        ))
);


-- =====================================================
-- 6. TELEFONO_CONTACTO
-- =====================================================

CREATE TABLE telefono_contacto (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contacto_id BIGINT NOT NULL,
    numero VARCHAR(30) NOT NULL,
    codigo_pais VARCHAR(5),
    tipo VARCHAR(20) NOT NULL DEFAULT 'MOVIL',
    es_principal BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_telefono_contacto
        FOREIGN KEY (contacto_id) REFERENCES contacto(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_telefono_tipo
        CHECK (tipo IN ('MOVIL', 'CASA', 'TRABAJO', 'OTRO'))
);

CREATE UNIQUE INDEX uq_telefono_principal
ON telefono_contacto(contacto_id)
WHERE es_principal = TRUE;


-- =====================================================
-- 7. CORREO_CONTACTO
-- =====================================================

CREATE TABLE correo_contacto (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contacto_id BIGINT NOT NULL,
    correo VARCHAR(254) NOT NULL,
    tipo VARCHAR(20) NOT NULL DEFAULT 'PERSONAL',
    es_principal BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_correo_contacto
        FOREIGN KEY (contacto_id) REFERENCES contacto(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_correo_tipo
        CHECK (tipo IN ('PERSONAL', 'TRABAJO', 'OTRO'))
);

CREATE UNIQUE INDEX uq_correo_contacto
ON correo_contacto(contacto_id, LOWER(correo));

CREATE UNIQUE INDEX uq_correo_principal
ON correo_contacto(contacto_id)
WHERE es_principal = TRUE;


-- =====================================================
-- 8. DIRECCION_CONTACTO
-- =====================================================

CREATE TABLE direccion_contacto (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contacto_id BIGINT NOT NULL,
    calle VARCHAR(150),
    numero_exterior VARCHAR(20),
    numero_interior VARCHAR(20),
    colonia VARCHAR(100),
    ciudad VARCHAR(100),
    estado VARCHAR(100),
    codigo_postal VARCHAR(15),
    pais VARCHAR(80),
    tipo VARCHAR(20) NOT NULL DEFAULT 'CASA',
    es_principal BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_direccion_contacto
        FOREIGN KEY (contacto_id) REFERENCES contacto(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_direccion_tipo
        CHECK (tipo IN ('CASA', 'TRABAJO', 'OTRO'))
);

CREATE UNIQUE INDEX uq_direccion_principal
ON direccion_contacto(contacto_id)
WHERE es_principal = TRUE;


-- =====================================================
-- 9. CONTACTO_CATEGORIA
-- =====================================================

CREATE TABLE contacto_categoria (
    usuario_id BIGINT NOT NULL,
    contacto_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,

    CONSTRAINT pk_contacto_categoria
        PRIMARY KEY (contacto_id, categoria_id),

    CONSTRAINT fk_cc_contacto
        FOREIGN KEY (contacto_id, usuario_id)
        REFERENCES contacto(id, usuario_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_cc_categoria
        FOREIGN KEY (categoria_id, usuario_id)
        REFERENCES categoria_contacto(id, usuario_id)
        ON DELETE CASCADE
);


-- =====================================================
-- 10. CONTACTO_ORGANIZACION
-- =====================================================

CREATE TABLE contacto_organizacion (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    contacto_id BIGINT NOT NULL,
    organizacion_id BIGINT NOT NULL,
    cargo VARCHAR(120),
    departamento VARCHAR(120),
    fecha_inicio DATE,
    fecha_fin DATE,

    CONSTRAINT fk_co_contacto
        FOREIGN KEY (contacto_id, usuario_id)
        REFERENCES contacto(id, usuario_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_co_organizacion
        FOREIGN KEY (organizacion_id, usuario_id)
        REFERENCES organizacion(id, usuario_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_co_fechas
        CHECK (
            fecha_fin IS NULL
            OR fecha_inicio IS NULL
            OR fecha_fin >= fecha_inicio
        ),

    CONSTRAINT uq_contacto_organizacion
        UNIQUE (contacto_id, organizacion_id)
);


-- =====================================================
-- 11. PARTICIPANTE_EVENTO
-- =====================================================

CREATE TABLE participante_evento (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    evento_id BIGINT NOT NULL,
    contacto_id BIGINT NOT NULL,
    estado_invitacion VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',

    CONSTRAINT fk_pe_evento
        FOREIGN KEY (evento_id, usuario_id)
        REFERENCES evento(id, usuario_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pe_contacto
        FOREIGN KEY (contacto_id, usuario_id)
        REFERENCES contacto(id, usuario_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_participante
        UNIQUE (evento_id, contacto_id),

    CONSTRAINT chk_participante_estado
        CHECK (estado_invitacion IN (
            'PENDIENTE', 'ACEPTADO', 'RECHAZADO'
        ))
);


-- =====================================================
-- 12. RECORDATORIO
-- =====================================================

CREATE TABLE recordatorio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    minutos_antes INTEGER NOT NULL DEFAULT 15,
    tipo VARCHAR(20) NOT NULL DEFAULT 'NOTIFICACION',
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_recordatorio_evento
        FOREIGN KEY (evento_id) REFERENCES evento(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_recordatorio_minutos
        CHECK (minutos_antes >= 0),

    CONSTRAINT uq_recordatorio
        UNIQUE (evento_id, minutos_antes, tipo)
);


-- =====================================================
-- 13. REGLA_RECURRENCIA
-- =====================================================

CREATE TABLE regla_recurrencia (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id BIGINT NOT NULL UNIQUE,
    frecuencia VARCHAR(20) NOT NULL,
    intervalo INTEGER NOT NULL DEFAULT 1,
    dias_semana SMALLINT[],
    dia_mes INTEGER,
    fecha_fin DATE,
    numero_ocurrencias INTEGER,

    CONSTRAINT fk_recurrencia_evento
        FOREIGN KEY (evento_id) REFERENCES evento(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_recurrencia_frecuencia
        CHECK (frecuencia IN (
            'DIARIA', 'SEMANAL', 'MENSUAL', 'ANUAL'
        )),

    CONSTRAINT chk_recurrencia_intervalo
        CHECK (intervalo > 0),

    CONSTRAINT chk_recurrencia_dias
        CHECK (
            dias_semana IS NULL
            OR dias_semana <@ ARRAY[1,2,3,4,5,6,7]::SMALLINT[]
        ),

    CONSTRAINT chk_recurrencia_dia_mes
        CHECK (dia_mes IS NULL OR dia_mes BETWEEN 1 AND 31),

    CONSTRAINT chk_recurrencia_semanal
        CHECK (frecuencia <> 'SEMANAL' OR dias_semana IS NOT NULL),

    CONSTRAINT chk_recurrencia_mensual
        CHECK (frecuencia <> 'MENSUAL' OR dia_mes IS NOT NULL),

    CONSTRAINT chk_recurrencia_limite
        CHECK (numero_ocurrencias IS NULL OR numero_ocurrencias > 0),

    CONSTRAINT chk_recurrencia_fin
        CHECK (fecha_fin IS NULL OR numero_ocurrencias IS NULL)
);


-- =====================================================
-- 14. EXCEPCION_RECURRENCIA
-- =====================================================

CREATE TABLE excepcion_recurrencia (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    fecha_original TIMESTAMPTZ NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    nueva_fecha_inicio TIMESTAMPTZ,
    nueva_fecha_fin TIMESTAMPTZ,
    nueva_ubicacion VARCHAR(255),

    CONSTRAINT fk_excepcion_evento
        FOREIGN KEY (evento_id) REFERENCES evento(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_excepcion_ocurrencia
        UNIQUE (evento_id, fecha_original),

    CONSTRAINT chk_excepcion_tipo
        CHECK (tipo IN ('OMITIR', 'MODIFICAR')),

    CONSTRAINT chk_excepcion_datos
        CHECK (
            (
                tipo = 'OMITIR'
                AND nueva_fecha_inicio IS NULL
                AND nueva_fecha_fin IS NULL
                AND nueva_ubicacion IS NULL
            )
            OR
            (
                tipo = 'MODIFICAR'
                AND nueva_fecha_inicio IS NOT NULL
            )
        ),

    CONSTRAINT chk_excepcion_fechas
        CHECK (
            nueva_fecha_fin IS NULL
            OR nueva_fecha_inicio IS NULL
            OR nueva_fecha_fin >= nueva_fecha_inicio
        )
);


-- =====================================================
-- INDICES PARA MEJORAR LAS CONSULTAS
-- =====================================================

CREATE INDEX idx_contacto_usuario_nombre
ON contacto(usuario_id, LOWER(nombre), LOWER(apellidos));

CREATE INDEX idx_organizacion_usuario
ON organizacion(usuario_id);

CREATE INDEX idx_categoria_usuario
ON categoria_contacto(usuario_id);

CREATE INDEX idx_evento_usuario_inicio
ON evento(usuario_id, fecha_inicio);

CREATE INDEX idx_telefono_contacto
ON telefono_contacto(contacto_id);

CREATE INDEX idx_direccion_contacto
ON direccion_contacto(contacto_id);

CREATE INDEX idx_contacto_organizacion_org
ON contacto_organizacion(organizacion_id);

CREATE INDEX idx_participante_contacto
ON participante_evento(contacto_id);

CREATE INDEX idx_recordatorio_evento
ON recordatorio(evento_id);

CREATE INDEX idx_excepcion_evento
ON excepcion_recurrencia(evento_id);


COMMIT;
