package com.timjaanson.desktopctrl.volume.service;

import com.timjaanson.desktopctrl.service.ShellExecutor;
import com.timjaanson.desktopctrl.volume.entity.VolumeRequest;
import com.timjaanson.desktopctrl.volume.entity.VolumeResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VolumeService {
    private static final Logger log = LoggerFactory.getLogger(VolumeService.class);

    private static final Integer VOLUME_CHANGE_STEP_SIZE = 5;
    private static final Long SLEEP_DURATION_BETWEEN_VOLUME_STEPS = 60L;

    private final ShellExecutor shell;

    @PostConstruct
    private void checkOS() {
        String os = System.getProperty("os.name");
        if (!"linux".equalsIgnoreCase(os)) {
            throw new IllegalArgumentException("Unsupported OS: "+os);
        }
        log.info("Running on OS:{}", os);
    }

    public void getSoundCards() throws Exception {
        List<String> soundCards = Arrays.stream(shell.exec(getSoundCardsCommand()).split("\n")).toList();

    }

    public VolumeRequest getVolume() throws Exception {
        try {
            String volumeLevel = shell.exec(getVolumeCommand());
            volumeLevel = volumeLevel.replace("\n", "").replace("\r", "");
            return VolumeRequest.builder()
                    .volumeLevel(Integer.parseInt(volumeLevel))
                    .muted(false) //TODO get actual muted state
                    .build();
        } catch (Exception e) {
            log.error("Exception getting volume level", e);
            throw e;
        }
    }

    public VolumeResponse setVolume(VolumeRequest req) {
        VolumeResponse res = new VolumeResponse();
        try {

            //TODO mute/unmute

            if (req.getVolumeLevel() != null) {
                int currentVolume = getVolume().getVolumeLevel();
                int volumeChange = req.getVolumeLevel() - currentVolume;
                int absVolumeChange = Math.abs(volumeChange);
                //TODO check incrementing logic
                incrementallyChangeVolume(absVolumeChange, volumeChange > 0, req.getVolumeLevel());

                Thread.sleep(SLEEP_DURATION_BETWEEN_VOLUME_STEPS);
                int newVolume = getVolume().getVolumeLevel();
                res.setSuccess(true);
                res.setMuted(false); //TODO mute state
                res.setVolumeLevel(newVolume);
            }

        } catch (Exception e) {
            log.error("Failed to set volume", e);
            res.setSuccess(false);
            res.setErrorMessage(e.getMessage());
            return res;
        }
        return res;
    }

    private void incrementallyChangeVolume(int amount, boolean incrementUp, int finalVolumeValue) throws IOException, InterruptedException {
        int steps = amount/VOLUME_CHANGE_STEP_SIZE;
        int remaining = amount % VOLUME_CHANGE_STEP_SIZE;
        for (int i = 0; i < steps; i++) {
            shell.exec(setVolumeIncrementallyCommand(VOLUME_CHANGE_STEP_SIZE, incrementUp));
            Thread.sleep(SLEEP_DURATION_BETWEEN_VOLUME_STEPS);
        }
        if (remaining != 0) {
            String[] cmd = setVolumeCommand(finalVolumeValue);
            shell.exec(cmd);
            //For some reason volume doesn't get set correctly the first time if setting final value, so send it again
            Thread.sleep(SLEEP_DURATION_BETWEEN_VOLUME_STEPS);
            shell.exec(cmd);
        }
    }


    private String[] getSoundCardsCommand() {
        String command = "cat /proc/asound/cards | grep -e '[0-9].*\\[.*\\]' | cut -d \":\" -f2 | awk '{$1=$1;print}'";
        return new String[]{"/bin/bash", "-c", command};
    }

    private String[] getVolumeCommand() {
        int channel = 1;
        String command = "awk -F'[][]' '/dB/ { print $2 }' <(amixer -M -c "+channel+" get Master) | sed 's/.$//' | tr -d '\\n'";
        return new String[]{"/bin/bash", "-c", command};
    }

    private String[] setVolumeCommand(int value) {
        int channel = 1;
        String command = "amixer -M -c "+channel+" set Master "+value+"%";
        return new String[]{"/bin/bash", "-c", command};
    }

    private String[] setVolumeIncrementallyCommand(int value, boolean incrementUp) {
        int channel = 1;
        String command = "amixer -M -c "+channel+" set Master "+value+"%"+(incrementUp ? "+" : "-");
        return new String[]{"/bin/bash", "-c", command};
    }
}
