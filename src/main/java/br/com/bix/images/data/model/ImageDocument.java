package br.com.bix.images.data.model;

import java.time.LocalDateTime;
import javax.persistence.GeneratedValue;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "images")
public class ImageDocument {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid4")
    private ObjectId id;
    private String name;
    private String type;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private ObjectId userId;
    private String path;
}
