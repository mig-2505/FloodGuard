package com.floodguard.repository;

import com.floodguard.model.Medicao;
import com.floodguard.model.TipoSensor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicaoRepository
        extends JpaRepository<Medicao, Long> {

    List<Medicao> findByTipoSensorOrderByIdDesc(
            TipoSensor tipoSensor
    );

    List<Medicao> findTop10ByTipoSensorOrderByIdDesc(
            TipoSensor tipoSensor
    );

    List<Medicao> findAllByOrderByIdDesc();
}
