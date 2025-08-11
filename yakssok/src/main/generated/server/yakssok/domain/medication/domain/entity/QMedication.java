package server.yakssok.domain.medication.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMedication is a Querydsl query type for Medication
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMedication extends EntityPathBase<Medication> {

    private static final long serialVersionUID = 1960650239L;

    public static final QMedication medication = new QMedication("medication");

    public final DateTimePath<java.time.LocalDateTime> endDateTime = createDateTime("endDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> intakeCount = createNumber("intakeCount", Integer.class);

    public final ListPath<MedicationIntakeDay, QMedicationIntakeDay> intakeDays = this.<MedicationIntakeDay, QMedicationIntakeDay>createList("intakeDays", MedicationIntakeDay.class, QMedicationIntakeDay.class, PathInits.DIRECT2);

    public final ListPath<MedicationIntakeTime, QMedicationIntakeTime> intakeTimes = this.<MedicationIntakeTime, QMedicationIntakeTime>createList("intakeTimes", MedicationIntakeTime.class, QMedicationIntakeTime.class, PathInits.DIRECT2);

    public final EnumPath<MedicationType> medicationType = createEnum("medicationType", MedicationType.class);

    public final StringPath medicineName = createString("medicineName");

    public final EnumPath<SoundType> soundType = createEnum("soundType", SoundType.class);

    public final DateTimePath<java.time.LocalDateTime> startDateTime = createDateTime("startDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QMedication(String variable) {
        super(Medication.class, forVariable(variable));
    }

    public QMedication(Path<? extends Medication> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMedication(PathMetadata metadata) {
        super(Medication.class, metadata);
    }

}

