package com.heyticket.backend.kopis.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPerformance is a Querydsl query type for Performance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPerformance extends EntityPathBase<Performance> {

    private static final long serialVersionUID = 1389325771L;

    public static final QPerformance performance = new QPerformance("performance");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final StringPath age = createString("age");

    public final StringPath cast = createString("cast");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath crew = createString("crew");

    public final StringPath dtguidance = createString("dtguidance");

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final StringPath entrpsnm = createString("entrpsnm");

    public final StringPath genre = createString("genre");

    public final StringPath id = createString("id");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath mt10id = createString("mt10id");

    public final BooleanPath openrun = createBoolean("openrun");

    public final StringPath pcseguidance = createString("pcseguidance");

    public final StringPath place = createString("place");

    public final StringPath poster = createString("poster");

    public final StringPath runtime = createString("runtime");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final StringPath state = createString("state");

    public final StringPath sty = createString("sty");

    public final StringPath title = createString("title");

    public QPerformance(String variable) {
        super(Performance.class, forVariable(variable));
    }

    public QPerformance(Path<? extends Performance> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPerformance(PathMetadata metadata) {
        super(Performance.class, metadata);
    }

}

