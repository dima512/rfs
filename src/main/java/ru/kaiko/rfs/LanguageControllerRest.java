package ru.kaiko.rfs;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class LanguageControllerRest {

    private final LanguageRepo repo;

    @Autowired
    public LanguageControllerRest(LanguageRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    @JsonView(View.Short.class)
    public Iterable<Language> getAllShort() {
        return repo.findAll();
    }

    @GetMapping("full")
    @JsonView(View.Full.class)
    public Iterable<Language> getAllFull() {
        return repo.findAll();
    }

    @GetMapping("pagination")
    public Iterable<Language> pagination(
            @RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = true, defaultValue = "2") Integer size) {
        return repo.findAll(PageRequest.of(page, size)).getContent();
    }

    @GetMapping("year/{year}")
    public Iterable<Language> getAllByYear(@PathVariable("year") Integer year) {
        return repo.findAllByYear(year);
    }

    @GetMapping("{id}")
    public ResponseEntity<Language> getById(@PathVariable("id") Long id) {
        Optional<Language> language = repo.findById(id);
        if (language.isPresent()) {
            return new ResponseEntity<>(language.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Language> create(@RequestBody Language language) {
        language.setId(null); // if we don't do it we replace row in database
        return new ResponseEntity<>(repo.save(language), HttpStatus.CREATED);
    }

    @PutMapping(consumes = "application/json")
    public Language put(@RequestBody Language language) {
        return repo.save(language);
    }

    @PatchMapping(consumes = "application/json")
    public ResponseEntity<Language> patch(@RequestBody Language language) {
        Optional<Language> languageDBOptional = repo.findById(language.getId());
        if (languageDBOptional.isPresent()) {
            Language res = changeProperties(languageDBOptional.get(), language);
            repo.save(res);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    public static Language changeProperties(Language languageDB, Language languageInput) {
        if (languageInput.getAuthor() != null) languageDB.setAuthor(languageInput.getAuthor());
        if (languageInput.getName() != null) languageDB.setName(languageInput.getName());
        if (languageInput.getFeature() != null) languageDB.setFeature(languageInput.getFeature());
        if (languageInput.getCurrentVersion() != null) languageDB.setCurrentVersion(languageInput.getCurrentVersion());
        if (languageInput.getYear() != null) languageDB.setYear(languageInput.getYear());
        return languageDB;
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        Optional<Language> language = repo.findById(id);
        if (language.isPresent()) {
            repo.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
