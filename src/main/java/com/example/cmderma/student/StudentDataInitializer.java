package com.example.cmderma.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class StudentDataInitializer {

    private static final int TARGET_STUDENT_COUNT = 100;
    /**
     * Texto longo para forcar ocupacao total do campo de observacoes (TEXT no MySQL ~65k).
     * 500 caracteres sao suficientes para testar validacao e comportamento do front-end.
     */
    private static final int OBS_FIELD_LENGTH = 500;

    private static final String[] FIRST_NAMES = {
            "Alice", "Bruno", "Carla", "Daniel", "Eva", "Fernando", "Gabriela", "Hugo", "Ines", "Joao",
            "Lara", "Miguel", "Nadia", "Oscar", "Patricia", "Quintino", "Rita", "Sofia", "Tiago", "Vera"
    };

    private static final String[] LAST_NAMES = {
            "Almeida", "Barros", "Costa", "Duarte", "Esteves", "Ferreira", "Gomes", "Henriques", "Igrejas", "Jardim",
            "Leite", "Marques", "Neves", "Oliveira", "Pereira", "Queiros", "Ramos", "Silva", "Teixeira", "Vieira"
    };

    private static final String[] PROFESSIONS = {
            "Advogado", "Professor", "Enfermeira", "Engenheiro", "Designer", "Carpinteiro", "Consultor", "Estudante",
            "Investigador", "Gestor", "Farmaceutico", "Programador", "Psicologa", "Arquiteto", "Contabilista"
    };

    @Bean
    CommandLineRunner seedStudents(StudentRepository repository) {
        return args -> {
            if (repository.count() >= TARGET_STUDENT_COUNT) {
                return;
            }

            List<Student> batch = new ArrayList<>(TARGET_STUDENT_COUNT);
            for (int i = 1; i <= TARGET_STUDENT_COUNT; i++) {
                batch.add(buildStudent(i));
            }
            repository.saveAll(batch);
        };
    }

    private Student buildStudent(int index) {
        Student student = new Student();
        student.setNome(generateName(index));
        student.setEntidade(String.format("Entidade %02d", (index % 15) + 1));
        student.setMorada(String.format("Rua %s %d", LAST_NAMES[index % LAST_NAMES.length], 100 + index));
        student.setTelefone(String.format("21%07d", index));
        student.setNbeneficia(String.format("BEN%04d", index));
        student.setSexo(index % 2 == 0 ? Student.Sexo.MASCULINO : Student.Sexo.FEMININO);

        LocalDate birthDate = LocalDate.of(1980 + (index % 25), ((index - 1) % 12) + 1, ((index - 1) % 28) + 1);
        student.setDatanascim(birthDate);

        student.setBinum(String.format("ID%06d", 100000 + index));
        student.setBiemissao(String.format("Municipio %02d", (index % 18) + 1));
        student.setBidata(birthDate.plusYears(18));
        student.setProfissao(PROFESSIONS[index % PROFESSIONS.length]);
        student.setEstadocivi(index % 3 == 0 ? Student.EstadoCivil.CASADO : Student.EstadoCivil.SOLTEIRO);
        student.setTelemovel(String.format("9%08d", 10000000 + index));
        student.setObs(generateObservation(index));
        return student;
    }

    private String generateName(int index) {
        String first = FIRST_NAMES[index % FIRST_NAMES.length];
        String last = LAST_NAMES[(index / FIRST_NAMES.length) % LAST_NAMES.length];
        return first + " " + last + " " + index;
    }

    private String generateObservation(int index) {
        String base = "Observacao detalhada do paciente " + index +
                ": estado clinico, tratamentos anteriores, alergias, notas da ultima consulta. ";
        StringBuilder builder = new StringBuilder(OBS_FIELD_LENGTH);
        while (builder.length() < OBS_FIELD_LENGTH) {
            builder.append(base);
        }
        return builder.substring(0, OBS_FIELD_LENGTH);
    }
}
