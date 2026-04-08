package com.ubongar.studentmanagement.service;

import com.ubongar.studentmanagement.exception.DuplicateEmailException;
import com.ubongar.studentmanagement.exception.ResourceNotFoundException;
import com.ubongar.studentmanagement.model.Student;
import com.ubongar.studentmanagement.repository.StudentRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateEmailException(student.getEmail());
        }
        if (student.getAge() <= 15) {
            throw new IllegalArgumentException("Age must be greater than 15");
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
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        String newEmail = updatedStudent.getEmail();
        if (!existingStudent.getEmail().equals(newEmail) && studentRepository.existsByEmail(newEmail)) {
            throw new DuplicateEmailException(newEmail);
        }

        if (updatedStudent.getAge() <= 15) {
            throw new IllegalArgumentException("Age must be greater than 15");
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
}
