package com.trip.triptogether.constant;

import java.util.List;

public interface GenericMapper<D,E> {

    D toDto(E e); //엔티티 -> DTO

    E toEntity(D d); //DTO -> 엔티티

    List<D> toDtoList(List<E> entitiyList); //엔티티 list -> Dto list

    List<E> toEntityList(List<D> dtolist); // Dto list -> 엔티티 list
}
