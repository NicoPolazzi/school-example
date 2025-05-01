package com.examples.school.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

class StudentMongoRepositoryTest {
    private static final String SCHOOL_DB_NAME = "school";
    private static final String SCHOOL_COLLECTION_NAME = "student";

    private static MongoServer server;
    private static InetSocketAddress serverAddress;
    private MongoClient client;
    private StudentRepository studentRepository;
    private MongoCollection<Document> studentCollection;

    @BeforeAll
    static void setUpServer() {
	server = new MongoServer(new MemoryBackend());
	serverAddress = server.bind();
    }

    @BeforeEach
    void setup() {
	client = new MongoClient(new ServerAddress(serverAddress));
	studentRepository = new StudentMongoRepository(client, SCHOOL_DB_NAME, SCHOOL_COLLECTION_NAME);
	MongoDatabase database = client.getDatabase(SCHOOL_DB_NAME);
	database.drop();
	studentCollection = database.getCollection(SCHOOL_COLLECTION_NAME);
    }

    @AfterEach
    void tearDown() {
	client.close();
    }

    @Test
    void testFindAllWhenDatabaseIsEmpty() {
	assertThat(studentRepository.findAll()).isEmpty();
    }

    @Test
    void testFindAllWhenDatabaseIsNotEmpty() {
	addTestStudentToDatabase("1", "test1");
	addTestStudentToDatabase("2", "test2");
	assertThat(studentRepository.findAll()).containsExactly(new Student("1", "test1"), new Student("2", "test2"));

    }

    @Test
    void testFindByIdNotFound() {
	assertThat(studentRepository.findById("1")).isNull();
    }

    @Test
    void testFindByIdFound() {
	addTestStudentToDatabase("1", "test1");
	addTestStudentToDatabase("2", "test2");
	assertThat(studentRepository.findById("2")).isEqualTo(new Student("2", "test2"));
    }

    private void addTestStudentToDatabase(String id, String name) {
	studentCollection.insertOne(new Document().append("id", id).append("name", name));
    }
}
