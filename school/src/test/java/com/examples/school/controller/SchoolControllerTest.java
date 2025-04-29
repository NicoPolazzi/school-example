package com.examples.school.controller;

import static java.util.Arrays.asList;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.examples.school.view.StudentView;

import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class SchoolControllerTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentView studentView;

    @InjectMocks
    private SchoolController schoolController;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
	closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void releaseMocks() throws Exception {
	closeable.close();
    }

    @Test
    void testAllStudents() {
	List<Student> students = asList(new Student());
	when(studentRepository.findAll()).thenReturn(students);
	schoolController.allStudents();
	verify(studentView).showAllStudents(students);
    }

    @Test
    void testNewStudentWhenStudentDoesNotAlreadyExist() {
	Student student = new Student("1", "test");
	when(studentRepository.findById("1")).thenReturn(null);
	schoolController.newStudent(student);
	InOrder inOrder = inOrder(studentRepository, studentView);
	inOrder.verify(studentRepository).save(student);
	inOrder.verify(studentView).studentAdded(student);
    }

    @Test
    void testNewStudentWhenStudentAlreadyExists() {
	Student studentToAdd = new Student("1", "test");
	Student existingStudent = new Student("1", "name");
	when(studentRepository.findById("1")).thenReturn(existingStudent);
	schoolController.newStudent(studentToAdd);
	verify(studentView).showError("Already existing student with id 1", existingStudent);
	verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }

    @Test
    void testDeleteStudentWhenStudentExists() {
	Student student = new Student("1", "test");
	when(studentRepository.findById("1")).thenReturn(student);
	schoolController.deleteStudent(student);
	InOrder inOrder = inOrder(studentRepository, studentView);
	inOrder.verify(studentRepository).delete(student.getId());
	inOrder.verify(studentView).studentRemoved(student);
    }

    @Test
    void testDeleteStudentWhenStudentDoesNotExist() {
	Student student = new Student("1", "test");
	when(studentRepository.findById("1")).thenReturn(null);
	schoolController.deleteStudent(student);
	verify(studentView).showError("No existing student with id 1", student);
	verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }
}
