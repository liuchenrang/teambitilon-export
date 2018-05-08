package app.repository;

import app.po.StageGroup;
import app.po.StageTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface StageTaskRepository extends MongoRepository<StageTask, String> {
    @Override
    Optional<StageTask> findById(String s);
}
