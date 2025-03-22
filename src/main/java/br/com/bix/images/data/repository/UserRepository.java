package br.com.bix.images.data.repository;

import br.com.bix.images.data.model.UserDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserDocument, String> {

    @Query(value = "{ 'email': ?0 }")
    Optional<UserDocument> findByEmail(String email);
}
