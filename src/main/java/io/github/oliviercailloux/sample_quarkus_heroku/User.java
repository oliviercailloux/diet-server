package io.github.oliviercailloux.sample_quarkus_heroku;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
@UserDefinition
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Username
	@NotNull
	private String username;
	@Password
	@NotNull
	private String password;
	@Roles
	@NotNull
	private String role;

	@OneToMany(mappedBy = "user")
	@NotNull
	List<Event> events;

	public User() {
		events = new ArrayList<>();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		checkArgument(!role.contains(","));
		this.role = role;
	}

	public void addEvent(Event event) {
		checkNotNull(event);
		checkArgument(event.getUser().equals(this));
		final boolean isAccepted = event instanceof EventAccepted;
		checkArgument(isAccepted == events.isEmpty());
		events.add(event);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("username", username).add("role", role).toString();
	}
}