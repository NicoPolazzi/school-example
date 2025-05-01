package com.examples.school.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.examples.school.view.StudentView;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class SchoolControllerRaceConditionTest {

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
    void testNewStudentConcurrent() {
	List<Student> students = new ArrayList<>();
	Student student = new Student("1", "name");
	// stub the StudentRepository
	when(studentRepository.findById(anyString()))
		.thenAnswer(invocation -> students.stream().findFirst().orElse(null));
	doAnswer(invocation -> {
	    students.add(student);
	    return null;
	}).when(studentRepository).save(any(Student.class));
	// start the threads calling newStudent concurrently
	List<Thread> threads = IntStream.range(0, 10)
		.mapToObj(i -> new Thread(() -> schoolController.newStudent(student))).peek(t -> t.start())
		.collect(Collectors.toList());
	// wait all threads to finish
	await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
	assertThat(students).containsExactly(student);
    }

    @Test
    void testDeleteStudentConcurrent() {
	Student student1 = new Student("1", "test1");
	Student student2 = new Student("2", "test2");
	List<Student> students = new ArrayList<>(List.of(student1, student2));
	when(studentRepository.findById(anyString()))
		.thenAnswer(invocation -> students.stream().filter(s -> s.equals(student1)).findFirst().orElse(null));
	doAnswer(invocation -> {
	    students.remove(student1);
	    return null;
	}).when(studentRepository).delete(anyString());
	List<Thread> threads = IntStream.range(0, 10)
		.mapToObj(i -> new Thread(() -> schoolController.deleteStudent(student1))).peek(t -> t.start())
		.collect(Collectors.toList());
	// wait all threads to finish
	await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
	assertThat(students).containsExactly(student2);
    }

}
