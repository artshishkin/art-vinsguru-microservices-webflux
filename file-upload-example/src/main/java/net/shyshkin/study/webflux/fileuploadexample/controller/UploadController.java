package net.shyshkin.study.webflux.fileuploadexample.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("upload")
public class UploadController {

    public static final Path basePath = Paths.get("target/upload/").toAbsolutePath();

    @PostConstruct
    private void init() {
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            log.error("Error in directories creation", e);
        }
    }


    @PostMapping("file/single")
    public Mono<Void> uploadSingleFile(@RequestPart("user-name") String name,
                                       @RequestPart("fileToUpload") Mono<FilePart> filePartMono) {

        log.debug("user: {} tries to upload to {}", name, basePath);
        return filePartMono
                .doOnNext(filePart -> log.debug("Received file: {}", filePart.filename()))
                .flatMap(filePart -> filePart.transferTo(basePath.resolve(filePart.filename())));
    }

    @PostMapping("file/multi")
    public Mono<Void> uploadMultipleFiles(@RequestPart("filesToUpload") Flux<FilePart> filePartFlux) {
        return filePartFlux
                .doOnNext(filePart -> log.debug("Received file: {}", filePart.filename()))
                .flatMap(filePart -> filePart.transferTo(basePath.resolve(filePart.filename())))
                .then();
    }
}
