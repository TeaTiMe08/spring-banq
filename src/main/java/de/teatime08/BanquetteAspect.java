package de.teatime08;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Aspect
@Component
public class BanquetteAspect {
    /**
     * For each method assigned to the @Banq Annotations, define a List of measures stored in this HashMap.
     */
    private final Map<String, LinkedList<BanqTick>> banqMeasures = new HashMap<>();

    /**
     * A BanqTick is a wrapper for a measure of ticks made with Banq.
     * @param count The recent number of the tick (identifier)
     * @param tickDate The LocalDateTime of the Tick, after method was executed
     * @param benchNanoTime The performance measure in nano-seconds. How long the method needed to execute.
     */
    public record BanqTick(int count, LocalDateTime tickDate, Long benchNanoTime){
        @Override
        public String toString() {
            return "BanqTick{" + "count=" + count + ", tickDate=" + tickDate +", benchNanoTime=" + benchNanoTime + '}';
        }
    }

    /**
     * Uses Spring AOP to intercept the method execution in order to execute a BanqTick measurement.
     * @param joinPoint The point on which the execution was stopped, holds information on the reflection.
     * @return the object returned from the method.
     * @throws Throwable if exeuction failed.
     */
    @Around(value = "@annotation(Banq)")
    public Object banq(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        LinkedList<BanqTick> keyTimes = banqMeasures.get(key);
        if (keyTimes == null) {
            keyTimes = new LinkedList<>();
            banqMeasures.put(key, keyTimes);
        }

        final long start = System.nanoTime();
        final Object proceed = joinPoint.proceed();
        final long executionNanoTime = System.nanoTime() - start;

        BanqTick tick = new BanqTick(banqMeasures.size(), LocalDateTime.now(), executionNanoTime);
        keyTimes.add(tick);

        return proceed;
    }

    /**
     * @return Gets the current stored Measures.
     */
    public Map<String, LinkedList<BanqTick>> getBanqMeasures() {
        return banqMeasures;
    }

    /**
     * Prints all the measures to a big String, implemented for logging etc. Unix Line seperator.
     * @return A big string for all measures made.
     */
    public String printToString() {
        StringBuilder sb = new StringBuilder();
        banqMeasures.forEach((x, y) -> {
            y.forEach(z -> sb.append(String.format("%-40s", x) + z.toString() + "\n"));
        });
        return sb.toString();
    }
}
