package server.yakssok.domain.medication_schedule.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMedicationSchedule is a Querydsl query type for MedicationSchedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMedicationSchedule extends EntityPathBase<MedicationSchedule> {

    private static final long serialVersionUID = 713092702L;

    public static final QMedicationSchedule medicationSchedule = new QMedicationSchedule("medicationSchedule");

    public final server.yakssok.domain.QBaseEntity _super = new server.yakssok.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isTaken = createBoolean("isTaken");

    public final NumberPath<Long> medicationId = createNumber("medicationId", Long.class);

    public final DatePath<java.time.LocalDate> scheduledDate = createDate("scheduledDate", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> scheduledTime = createTime("scheduledTime", java.time.LocalTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QMedicationSchedule(String variable) {
        super(MedicationSchedule.class, forVariable(variable));
    }

    public QMedicationSchedule(Path<? extends MedicationSchedule> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMedicationSchedule(PathMetadata metadata) {
        super(MedicationSchedule.class, metadata);
    }

}

