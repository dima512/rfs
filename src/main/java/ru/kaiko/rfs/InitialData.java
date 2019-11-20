package ru.kaiko.rfs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class InitialData {

    @Bean
    public CommandLineRunner commandLineRunner(LanguageRepo repo) {
        return args -> {
            log.info("start load h2...");

            repo.save(new Language().setName("scala").setAuthor("odersky")
                    .setCurrentVersion("2.12.8").setFeature("functional and oop").setYear(2003));

            repo.save(new Language().setName("java").setAuthor("gosling")
                    .setCurrentVersion("12").setFeature("oop").setYear(1995));

            repo.save(new Language().setName("javascript").setAuthor("eich")
                    .setCurrentVersion("ECMAScript 2018").setFeature("frontend").setYear(1995));

            repo.save(new Language().setName("elixir").setAuthor("valim")
                    .setCurrentVersion("1.8").setFeature("functional and actor model").setYear(2012));

            repo.save(new Language().setName("clojure").setAuthor("hitch")
                    .setCurrentVersion("1.10.0").setFeature("lisp and functional").setYear(2007));

            repo.save(new Language().setName("python").setAuthor("guido")
                    .setCurrentVersion("3.7.2").setFeature("dynamic and oop").setYear(1991));

            repo.save(new Language().setName("haskell").setAuthor("augustsson")
                    .setCurrentVersion("8.4.3").setFeature("functional and lazy").setYear(1990));

            log.info("finish load h2...");
        };
    }
}
