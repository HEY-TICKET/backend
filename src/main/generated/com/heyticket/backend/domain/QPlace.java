package com.heyticket.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPlace is a Querydsl query type for Place
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlace extends EntityPathBase<Place> {

    private static final long serialVersionUID = 1955967850L;

    public static final QPlace place = new QPlace("place");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final StringPath address = createString("address");

    public final StringPath Characteristic = createString("Characteristic");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath gugunName = createString("gugunName");

    public final StringPath id = createString("id");

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath name = createString("name");

    public final StringPath openYear = createString("openYear");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath relateUrl = createString("relateUrl");

    public final NumberPath<Integer> seatScale = createNumber("seatScale", Integer.class);

    public final StringPath sidoName = createString("sidoName");

    public final NumberPath<Integer> stageCount = createNumber("stageCount", Integer.class);

    public QPlace(String variable) {
        super(Place.class, forVariable(variable));
    }

    public QPlace(Path<? extends Place> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlace(PathMetadata metadata) {
        super(Place.class, metadata);
    }

}

