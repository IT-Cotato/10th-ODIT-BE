package com.odit.backend.domain.place.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.odit.backend.domain.image.entity.PlaceImage;
import com.odit.backend.domain.place.dto.request.PlaceRequestDto;
import com.odit.backend.global.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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

	@Id@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private Long seq;

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
