package com.adit.backend.domain.place.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.adit.backend.domain.image.entity.PlaceImage;
import com.adit.backend.domain.place.dto.request.PlaceRequestDto;
import com.adit.backend.global.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place")
public class Place extends BaseEntity {

	@Id
	private Long id;

	@Column(nullable = false)
	private String placeName;

	@Column(nullable = false, precision = 38, scale = 7)
	private BigDecimal latitude;

	@Column(nullable = false, precision = 38, scale = 7)
	private BigDecimal longitude;

	@Column(nullable = false)
	private String addressName;

	@Column(nullable = false)
	private String roadAddressName;

	@Column(nullable = false)
	private String subCategory;

	@Column(nullable = false)
	private String url;

	@Builder.Default
	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlaceImage> images = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserPlace> userPlaces = new ArrayList<>();

	public void updatePlace(PlaceRequestDto requestDto) {
		this.placeName = requestDto.placeName();
		this.addressName = requestDto.addressName();
		this.latitude = requestDto.latitude();
		this.longitude = requestDto.longitude();
		this.roadAddressName = requestDto.roadAddressName();
		this.subCategory = requestDto.subCategory();
		this.url = requestDto.url();
	}

	//연관관계 메서드
	public void addUserPlace(UserPlace userPlace) {
		this.userPlaces.add(userPlace);
		userPlace.assignedPlace(this);
	}

	public void addImage(PlaceImage image) {
		this.images.add(image);
		image.assignPlace(this);
	}

}
