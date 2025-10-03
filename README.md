# spring-banq
Banq is a lightweight runtime performance measurement tool based on Spring AOP (Aspect-Oriented Programming).
It allows you to measure execution times of methods in Spring-managed beans simply by annotating them with @Banq.

# Note
⚠️ Important: Banq only works on methods called through Spring’s proxy mechanism. 
That means the method must belong to a Spring-managed bean. Direct self-calls inside the same class will not be intercepted.

# Build
Java 19+ required.
<code>mvn clean install package</code>

# Setup
### 1. Add to Project
Add the two classes to your project source folder: /src/main/de/teatime08
### 2. Add the Annotation
Mark any method in a Spring @Component, @Service, or @Controller class with the @Banq annotation:
```
import de.teatime08.Banq;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {

    @Banq
    public String doWork() {
        // some business logic
        return "done!";
    }
}
```

### 3. Spring Context Scan
The performance measuring logic is already provided by the BanquetteAspect class.
Simply make sure it is part of your Spring application context:
```
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.teatime08")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
The @Aspect and @Component annotations on BanquetteAspect will ensure it is picked up automatically.

# Measure Results
### 1. Collect Measurements
Whenever a @Banq method is executed, Banq records:
- Count: The running number of the measurement.
- Tick Date: The timestamp when the execution finished.
- Execution Time: Duration in nanoseconds.

All measures are stored in memory inside a Map<String, LinkedList<BanqTick>>, keyed by the method signature.
You can access them programmatically:
```
@Autowired
private BanquetteAspect banquetteAspect;

public void printMeasures() {
    System.out.println(banquetteAspect.printToString());
}
```
### 2. Statistical Outputs
Banq isn’t just logging times. It provides simple statistical helpers:
- Median:
```
double median = banquetteAspect.median("String de.teatime08.ExampleService.doWork()");
```
- Percentiles (e.g., 90th):
```
double p90 = banquetteAspect.percentile("String de.teatime08.ExampleService.doWork()", 90);
```
BoxPlot summary (min, max, median, Q1, Q3):
```
BanquetteAspect.BoxPlot stats = banquetteAspect.boxPlot("String de.teatime08.ExampleService.doWork()");
System.out.println(stats);
```
### 3. Example Output
If you’ve run your @Banq-annotated method several times, calling printToString() might give you something like:
```
String de.teatime08.ExampleService.doWork() BanqTick{count=0, tickDate=2025-10-03T12:01:05, benchNanoTime=105321}
String de.teatime08.ExampleService.doWork() BanqTick{count=1, tickDate=2025-10-03T12:01:07, benchNanoTime=100842}
String de.teatime08.ExampleService.doWork() BanqTick{count=2, tickDate=2025-10-03T12:01:09, benchNanoTime=99054}
```
This makes it easy to plug into logging, monitoring dashboards, or just use for ad-hoc performance checks during development.

# Note 2
- Works only with methods in Spring-managed beans (@Component, @Service, @Controller, etc.).
- Results are stored in memory — no persistence layer is included.
- The statistics are simple by design (median, percentiles, boxplot). If you need advanced metrics or visualization, export them to a monitoring system.

Overall this is a small tool to annotate, run, measure, analyze the performance of smaller codeblocks in your Spring application.
