package co.com.srdejo.trazabilidad.infrastructure.out.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "objectTable")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ObjectEntity {
    @Id
    private Long id;

    private String name;
}
