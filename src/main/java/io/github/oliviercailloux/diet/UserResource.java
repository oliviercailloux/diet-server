package io.github.oliviercailloux.diet;

import io.github.oliviercailloux.diet.dao.Login;
import io.github.oliviercailloux.diet.entity.Judgment;
import io.github.oliviercailloux.diet.entity.ReadEventJudgment;
import io.github.oliviercailloux.diet.entity.UserAppendable;
import io.github.oliviercailloux.diet.entity.UserFactory;
import io.github.oliviercailloux.diet.entity.UserStatus;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/me")
public class UserResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

	@Context
	SecurityContext securityContext;

	@Inject
	UserFactory userFactory;

	@Inject
	UserService userService;

	@Inject
	VideoService videoService;

	@Inject
	EntityManager em;

	private String getCurrentUsername() {
		return securityContext.getUserPrincipal().getName();
	}

	@GET
	@RolesAllowed("user")
	@Path("/status")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserStatus status() {
		return userFactory.getStatus(getCurrentUsername());
	}

	/**
	 * Idempotent: when body changes, new user is created; otherwise, does not
	 * create new user.
	 *
	 * This should return a HTTP created, I suppose, and be based at the root.
	 */
	@PUT
	@PermitAll
	@Path("/create-accept")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserStatus createAcceptingUser(Login login) {
		LOGGER.info("Creating {}.", login);
		return userFactory.addUser(login);
	}

	@POST
	@RolesAllowed("user")
	@Path("/judgment")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserStatus postJudgment(Judgment judgment) {
		final UserAppendable user = userFactory.getAppendable(getCurrentUsername());
		final ReadEventJudgment event = ReadEventJudgment.now(judgment);
		em.persist(judgment);
		user.persistEvent(event);
		return user.status(videoService);
	}
}