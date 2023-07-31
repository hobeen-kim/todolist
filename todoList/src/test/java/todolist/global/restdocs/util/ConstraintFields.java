package todolist.global.restdocs.util;

import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes;

import java.util.stream.Collectors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ConstraintFields<T> {

    private final ConstraintDescriptions constraintDescriptions;

    public ConstraintFields(Class<T> clazz) {
        this.constraintDescriptions = new ConstraintDescriptions(clazz);
    }

    public FieldDescriptor withPath(String path) {
        return fieldWithPath(path).attributes(
                Attributes.key("constraints").value(
                        this.constraintDescriptions.descriptionsForProperty(path).stream()
                                .map(description -> "- " + description)
                                .collect(Collectors.joining("\n"))
                )
        );
    }

}

