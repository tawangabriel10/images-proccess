package br.com.bix.images.data.repository;

import br.com.bix.images.data.model.ImageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends MongoRepository<ImageDocument, String> {

}
