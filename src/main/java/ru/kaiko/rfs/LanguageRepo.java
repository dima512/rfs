package ru.kaiko.rfs;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepo extends JpaRepository<Language, Long> {

    Iterable<Language> findAllByYear(Integer year);
}
