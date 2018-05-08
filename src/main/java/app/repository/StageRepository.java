package app.repository;

import app.po.Project;
import app.po.Stage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface StageRepository extends MongoRepository<Stage, String> {
    Optional<Stage> findById(String source);
}
