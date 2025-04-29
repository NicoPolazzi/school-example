package com.examples.school.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.examples.school.model.Student;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Testcontainers
class StudentMongoRepositoryTestcontainersIT {

    @Container
    static final MongoDBContainer mongo = new MongoDBContainer("mongo:5");
    private MongoClient client;
    private MongoCollection<Document> studentCollection;
    private StudentMongoRepository studentRepository;

    @BeforeEach
    void setup() {
	client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getMappedPort(27017)));
	studentRepository = new StudentMongoRepository(client);
	MongoDatabase database = client.getDatabase(StudentMongoRepository.SCHOOL_DB_NAME);
	database.drop();
	studentCollection = database.getCollection(StudentMongoRepository.SCHOOL_COLLECTION_NAME);
    }

    @AfterEach
    void tearDown() {
	client.close();
    }

    @Test
    void testFindAll() {
	addTestStudentToDatabase("1", "test1");
	addTestStudentToDatabase("2", "test2");
	assertThat(studentRepository.findAll()).containsExactly(new Student("1", "test1"),
		new Student("2", "test2"));
    }

    @Test
    void testFindById() {
	addTestStudentToDatabase("1", "test1");
	addTestStudentToDatabase("2", "test2");
	assertThat(studentRepository.findById("2")).isEqualTo(new Student("2", "test2"));
    }

    private void addTestStudentToDatabase(String id, String name) {
	studentCollection.insertOne(new Document().append("id", id).append("name", name));
    }

}
