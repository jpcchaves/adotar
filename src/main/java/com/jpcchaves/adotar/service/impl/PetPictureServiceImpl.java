package com.jpcchaves.adotar.service.impl;

import com.jpcchaves.adotar.domain.entities.Pet;
import com.jpcchaves.adotar.domain.entities.PetPicture;
import com.jpcchaves.adotar.exception.ResourceNotFoundException;
import com.jpcchaves.adotar.payload.dto.ApiMessageResponseDto;
import com.jpcchaves.adotar.payload.dto.pet.PetPictureDto;
import com.jpcchaves.adotar.repository.PetPictureRepository;
import com.jpcchaves.adotar.repository.PetRepository;
import com.jpcchaves.adotar.service.usecases.PetPictureService;
import com.jpcchaves.adotar.utils.mapper.MapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetPictureServiceImpl implements PetPictureService {

    private final PetPictureRepository petPictureRepository;
    private final PetRepository petRepository;
    private final MapperUtils mapperUtils;

    public PetPictureServiceImpl(PetPictureRepository petPictureRepository,
                                 PetRepository petRepository,
                                 MapperUtils mapperUtils) {
        this.petPictureRepository = petPictureRepository;
        this.petRepository = petRepository;
        this.mapperUtils = mapperUtils;
    }

    @Override
    public List<PetPictureDto> getAll(Long petId) {
        List<PetPicture> petPictures = petPictureRepository.getAllByPet_Id(petId);
        return mapperUtils.parseListObjects(petPictures, PetPictureDto.class);
    }

    @Override
    public PetPictureDto getById(Long petId,
                                 Long picId) {
        PetPicture petPicture = petPictureRepository
                .getByIdAndPet_Id(picId, petId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada"));
        return mapperUtils.parseObject(petPicture, PetPictureDto.class);
    }

    @Override
    public PetPictureDto create(Long petId,
                                PetPictureDto petPictureDto) {
        Pet pet = petRepository
                .findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com o id informado: " + petId));

        PetPicture petPicture = mapperUtils.parseObject(petPictureDto, PetPicture.class);
        petPicture.setPet(pet);

        PetPicture savedPetPicture = petPictureRepository.save(petPicture);
        return mapperUtils.parseObject(savedPetPicture, PetPictureDto.class);
    }

    @Override
    public PetPictureDto update(Long petId,
                                Long picId,
                                PetPictureDto petPictureDto) {
        PetPicture petPicture = petPictureRepository
                .getByIdAndPet_Id(picId, petId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada com o id informado: " + picId));

        petPicture.setImgUrl(petPictureDto.getImgUrl());

        PetPicture updatedPicture = petPictureRepository.save(petPicture);
        return mapperUtils.parseObject(updatedPicture, PetPictureDto.class);
    }

    @Override
    public ApiMessageResponseDto delete(Long petId,
                                        Long picId) {
        PetPicture petPicture = petPictureRepository
                .getByIdAndPet_Id(picId, petId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada com o id informado: " + picId));

        petPictureRepository.deleteById(petPicture.getId());

        return new ApiMessageResponseDto("Foto excluída com sucesso");
    }
}
