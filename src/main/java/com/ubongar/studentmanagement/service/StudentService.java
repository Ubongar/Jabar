package com.ubongar.studentmanagement.service;

import com.ubongar.studentmanagement.exception.DuplicateEmailException;
import com.ubongar.studentmanagement.exception.ResourceNotFoundException;
import com.ubongar.studentmanagement.model.Student;
import com.ubongar.studentmanagement.repository.StudentRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final Validator validator;

    public StudentService(StudentRepository studentRepository, Validator validator) {
        this.studentRepository = studentRepository;
        this.validator = validator;
    }

    public Student addStudent(Student student) {
        validateStudent(student);

        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateEmailException(student.getEmail());
        }

        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        validateStudent(updatedStudent);

        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        String newEmail = updatedStudent.getEmail();
        if (!existingStudent.getEmail().equals(newEmail) && studentRepository.existsByEmail(newEmail)) {
            throw new DuplicateEmailException(newEmail);
        }

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setEmail(newEmail);
        existingStudent.setAge(updatedStudent.getAge());

        return studentRepository.save(existingStudent);
    }

    public void deleteStudent(Long id) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        studentRepository.deleteById(existingStudent.getId());
    }

    private void validateStudent(Student student) {
        Set<ConstraintViolation<Student>> violations = validator.validate(student);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(message);
        }
    }
}
