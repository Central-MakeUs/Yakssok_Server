package server.yakssok.domain.medication.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMedicationIntakeDay is a Querydsl query type for MedicationIntakeDay
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMedicationIntakeDay extends EntityPathBase<MedicationIntakeDay> {

    private static final long serialVersionUID = 880070033L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMedicationIntakeDay medicationIntakeDay = new QMedicationIntakeDay("medicationIntakeDay");

    public final EnumPath<java.time.DayOfWeek> dayOfWeek = createEnum("dayOfWeek", java.time.DayOfWeek.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMedication medication;

    public QMedicationIntakeDay(String variable) {
        this(MedicationIntakeDay.class, forVariable(variable), INITS);
    }

    public QMedicationIntakeDay(Path<? extends MedicationIntakeDay> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMedicationIntakeDay(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMedicationIntakeDay(PathMetadata metadata, PathInits inits) {
        this(MedicationIntakeDay.class, metadata, inits);
    }

    public QMedicationIntakeDay(Class<? extends MedicationIntakeDay> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.medication = inits.isInitialized("medication") ? new QMedication(forProperty("medication")) : null;
    }

}

