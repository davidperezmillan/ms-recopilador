package com.davidperezmillan.recopilador.infrastructure.health.services;

import com.davidperezmillan.recopilador.infrastructure.health.models.HealthStatus;
import com.davidperezmillan.recopilador.infrastructure.health.models.EventsFile;
import com.davidperezmillan.recopilador.infrastructure.health.models.StatusHealthyEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.io.File;

@Service
public class HealthHostService {

    @Value("${app.health.directory}")
    private String healthDirectory;

    private static final Pattern DIRECTORY_PATTERN = Pattern.compile("(\\d+)Y(\\d+)M(\\d+)D(\\d+)H");

    public HealthStatus checkWebPageHealth(String url) {
        try {
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            connection.connect();
            int code = connection.getResponseCode();

            // Considerar tanto 200 como 401 como estados saludables
            StatusHealthyEnum isHealthy = (code == 200 || code == 401) ? StatusHealthyEnum.HEALTHY : StatusHealthyEnum.UNHEALTHY;
            String details = "HTTP status: " + code;

            if (code == 401) {
                details += " (Camera is responding but requires authentication)";
            }

            return HealthStatus.builder().healthy(isHealthy).detailsWebsite(details).build();
        } catch (Exception e) {
            return HealthStatus.builder().healthy(StatusHealthyEnum.UNHEALTHY).detailsWebsite("Error: " + e.getMessage()).build();
        }
    }

    public HealthStatus checkFileSystemHealth(String camaraPath) {
        String fullPath = buildFullPath(camaraPath);
        File directory = new File(fullPath);

        if (!directoryExists(directory)) {
            return buildUnhealthyStatus(fullPath, "No existe el directorio");
        }

        File[] directories = getSubdirectories(directory);
        if (directories == null) {
            return buildUnhealthyStatus(fullPath, "No se pudo leer el directorio");
        }

        String[] directoryNames = extractDirectoryNames(directories);
        String[] sortedNames = sortDirectoriesByDateTime(directoryNames);

        return buildHealthyStatus(fullPath, sortedNames);
    }

    private String buildFullPath(String camaraPath) {
        return healthDirectory + "/" + camaraPath;
    }

    private boolean directoryExists(File directory) {
        return directory.exists();
    }

    private File[] getSubdirectories(File directory) {
        try {
            return directory.listFiles(File::isDirectory);
        } catch (Exception e) {
            return null;
        }
    }

    private String[] extractDirectoryNames(File[] directories) {
        String[] directoryNames = new String[directories.length];
        for (int i = 0; i < directories.length; i++) {
            directoryNames[i] = directories[i].getName();
        }
        return directoryNames;
    }

    private String[] sortDirectoriesByDateTime(String[] directoryNames) {
        Arrays.sort(directoryNames, this::compareDirectoryNames);
        return directoryNames;
    }

    private int compareDirectoryNames(String a, String b) {
        Matcher matcherA = DIRECTORY_PATTERN.matcher(a);
        Matcher matcherB = DIRECTORY_PATTERN.matcher(b);

        if (matcherA.matches() && matcherB.matches()) {
            LocalDateTime dateA = parseDirectoryDateTime(matcherA);
            LocalDateTime dateB = parseDirectoryDateTime(matcherB);
            return dateB.compareTo(dateA); // Orden descendente: más reciente primero
        }

        if (matcherA.matches()) return -1;
        if (matcherB.matches()) return 1;
        return a.compareTo(b);
    }

    private LocalDateTime parseDirectoryDateTime(Matcher matcher) {
        int year = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int day = Integer.parseInt(matcher.group(3));
        int hour = Integer.parseInt(matcher.group(4));
        return LocalDateTime.of(year, month, day, hour, 0);
    }

    private HealthStatus buildUnhealthyStatus(String path, String errorMessage) {
        EventsFile eventsFile = EventsFile.builder()
                .path(path)
                .status(false)
                .EventDir(new String[]{errorMessage})
                .build();
        return HealthStatus.builder()
                .healthy(StatusHealthyEnum.UNHEALTHY)
                .eventsFile(eventsFile)
                .build();
    }

    private HealthStatus buildHealthyStatus(String path, String[] directoryNames) {
        EventsFile eventsFile = EventsFile.builder()
                .path(path)
                .status(true)
                .EventDir(directoryNames)
                .build();
        return HealthStatus.builder()
                .healthy(StatusHealthyEnum.HEALTHY)
                .eventsFile(eventsFile)
                .build();
    }
}
