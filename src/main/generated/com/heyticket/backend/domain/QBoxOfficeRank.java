package com.heyticket.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoxOfficeRank is a Querydsl query type for BoxOfficeRank
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoxOfficeRank extends EntityPathBase<BoxOfficeRank> {

    private static final long serialVersionUID = -44900202L;

    public static final QBoxOfficeRank boxOfficeRank = new QBoxOfficeRank("boxOfficeRank");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final EnumPath<com.heyticket.backend.module.kopis.enums.BoxOfficeArea> area = createEnum("area", com.heyticket.backend.module.kopis.enums.BoxOfficeArea.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final EnumPath<com.heyticket.backend.module.kopis.enums.BoxOfficeGenre> genre = createEnum("genre", com.heyticket.backend.module.kopis.enums.BoxOfficeGenre.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath performanceIds = createString("performanceIds");

    public final EnumPath<com.heyticket.backend.module.kopis.enums.TimePeriod> timePeriod = createEnum("timePeriod", com.heyticket.backend.module.kopis.enums.TimePeriod.class);

    public QBoxOfficeRank(String variable) {
        super(BoxOfficeRank.class, forVariable(variable));
    }

    public QBoxOfficeRank(Path<? extends BoxOfficeRank> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoxOfficeRank(PathMetadata metadata) {
        super(BoxOfficeRank.class, metadata);
    }

}

