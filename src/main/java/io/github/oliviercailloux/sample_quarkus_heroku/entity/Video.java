package io.github.oliviercailloux.sample_quarkus_heroku.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
@NamedQuery(name = "latest file id", query = "SELECT MAX(v.fileId) FROM Video v")
@NamedQuery(name = "replies", query = "SELECT a.video FROM Video v, ArguerAttack a JOIN a.counters v WHERE v IN (:videos)")
@NamedQuery(name = "starters", query = "SELECT v FROM Video v WHERE v.fileId IN (1, 2)")
public class Video {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int id;

	@NotNull
	@Column(unique = true)
	private int fileId;

	@NotNull
	@Column(unique = true)
	private String description;

	@OneToMany(mappedBy = "video")
	@JsonIgnore
	private Set<ArguerAttack> counters;

	@OneToMany(mappedBy = "counters")
	@JsonIgnore
	private Set<ArguerAttack> counteredBy;

	Video() {
		counters = new LinkedHashSet<>();
		counteredBy = new LinkedHashSet<>();
	}

	public Video(int fileId, String description) {
		this();
		this.fileId = fileId;
		this.description = checkNotNull(description);
	}

	public int getFileId() {
		return fileId;
	}

	public String getDescription() {
		return description;
	}

	public ImmutableSet<Video> getCounters() {
		return counters.stream().map(ArguerAttack::getCounters).collect(ImmutableSet.toImmutableSet());
	}

	public ImmutableSet<Video> getCounteredBy() {
		return counters.stream().map(ArguerAttack::getVideo).collect(ImmutableSet.toImmutableSet());
	}

	public void addCounters(ArguerAttack attack) {
		checkArgument(attack.getVideo().equals(this));
		counters.add(attack);
	}

	public ArguerAttack addCounters(Video countered) {
		final ArguerAttack attack = new ArguerAttack(this, countered);
		counters.add(attack);
		countered.addCounteredBy(attack);
		return attack;
	}

	public void addCounteredBy(ArguerAttack attack) {
		checkArgument(attack.getCounters().equals(this));
		counteredBy.add(attack);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("fileId", fileId).add("description", description)
				.add("counters", counters).add("counteredBy", counteredBy).toString();
	}
}