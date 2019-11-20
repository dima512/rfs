package ru.kaiko.rfs;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "language")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.Full.class)
    private Long id;

    @JsonView(View.Short.class)
    @NotNull
    private String name;

    @JsonView(View.Short.class)
    @NotNull
    private String author;

    @JsonView(View.Full.class)
    @NotNull
    private Integer year;

    @JsonView(View.Full.class)
    @NotNull
    private String feature;

    @JsonView(View.Full.class)
    @NotNull
    private String currentVersion;
}
