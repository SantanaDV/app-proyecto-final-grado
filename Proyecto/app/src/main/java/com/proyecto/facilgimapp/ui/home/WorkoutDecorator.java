package com.proyecto.facilgimapp.ui.home;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.proyecto.facilgimapp.R;
import java.util.HashSet;
import java.util.List;

/**
 * Decorador para el calendario que marca con un punto las fechas en las que hay entrenamientos.
 * <p>
 * Convierte una lista de {@link java.time.LocalDate} a un conjunto de {@link CalendarDay}
 * y, para cada día que coincida, añade un punto de color en la vista del día.
 * </p>
 * 
 * @author Francisco Santana
 */
public class WorkoutDecorator implements DayViewDecorator {
    /**
     * Conjunto de fechas que deben ser decoradas (punteadas) en el calendario.
     */
    private final HashSet<CalendarDay> dates;

    /**
     * Color que se utilizará para el punto que marca el entrenamiento en la fecha.
     */
    private final int color;

    /**
     * Crea un nuevo {@code WorkoutDecorator} a partir de una lista de fechas de entrenamiento.
     * <p>
     * Convierte cada {@link java.time.LocalDate} de la lista en un {@link CalendarDay}
     * y lo añade al conjunto interno. Obtiene el color desde los recursos (R.color.green_500)
     * para usarlo en la decoración.
     * </p>
     *
     * @param context Contexto de la aplicación, necesario para obtener el color desde recursos.
     * @param list    Lista de {@link java.time.LocalDate} con las fechas de entrenamientos a decorar.
     */
    public WorkoutDecorator(Context context, List<java.time.LocalDate> list) {
        dates = new HashSet<>();
        for (java.time.LocalDate d : list) {
            dates.add(CalendarDay.from(d.getYear(), d.getMonthValue(), d.getDayOfMonth()));
        }
        color = ContextCompat.getColor(context, R.color.green_500);
    }

    /**
     * Determina si un día concreto debe ser decorado.
     * <p>
     * Retorna {@code true} si el {@code CalendarDay} recibido está contenido en el conjunto
     * de fechas de entrenamiento, {@code false} en caso contrario.
     * </p>
     *
     * @param day Instancia de {@link CalendarDay} que representa el día a verificar.
     * @return {@code true} si ese día coincide con un entrenamiento; {@code false} si no.
     */
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    /**
     * Aplica la decoración al día especificado.
     * <p>
     * Añade un punto de tamaño 8 y del color definido en el constructor
     * a la {@link DayViewFacade} para indicar la presencia de un entrenamiento.
     * </p>
     *
     * @param view Instancia de {@link DayViewFacade} sobre la que se aplicará la decoración.
     */
    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new com.prolificinteractive.materialcalendarview.spans.DotSpan(8, color));
    }
}
