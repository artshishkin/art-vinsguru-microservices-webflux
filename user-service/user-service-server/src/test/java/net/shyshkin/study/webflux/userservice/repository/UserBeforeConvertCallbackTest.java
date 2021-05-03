package net.shyshkin.study.webflux.userservice.repository;

import com.github.javafaker.Faker;
import net.shyshkin.study.webflux.userservice.entity.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class UserBeforeConvertCallbackTest {

    @Autowired
    UserRepository userRepository;

    private static Stream<Arguments> onBeforeConvert() {
        return Stream.of(
                Arguments.of("Art Shyshkin", "Art Shyshkin"),
                Arguments.of("Mr.Shyshkin", "Mr.Shyshkin"),
                Arguments.of("Mrs. Kate *&^Shys###hkina", "Mrs. Kate Shyshkina"),
                Arguments.of("!`M`s. Ar\\i/|n****a *&^Shys_hkina", "Ms. Arina Shyshkina"),
                Arguments.of("D'Nazar Shyshkin", "D'Nazar Shyshkin")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @MethodSource
    void onBeforeConvert(String originalUserName, String expectedUserName) {
        //given
        User user = User.builder().name(originalUserName).balance(Faker.instance().random().nextInt(1000)).build();

        //when
        Mono<User> mono = userRepository.save(user);

        //then
        StepVerifier.create(mono)
                .assertNext(savedUser -> assertThat(savedUser.getName()).isEqualTo(expectedUserName))
                .verifyComplete();
    }
}