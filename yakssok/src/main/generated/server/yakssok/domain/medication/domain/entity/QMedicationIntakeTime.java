package server.yakssok.domain.medication.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMedicationIntakeTime is a Querydsl query type for MedicationIntakeTime
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMedicationIntakeTime extends EntityPathBase<MedicationIntakeTime> {

    private static final long serialVersionUID = 1512851320L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMedicationIntakeTime medicationIntakeTime = new QMedicationIntakeTime("medicationIntakeTime");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMedication medication;

    public final TimePath<java.time.LocalTime> time = createTime("time", java.time.LocalTime.class);

    public QMedicationIntakeTime(String variable) {
        this(MedicationIntakeTime.class, forVariable(variable), INITS);
    }

    public QMedicationIntakeTime(Path<? extends MedicationIntakeTime> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMedicationIntakeTime(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMedicationIntakeTime(PathMetadata metadata, PathInits inits) {
        this(MedicationIntakeTime.class, metadata, inits);
    }

    public QMedicationIntakeTime(Class<? extends MedicationIntakeTime> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.medication = inits.isInitialized("medication") ? new QMedication(forProperty("medication")) : null;
    }

}

