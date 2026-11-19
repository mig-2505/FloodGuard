package com.floodguard.repository;

import com.floodguard.model.Alerta;
import com.floodguard.model.NivelAlerta;
import com.floodguard.model.TipoSensor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRepository
        extends JpaRepository<Alerta, Long> {

    List<Alerta> findAllByOrderByIdDesc();

    List<Alerta> findByTipoSensorOrderByIdDesc(
            TipoSensor tipoSensor
    );

    List<Alerta> findByNivelOrderByIdDesc(
            NivelAlerta nivel
    );
}
