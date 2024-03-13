package com.example.back;

import com.example.back.service.HistoryService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@SpringBootApplication
public class BackApplication {

    private final HistoryService historyService;

    public BackApplication(HistoryService historyService) {
        this.historyService = historyService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

    @Scheduled(cron = "0 00 03 * * ?", zone = "Europe/Bucharest")
    void reservationHistory() {
        System.out.println(historyService.moveOldReservations());
    }

    @Scheduled(cron = "0 00 03 * * ?", zone = "Europe/Bucharest")
    void databaseBackup() {
        System.out.println("Backup Started at " + new Date());

        String fileName = "Daily_DB_Backup"; // default file name
        String folderPath = "/root/backupDB";
        File f1 = new File(folderPath);
        f1.mkdir(); // create folder if not exist

        String saveFileName = fileName + ".sql";
        String savePath = folderPath + File.separator + saveFileName;

        String executeCmd = "mysqldump -uroot -ppassword hotelcalendarapp -r " + savePath;

        System.out.println(executeCmd);

        Process runtimeProcess = null;
        try {
            runtimeProcess = Runtime.getRuntime().exec(executeCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int processComplete = 0;
        try {
            assert runtimeProcess != null;
            processComplete = runtimeProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (processComplete == 0) {
            System.out.println("Backup Complete at " + new Date());
        } else {
            System.out.println("Backup Failure");
        }
    }

}

@Configuration
@EnableScheduling
@ConditionalOnProperty(name="scheduling.enabled", matchIfMissing = true)
class SchedulingConfiguration {

}
