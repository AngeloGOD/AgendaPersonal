package com.agenda.personal.servicio;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.dto.EventoCalendarioDTO;
import com.agenda.personal.entidad.Evento;
import com.agenda.personal.entidad.ExcepcionRecurrencia;
import com.agenda.personal.entidad.ReglaRecurrencia;
import com.agenda.personal.enums.FrecuenciaRecurrencia;
import com.agenda.personal.enums.TipoExcepcion;
import com.agenda.personal.repositorio.ExcepcionRecurrenciaRepository;
import com.agenda.personal.repositorio.ReglaRecurrenciaRepository;

@Service
@Transactional(readOnly = true)
public class ServicioCalendario {

	private static final ZoneId ZONA = ZoneId.systemDefault();

	private static final int MAX_DIAS_GENERACION = 100_000;

	private final ServicioEvento servicioEvento;
	private final ReglaRecurrenciaRepository reglaRepository;
	private final ExcepcionRecurrenciaRepository excepcionRepository;

	public ServicioCalendario(ServicioEvento servicioEvento, ReglaRecurrenciaRepository reglaRepository,
			ExcepcionRecurrenciaRepository excepcionRepository) {

		this.servicioEvento = servicioEvento;
		this.reglaRepository = reglaRepository;
		this.excepcionRepository = excepcionRepository;
	}

	public List<EventoCalendarioDTO> obtenerEventos(Long usuarioId, Instant fechaInicio, Instant fechaFin) {

		if (usuarioId == null) {
			throw new IllegalArgumentException("El usuario es obligatorio.");
		}

		if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {

			throw new IllegalArgumentException("El intervalo de fechas no es válido.");
		}

		List<ReglaRecurrencia> reglas = reglaRepository.buscarReglasDelUsuario(usuarioId, fechaFin);

		Set<Long> eventosRecurrentes = new HashSet<>();

		List<Long> idsEventos = new ArrayList<>();

		for (ReglaRecurrencia regla : reglas) {
			Long eventoId = regla.getEvento().getId();

			eventosRecurrentes.add(eventoId);
			idsEventos.add(eventoId);
		}

		List<ExcepcionRecurrencia> excepciones = idsEventos.isEmpty() ? List.of()
				: excepcionRepository.findAllByEvento_IdInOrderByFechaOriginalAsc(idsEventos);

		Map<Long, List<ExcepcionRecurrencia>> excepcionesPorEvento = new HashMap<>();

		for (ExcepcionRecurrencia excepcion : excepciones) {

			Long eventoId = excepcion.getEvento().getId();

			excepcionesPorEvento.computeIfAbsent(eventoId, id -> new ArrayList<>()).add(excepcion);
		}

		List<EventoCalendarioDTO> resultado = new ArrayList<>();

		// Agregar eventos que no tienen recurrencia.
		List<Evento> eventosNormales = servicioEvento.obtenerEventos(usuarioId, fechaInicio, fechaFin);

		for (Evento evento : eventosNormales) {

			if (eventosRecurrentes.contains(evento.getId())) {
				continue;
			}

			resultado.add(new EventoCalendarioDTO(evento, evento.getFechaInicio(), evento.getFechaFin(),
					evento.getUbicacion(), false, null));
		}

		// Generar las ocurrencias de los eventos recurrentes.
		for (ReglaRecurrencia regla : reglas) {

			Evento evento = regla.getEvento();

			List<ExcepcionRecurrencia> excepcionesEvento = excepcionesPorEvento.getOrDefault(evento.getId(), List.of());

			resultado.addAll(generarOcurrencias(evento, regla, excepcionesEvento, fechaInicio, fechaFin));
		}

		resultado.sort(Comparator.comparing(EventoCalendarioDTO::getFechaInicio));

		return resultado;
	}

