package com.example.workout.repository.packaze;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface PackageRepository extends JpaRepository<PackageEntity, Integer> {

    List<PackageEntity> findByCreatedAtAfter(LocalDateTime dateTime, Pageable packageSeq);

    @Transactional // 트랜잭션이 없으면 에러발생(service에서 하든지 알아서)
    @Modifying // JPQL  데이트가 변경되는 부분에서 추가되는 어노테이션
    @Query(value = "UPDATE PackageEntity p " +
        "             SET p.count = :count, " +
        "                 p.period = :period " +
        "           WHERE p.packageSeq = :packageSeq"
    )
    int updateCountAndPeriod(@Param("packageSeq")Integer packageSeq, @Param("count") Integer count, @Param("period")Integer period);

}