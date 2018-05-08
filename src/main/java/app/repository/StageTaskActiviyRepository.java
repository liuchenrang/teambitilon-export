package app.repository;

import app.po.StageTask;
import app.po.StageTaskActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface StageTaskActiviyRepository extends MongoRepository<StageTaskActivity, String> {
    @Override
    Optional<StageTaskActivity> findById(String s);
}
