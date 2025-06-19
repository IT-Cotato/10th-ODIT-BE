package com.odit.backend.domain.place.entity;

import java.util.ArrayList;
import java.util.List;

import com.odit.backend.domain.image.entity.UserPlaceImage;
import com.odit.backend.domain.user.entity.User;
import com.odit.backend.global.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "user_place")
public class UserPlace extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	@OneToMany(mappedBy = "userPlace", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserPlaceImage> images = new ArrayList<>();

	private String memo;
	private Boolean visited;

	public void updatedMemo(String memo) {
		this.memo = memo;
	}

	public void updatedVisited() {
		this.visited = true;
	}

	public void assignedPlace(Place place) {
		this.place = place;
	}

	public void assignedUser(User user) {
		this.user = user;
	}

	public void addImage(UserPlaceImage image) {
		this.images.add(image);
		image.assignUserPlace(this);
	}

}
