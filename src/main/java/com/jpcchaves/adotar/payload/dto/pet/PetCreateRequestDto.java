package com.jpcchaves.adotar.payload.dto.pet;

import com.jpcchaves.adotar.domain.Enum.AnimalGender;
import com.jpcchaves.adotar.domain.Enum.AnimalSize;
import com.jpcchaves.adotar.domain.Enum.HealthCondition;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

public class PetCreateRequestDto {

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @PositiveOrZero(message = "A idade em anos deve ser um valor maior ou igual a 0")
    @Min(value = 0, message = "A idade em anos deve ser 0 ou maior")
    @Max(value = 25, message = "Verifique a idade em anos informada e tente novamente")
    @NotNull(message = "A idade em anos é obrigatória")
    private int yearsAge;

    @Min(value = 1, message = "A idade em meses deve ser um valor entre 1 e 11 (meses do ano)")
    @Max(value = 11, message = "A idade em meses deve ser um valor entre 1 e 11 (meses do ano)")
    @Positive(message = "A idade em meses deve ser um valor maior ou igual a 1")
    @NotNull(message = "A idade em meses é obrigatória")
    private int monthsAge;

    private char gender;
    private char size;
    private char healthCondition;

    @NotBlank(message = "A cor é obrigatória")
    private String color;
    private String description;
    private boolean active;
    private boolean isAvailable;
    private List<Long> characteristicsIds = new ArrayList<>();
    private List<PetPictureDto> petPictures = new ArrayList<>();

    @NotNull(message = "O tipo do animal é obrigatório")
    private Long typeId;

    @NotNull(message = "A raça do animal é obrigatória")
    private Long breedId;

    public PetCreateRequestDto() {
    }

    public PetCreateRequestDto(String name,
                               int yearsAge,
                               int monthsAge,
                               AnimalGender gender,
                               AnimalSize size,
                               HealthCondition healthCondition,
                               String color,
                               String description,
                               boolean active,
                               boolean isAvailable,
                               List<Long> characteristicsIds,
                               List<PetPictureDto> petPictures,
                               Long typeId,
                               Long breedId) {
        this.name = name;
        this.yearsAge = yearsAge;
        this.monthsAge = monthsAge;
        setGender(gender);
        setSize(size);
        setHealthCondition(healthCondition);
        this.color = color;
        this.description = description;
        this.active = active;
        this.isAvailable = isAvailable;
        this.characteristicsIds = characteristicsIds;
        this.petPictures = petPictures;
        this.typeId = typeId;
        this.breedId = breedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYearsAge() {
        return yearsAge;
    }

    public void setYearsAge(int yearsAge) {
        this.yearsAge = yearsAge;
    }

    public int getMonthsAge() {
        return monthsAge;
    }

    public void setMonthsAge(int monthsAge) {
        this.monthsAge = monthsAge;
    }

    public AnimalGender getGender() {
        return AnimalGender.valueOf(gender);
    }

    public void setGender(AnimalGender gender) {
        if (gender != null) {
            this.gender = gender.getGender();
        }
    }

    public AnimalSize getSize() {
        return AnimalSize.valueOf(size);
    }

    public void setSize(AnimalSize size) {
        if (size != null) {
            this.size = size.getSize();
        }
    }

    public HealthCondition getHealthCondition() {
        return HealthCondition.valueOf(healthCondition);
    }

    public void setHealthCondition(HealthCondition healthCondition) {
        if (healthCondition != null) {
            this.healthCondition = healthCondition.getHealthCondition();
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public List<Long> getCharacteristicsIds() {
        return characteristicsIds;
    }

    public void setCharacteristicsIds(List<Long> characteristicsIds) {
        this.characteristicsIds = characteristicsIds;
    }

    public List<PetPictureDto> getPetPictures() {
        return petPictures;
    }

    public void setPetPictures(List<PetPictureDto> petPictures) {
        this.petPictures = petPictures;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getBreedId() {
        return breedId;
    }

    public void setBreedId(Long breedId) {
        this.breedId = breedId;
    }
}
