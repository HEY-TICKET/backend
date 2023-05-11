package com.heyticket.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoxOffice is a Querydsl query type for BoxOffice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoxOffice extends EntityPathBase<BoxOffice> {

    private static final long serialVersionUID = -1123297974L;

    public static final QBoxOffice boxOffice = new QBoxOffice("boxOffice");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final StringPath area = createString("area");

    public final StringPath cate = createString("cate");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath mt20id = createString("mt20id");

    public final StringPath poster = createString("poster");

    public final NumberPath<Integer> prfdtcnt = createNumber("prfdtcnt", Integer.class);

    public final StringPath prfnm = createString("prfnm");

    public final StringPath prfplcnm = createString("prfplcnm");

    public final StringPath rnum = createString("rnum");

    public final StringPath seatcnt = createString("seatcnt");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public QBoxOffice(String variable) {
        super(BoxOffice.class, forVariable(variable));
    }

    public QBoxOffice(Path<? extends BoxOffice> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoxOffice(PathMetadata metadata) {
        super(BoxOffice.class, metadata);
    }

}

