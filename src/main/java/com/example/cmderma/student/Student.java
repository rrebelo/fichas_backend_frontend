package com.example.cmderma.student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "morada", length = 50)
    private String morada;

    @Column(name = "telefone", length = 50)
    private String telefone;

    @Column(name = "entidade", length = 50)
    private String entidade;

    @Column(name = "nbeneficia", length = 50)
    private String nbeneficia;

    @Column(name = "sexo", length = 15)
    @Enumerated(STRING)
    private Sexo sexo;

    @Column(name = "data_nascim")
    private LocalDate datanascim;

    @Column(name = "binum", length = 50)
    private String binum;

    @Column(name = "biemissao", length = 50)
    private String biemissao;

    @Column(name = "bidata")
    private LocalDate bidata;

    @Column(name = "profissao", length = 50)
    private String profissao;

    @Column(name = "estadocivi", length = 15)
    @Enumerated(STRING)
    private EstadoCivil estadocivi;

    @Column(name = "telemovel", length = 50)
    private String telemovel;

    @Column(name = "obs", columnDefinition = "TEXT")
    private String obs;

    public Student(String nome,
                   String morada,
                   String telefone,
                   String entidade,
                   String nbeneficia,
                   Sexo sexo,
                   LocalDate datanascim,
                   String binum,
                   String biemissao,
                   LocalDate bidata,
                   String profissao,
                   EstadoCivil estadocivi,
                   String telemovel,
                   String obs) {
        this.nome = nome;
        this.morada = morada;
        this.telefone = telefone;
        this.entidade = entidade;
        this.nbeneficia = nbeneficia;
        this.sexo = sexo;
        this.datanascim = datanascim;
        this.binum = binum;
        this.biemissao = biemissao;
        this.bidata = bidata;
        this.profissao = profissao;
        this.estadocivi = estadocivi;
        this.telemovel = telemovel;
        this.obs = obs;
    }

    public enum Sexo {
        MASCULINO, FEMININO
    }

    public enum EstadoCivil {
        CASADO, SOLTEIRO
    }
}
