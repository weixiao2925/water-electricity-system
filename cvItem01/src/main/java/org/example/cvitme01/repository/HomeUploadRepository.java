package org.example.cvitme01.repository;

import org.babyfish.jimmer.spring.repository.JRepository;
import org.example.cvitme01.entity.dto.Reading;

public interface HomeUploadRepository extends JRepository<Reading, Long> {

}
