package net.shyshkin.study.webflux.webfluxdemo.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SleepUtil {
    public static void sleep(double seconds) {

        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            log.error("", e);
        }

    }
}
