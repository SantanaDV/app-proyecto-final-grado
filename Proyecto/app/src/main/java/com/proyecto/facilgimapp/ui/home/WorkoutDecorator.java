package com.proyecto.facilgimapp.ui.home;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.proyecto.facilgimapp.R;
import java.util.HashSet;
import java.util.List;

public class WorkoutDecorator implements DayViewDecorator {
    private final HashSet<CalendarDay> dates;
    private final int color;

    public WorkoutDecorator(Context context, List<java.time.LocalDate> list) {
        dates = new HashSet<>();
        for (java.time.LocalDate d : list) {
            dates.add(CalendarDay.from(d.getYear(), d.getMonthValue(), d.getDayOfMonth()));
        }
        color = ContextCompat.getColor(context, R.color.green_500);
    }

    @Override public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override public void decorate(DayViewFacade view) {
        view.addSpan(new com.prolificinteractive.materialcalendarview.spans.DotSpan(8, color));
    }
}
