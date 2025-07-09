package ru.uwurahara.todolistapplication.util;

import ru.uwurahara.todolistapplication.enumerations.Status;

import java.util.Map;

public final class StatusSortUtil {
    public static final Map<Status, Integer> statusOrderAsc = Map.of(
            Status.TODO, 1,
            Status.IN_PROGRESS, 2,
            Status.DONE, 3
    );
}
