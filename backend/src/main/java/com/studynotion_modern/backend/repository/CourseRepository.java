package com.studynotion_modern.backend.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.Course;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends MongoRepository<Course, ObjectId> {
    List<Course> findByInstructorId(ObjectId instrId);

}
