package app.repository;

import app.po.Project;
import app.po.StageGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface StageGroupRepository extends MongoRepository<StageGroup, String> {
    @Override
    Optional<StageGroup> findById(String s);
}
