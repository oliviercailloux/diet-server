package io.github.oliviercailloux.sample_quarkus_heroku.dao;

import com.google.common.collect.ImmutableList;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.User;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.Video;
import java.util.List;

public class UserStatus extends User {
	private final ImmutableList<Video> seen;
	private final ImmutableList<Video> toSee;

	public UserStatus(List<Video> seen, List<Video> toSee) {
		super();
		this.seen = ImmutableList.copyOf(seen);
		this.toSee = ImmutableList.copyOf(toSee);
	}

	public ImmutableList<Video> getSeen() {
		return seen;
	}

	public ImmutableList<Video> getToSee() {
		return toSee;
	}
}
