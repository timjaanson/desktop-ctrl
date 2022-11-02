package com.timjaanson.desktopctrl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class ShellExecutor {
    static final Logger log = LoggerFactory.getLogger(ShellExecutor.class);

    public String exec(String[] cmd) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(cmd);
        Process process = builder.start();

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }
        reader.close();
        int exitCode = process.waitFor();
        log.debug("command:{}\nexit code:{}\n{}", cmd, exitCode, output);
        if (exitCode != 0) {
            String message = "exit code "+exitCode+" for command '"+String.join(" ", cmd)+"' -"+output;
            throw new IllegalArgumentException(message);
        }

        return output.toString();
    }
}
