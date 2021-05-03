package net.shyshkin.study.webflux.fileuploadexample.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
class UploadControllerTest {

    @Autowired
    WebTestClient webClient;

    @Test
    void uploadSingleFile_fromClassPathResource() throws IOException {

        //given
        String filename = "file1.txt";
        String fileContent = "file1 content";

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder
                .part("fileToUpload", new ClassPathResource("/files/file1.txt"))
                .contentType(MediaType.MULTIPART_FORM_DATA);
        multipartBodyBuilder.part("user-name", "Kate Shyshkina");
        MultiValueMap<String, HttpEntity<?>> multipartData = multipartBodyBuilder.build();

        //when
        webClient.post()
                .uri("/upload/file/single")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartData))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody().isEmpty();

        String content = Files.readString(UploadController.basePath.resolve(filename));
        assertThat(content).isEqualTo(fileContent);

    }

    @Test
    void uploadSingleFile_fromByteArrayResource() throws IOException {

        //given
        String filename = "byteArrayResource.txt";
        String fileContent = "Byte Array Resource content";

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder
                .part("fileToUpload",
                        new ByteArrayResource(fileContent.getBytes(), "byteArray description"),
                        MediaType.MULTIPART_FORM_DATA)
                .filename(filename);
        multipartBodyBuilder.part("user-name", "Kate Shyshkina");
        MultiValueMap<String, HttpEntity<?>> multipartData = multipartBodyBuilder.build();

        //when
        webClient.post()
                .uri("/upload/file/single")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartData))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody().isEmpty();

        String content = Files.readString(UploadController.basePath.resolve(filename));
        assertThat(content).isEqualTo(fileContent);
    }

    @Test
    void uploadSingleFile_fromByteArray() throws IOException {

        //given
        String filename = "byteArray.txt";
        String fileContent = "Byte Array content";

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder
                .part("fileToUpload",
                        fileContent.getBytes(),
                        MediaType.MULTIPART_FORM_DATA)
                .filename(filename);
        multipartBodyBuilder.part("user-name", "Kate Shyshkina");
        MultiValueMap<String, HttpEntity<?>> multipartData = multipartBodyBuilder.build();

        //when
        webClient.post()
                .uri("/upload/file/single")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartData))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody().isEmpty();

        String content = Files.readString(UploadController.basePath.resolve(filename));
        assertThat(content).isEqualTo(fileContent);
    }

    @Test
    void uploadSingleFile_fromString() throws IOException {

        //given
        String filename = "fromString.txt";
        String fileContent = "String content";

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder
                .part("fileToUpload", fileContent)
                .filename(filename);
        multipartBodyBuilder.part("user-name", "Kate Shyshkina");
        MultiValueMap<String, HttpEntity<?>> multipartData = multipartBodyBuilder.build();

        //when
        webClient.post()
                .uri("/upload/file/single")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartData))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody().isEmpty();

        String content = Files.readString(UploadController.basePath.resolve(filename));
        assertThat(content).isEqualTo(fileContent);
    }

    @Test
    void uploadMultipleFiles_fromString() throws IOException {

        //given
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        for (int i = 1; i <= 3; i++) {
            String fileContent = fileContent(i);
            String filename = fileName(i);
            multipartBodyBuilder
                    .part("filesToUpload", fileContent)
                    .filename(filename);
        }
        multipartBodyBuilder.part("user-name", "Kate Shyshkina");
        MultiValueMap<String, HttpEntity<?>> multipartData = multipartBodyBuilder.build();

        //when
        webClient.post()
                .uri("/upload/file/multi")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartData))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody().isEmpty();

        for (int i = 1; i <= 3; i++) {
            String content = Files.readString(UploadController.basePath.resolve(fileName(i)));
            assertThat(content).isEqualTo(fileContent(i));
        }
    }

    private String fileName(int index) {
        return String.format("fromString_%02d.txt", index);
    }

    private String fileContent(int index) {
        return String.format("File content from String %02d", index);
    }

    @Test
    void uploadMultipleFiles_fromClassPathResource() throws IOException {

        //given
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        for (int i = 1; i <= 3; i++) {
            String filename = String.format("file%d.txt", i);
            multipartBodyBuilder
                    .part("filesToUpload", new ClassPathResource("/files/" + filename))
                    .filename(filename);
        }
        multipartBodyBuilder.part("user-name", "Kate Shyshkina");
        MultiValueMap<String, HttpEntity<?>> multipartData = multipartBodyBuilder.build();

        //when
        webClient.post()
                .uri("/upload/file/multi")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartData))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody().isEmpty();

        for (int i = 1; i <= 3; i++) {
            String content = Files.readString(UploadController.basePath.resolve("file" + i + ".txt"));
            assertThat(content).isEqualTo("file" + i + " content");
        }
    }
}