package app.repository;

import app.po.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public interface ProjectRepository extends MongoRepository<Project, String> {
    Project findBySourceId(String source);
}
