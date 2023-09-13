package com.example.workout.repository.packaze;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class PackageRepositoryTest {
    @Autowired
    private PackageRepository packageRepository;

    @Test
    public void test_save() throws Exception{
        //given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("바디 첼린지 PT 12주");
        packageEntity.setPeriod(84);

        //when
        packageRepository.save(packageEntity);
        //then
        assertNotNull(packageEntity.getPackageSeq());
    }
    @Test
    public void test_findByCreatedAtAfter() throws Exception{
        //given
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(1);
        PackageEntity packageEntity0 = new PackageEntity();
        packageEntity0.setPackageName("학생 전용 3개월");
        packageEntity0.setPeriod(90);
        PackageEntity packageEntity1 = new PackageEntity();
        packageEntity1.setPackageName("학생 전용 6개월");
        packageEntity1.setPeriod(180);


        packageRepository.saveAll(List.of(packageEntity0, packageEntity1));

        //when
        List<PackageEntity> packageEntities = packageRepository.findByCreatedAtAfter(dateTime,
                PageRequest.of(0, 1, Sort.by("packageSeq").descending())
        );

        //then
        assertEquals(1, packageEntities.size());
        assertEquals(packageEntity1.getPackageSeq(), packageEntities.get(0).getPackageSeq());
    }

    @Test
    public void test_updateCountAndPeriod() throws Exception{
        //given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPeriod(90);
        packageEntity.setPackageName("바디프로필 이벤트 4개월");
        packageRepository.save(packageEntity);

        //when
        int updatedCount = packageRepository.updateCountAndPeriod(packageEntity.getPackageSeq(), 30, 120);
        PackageEntity packaze = packageRepository.findById(packageEntity.getPackageSeq()).get();


        //then
        assertEquals(updatedCount,1);
        assertEquals(30, packaze.getCount());
        assertEquals(120, packaze.getPeriod());
    }
    @Test
    public void test_delete() throws Exception{
        //given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPeriod(90);
        packageEntity.setPackageName("바디프로필 이벤트 4개월");
        packageRepository.save(packageEntity);

        //when
        packageRepository.deleteById(packageEntity.getPackageSeq());

        //then
        assertTrue(packageRepository.findById(packageEntity.getPackageSeq()).isEmpty());
    }
}