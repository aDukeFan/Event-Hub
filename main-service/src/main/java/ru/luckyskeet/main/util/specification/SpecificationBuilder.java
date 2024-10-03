package ru.luckyskeet.main.util.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.luckyskeet.main.model.Event;

public class SpecificationBuilder {

    private Specification<Event> specification;

    public SpecificationBuilder() {
        this.specification = Specification.where(null);
    }

    public SpecificationBuilder withSpecification(Specification<Event> newSpecification) {
        if (newSpecification != null) {
            this.specification = this.specification.and(newSpecification);
        }
        return this;
    }

    public Specification<Event> build() {
        return this.specification;
    }
}
