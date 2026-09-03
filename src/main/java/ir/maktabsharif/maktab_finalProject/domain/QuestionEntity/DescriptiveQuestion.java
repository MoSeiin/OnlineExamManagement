package ir.maktabsharif.maktab_finalProject.domain.QuestionEntity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DESCRIPTIVE")
public class DescriptiveQuestion extends Question{
}
