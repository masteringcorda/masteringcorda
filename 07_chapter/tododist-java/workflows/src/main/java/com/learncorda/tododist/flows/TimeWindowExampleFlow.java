package com.learncorda.tododist.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.TimeWindow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.TimeZone;

@StartableByRPC
public class TimeWindowExampleFlow extends FlowLogic<Void> {

    @Suspendable
    @Override
    public Void call() throws FlowException {
        DateFormat formatter = new SimpleDateFormat("MMMM d, yyyy hh:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("EST"));
        Instant fromInstant = null;
        Instant untilInstant = null;
        String from = "January 1, 2021 09:00:00";
        String until = "January 1, 2021 16:00:00";

        try {
            fromInstant = formatter.parse(from).toInstant();
            System.out.println(("fromInstant: " + fromInstant));
            untilInstant = formatter.parse(until).toInstant();
            System.out.println(("untilInstant: " + untilInstant));
        } catch (Exception e) {}

        // No earlier than Jan 1, 2021 9AM
        TimeWindow timeWindow = null;

        timeWindow = TimeWindow.fromOnly(fromInstant);
        System.out.println("Time window fromOnly: " + timeWindow);


        timeWindow = TimeWindow.untilOnly(untilInstant);
        System.out.println("Time window untilOnly: " + timeWindow);
        timeWindow = TimeWindow.between(fromInstant,untilInstant);
        System.out.println("Time window between: " + timeWindow);


        timeWindow = TimeWindow.fromStartAndDuration(fromInstant, Duration.ofDays(1));
        System.out.println("Time window fromStartAndDuration: " + timeWindow);

        timeWindow = TimeWindow.withTolerance(untilInstant,Duration.ofMinutes(5));
        System.out.println("Time window withTolerance: " + timeWindow);

        final Instant midpoint = timeWindow.getMidpoint();
        System.out.println("Midpoint: " + midpoint);
        return null;
    }
}