	private List<EventoCalendarioDTO> generarOcurrencias(Evento evento, ReglaRecurrencia regla,
			List<ExcepcionRecurrencia> excepciones, Instant rangoInicio, Instant rangoFin) {

		List<EventoCalendarioDTO> resultado = new ArrayList<>();

		Instant inicioOriginal = evento.getFechaInicio();
		Instant finOriginal = evento.getFechaFin();

		Duration duracion = Duration.between(inicioOriginal, finOriginal);

		LocalDate fechaBase = inicioOriginal.atZone(ZONA).toLocalDate();

		LocalDate fechaFinOriginal = finOriginal.atZone(ZONA).toLocalDate();

		boolean eventoOriginalVariosDias = !fechaBase.equals(fechaFinOriginal);

		LocalTime horaBase = inicioOriginal.atZone(ZONA).toLocalTime();

		LocalDate fechaLimite = rangoFin.atZone(ZONA).toLocalDate();

		if (regla.getFechaFin() != null && regla.getFechaFin().isBefore(fechaLimite)) {

			fechaLimite = regla.getFechaFin();
		}

		if (fechaLimite.isBefore(fechaBase)) {
			return resultado;
		}

		Map<Instant, ExcepcionRecurrencia> excepcionesPorFecha = new HashMap<>();

		for (ExcepcionRecurrencia excepcion : excepciones) {
			excepcionesPorFecha.put(excepcion.getFechaOriginal(), excepcion);
		}

		Set<Instant> fechasProcesadas = new HashSet<>();

		int ocurrenciasGeneradas = 0;

		LocalDate fecha = fechaBase;

		int diasRevisados = 0;

		while (!fecha.isAfter(fechaLimite) && diasRevisados < MAX_DIAS_GENERACION) {

			diasRevisados++;

			boolean esOcurrencia = fecha.equals(fechaBase) || correspondeARegla(fechaBase, fecha, regla);

			if (esOcurrencia) {

				ocurrenciasGeneradas++;

				if (regla.getNumeroOcurrencias() != null && ocurrenciasGeneradas > regla.getNumeroOcurrencias()) {
					break;
				}

				Instant inicioOcurrencia = fecha.atTime(horaBase).atZone(ZONA).toInstant();

				Instant finOcurrencia = calcularFinOcurrencia(inicioOcurrencia, duracion, eventoOriginalVariosDias);

				fechasProcesadas.add(inicioOcurrencia);

				ExcepcionRecurrencia excepcion = excepcionesPorFecha.get(inicioOcurrencia);

				if (excepcion == null) {

					agregarSiCoincide(resultado, evento, inicioOcurrencia, finOcurrencia, evento.getUbicacion(),
							inicioOcurrencia, rangoInicio, rangoFin);

				} else if (excepcion.getTipo() == TipoExcepcion.MODIFICAR) {

					Instant nuevoInicio = excepcion.getNuevaFechaInicio();

					Instant nuevoFin = excepcion.getNuevaFechaFin() != null ? excepcion.getNuevaFechaFin()
							: calcularFinOcurrencia(nuevoInicio, duracion, eventoOriginalVariosDias);

					String nuevaUbicacion = excepcion.getNuevaUbicacion() != null ? excepcion.getNuevaUbicacion()
							: evento.getUbicacion();

					agregarSiCoincide(resultado, evento, nuevoInicio, nuevoFin, nuevaUbicacion, inicioOcurrencia,
							rangoInicio, rangoFin);
				}
			}

			fecha = fecha.plusDays(1);
		}

		// Mostrar también las ocurrencias modificadas que fueron
		// trasladadas desde fuera del intervalo consultado hacia dentro.
		for (ExcepcionRecurrencia excepcion : excepciones) {

			if (excepcion.getTipo() != TipoExcepcion.MODIFICAR) {
				continue;
			}

			if (fechasProcesadas.contains(excepcion.getFechaOriginal())) {
				continue;
			}

			Instant nuevoInicio = excepcion.getNuevaFechaInicio();

			Instant nuevoFin = excepcion.getNuevaFechaFin() != null ? excepcion.getNuevaFechaFin()
					: calcularFinOcurrencia(nuevoInicio, duracion, eventoOriginalVariosDias);

			String nuevaUbicacion = excepcion.getNuevaUbicacion() != null ? excepcion.getNuevaUbicacion()
					: evento.getUbicacion();

			agregarSiCoincide(resultado, evento, nuevoInicio, nuevoFin, nuevaUbicacion, excepcion.getFechaOriginal(),
					rangoInicio, rangoFin);
		}

		return resultado;
	}

	private Instant calcularFinOcurrencia(Instant inicio, Duration duracionOriginal, boolean eventoOriginalVariosDias) {

		if (!eventoOriginalVariosDias) {
			return inicio.plus(duracionOriginal);
		}

		LocalDate fecha = inicio.atZone(ZONA).toLocalDate();

		return fecha.atTime(LocalTime.MAX).atZone(ZONA).toInstant();
	}

	private boolean correspondeARegla(LocalDate fechaBase, LocalDate fecha, ReglaRecurrencia regla) {

		int intervalo = regla.getIntervalo();

		if (intervalo <= 0) {
			return false;
		}

		FrecuenciaRecurrencia frecuencia = regla.getFrecuencia();

		switch (frecuencia) {

		case DIARIA: {

			long dias = ChronoUnit.DAYS.between(fechaBase, fecha);

			return dias % intervalo == 0;
		}

		case SEMANAL: {

			LocalDate lunesBase = fechaBase.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

			LocalDate lunesFecha = fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

			long semanas = ChronoUnit.WEEKS.between(lunesBase, lunesFecha);

			if (semanas % intervalo != 0) {
				return false;
			}

			Short dia = (short) fecha.getDayOfWeek().getValue();

			if (regla.getDiasSemana() == null) {
				return false;
			}

			for (Short seleccionado : regla.getDiasSemana()) {
				if (dia.equals(seleccionado)) {
					return true;
				}
			}

			return false;
		}

		case MENSUAL: {

			long meses = ChronoUnit.MONTHS.between(YearMonth.from(fechaBase), YearMonth.from(fecha));

			return meses > 0 && meses % intervalo == 0 && fecha.getDayOfMonth() == regla.getDiaMes();
		}

		case ANUAL: {

			long anios = fecha.getYear() - fechaBase.getYear();

			return anios > 0 && anios % intervalo == 0 && fecha.getMonthValue() == fechaBase.getMonthValue()
					&& fecha.getDayOfMonth() == fechaBase.getDayOfMonth();
		}

		default:
			return false;
		}
	}

	private void agregarSiCoincide(List<EventoCalendarioDTO> resultado, Evento evento, Instant inicio, Instant fin,
			String ubicacion, Instant fechaOriginal, Instant rangoInicio, Instant rangoFin) {

		boolean coincide = !inicio.isAfter(rangoFin) && !fin.isBefore(rangoInicio);

		if (!coincide) {
			return;
		}

		resultado.add(new EventoCalendarioDTO(evento, inicio, fin, ubicacion, true, fechaOriginal));
	}
}