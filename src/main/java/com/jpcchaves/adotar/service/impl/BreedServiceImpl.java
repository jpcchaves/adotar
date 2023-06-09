package com.jpcchaves.adotar.service.impl;

import com.jpcchaves.adotar.domain.entities.Breed;
import com.jpcchaves.adotar.payload.dto.pet.BreedDto;
import com.jpcchaves.adotar.repository.BreedRepository;
import com.jpcchaves.adotar.service.usecases.BreedService;
import com.jpcchaves.adotar.utils.mapper.MapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BreedServiceImpl implements BreedService {

    private final BreedRepository breedRepository;
    private final MapperUtils mapper;

    public BreedServiceImpl(BreedRepository breedRepository,
                            MapperUtils mapper) {
        this.breedRepository = breedRepository;
        this.mapper = mapper;
    }

    @Override
    public List<BreedDto> findAllByAnimalType(Long animalTypeId) {
        List<Breed> breedList = breedRepository.findAllByAnimalType_Id(animalTypeId);
        return mapper.parseListObjects(breedList, BreedDto.class);
    }
}
