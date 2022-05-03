package io.github.oliviercailloux.diet;

import static com.google.common.base.Preconditions.checkState;

import io.github.oliviercailloux.diet.user.Judgment;
import io.github.oliviercailloux.diet.user.Login;
import io.github.oliviercailloux.diet.user.ReadEventJudgment;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.github.oliviercailloux.diet.user.UserPersistentWithEvents;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.Side;
import io.github.oliviercailloux.diet.video.Video;
import io.github.oliviercailloux.diet.video.VideoFactory;
import io.quarkus.runtime.StartupEvent;
import java.util.Optional;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Startup {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

	@Inject
	EntityManager em;

	@Inject
	VideoFactory videoFactory;

	@Inject
	UserFactory userFactory;

	@Transactional
	public void loadAtStartup(@Observes StartupEvent evt) {
		LOGGER.info("Loading at startup, considering {}.", evt);
		loadVideos();
		loadUsers();
	}

	@Transactional
	public void loadVideos() {
		final TypedQuery<Integer> q = em.createNamedQuery("latest file id", Integer.class);
		final Optional<Integer> initialLatest = Optional.ofNullable(q.getSingleResult());
		checkState(initialLatest.isEmpty());
		final Video v1 = new Video(1, "Climat et biodiversité", Side.VEGAN);
		final Video v2 = new Video(2, "Santé vegan", Side.VEGAN);
		final Video v3 = new Video(3, "Réduction pour écologie", Side.VEGAN);
		final Video v4 = new Video(4, "Entente", Side.VEGAN);
		final Video v5 = new Video(5, "Stratégie", Side.VEGAN);
		final Video v6 = new Video(6, "Effort écologique", Side.VEGAN);
		final Video v7 = new Video(7, "Consolidation", Side.VEGAN);
		final Video v8 = new Video(8, "Durable, éthique et gout", Side.VEGAN);
		final Video v9 = new Video(9, "Élevage moindre mal que transport", Side.MEAT);
		final Video v10 = new Video(10, "Élevage encourage biodiversité", Side.MEAT);
		final Video v11 = new Video(11, "Prairies bonnes pour GES", Side.MEAT);
		final Video v12 = new Video(12, "Santé viande", Side.MEAT);
		final Video v13 = new Video(13, "Viande pour ados", Side.MEAT);
		final Video v14 = new Video(14, "Liberté de choix aux enfants", Side.MEAT);
		final Video v15 = new Video(15, "B12 ou mauvais traitement", Side.MEAT);
		final Video v16 = new Video(16, "Imposition de classe", Side.MEAT);
		em.persist(v1);
		em.persist(v2);
		em.persist(v3);
		em.persist(v4);
		em.persist(v5);
		em.persist(v6);
		em.persist(v7);
		em.persist(v8);
		em.persist(v9);
		em.persist(v10);
		em.persist(v11);
		em.persist(v12);
		em.persist(v13);
		em.persist(v14);
		em.persist(v15);
		em.persist(v16);
		em.persist(v9.addCounters(v1));
		em.persist(v9.addCounters(v3));
		em.persist(v10.addCounters(v1));
		em.persist(v11.addCounters(v1));
		em.persist(v12.addCounters(v2));
		em.persist(v13.addCounters(v2));
		em.persist(v14.addCounters(v2));
		em.persist(v15.addCounters(v2));
		final TypedQuery<Integer> q2 = em.createNamedQuery("latest file id", Integer.class);
		final int newLatest = q2.getSingleResult();
		checkState(newLatest == 16);
	}

	@Transactional
	public void loadUsers() {
		userFactory.addAdmin(new Login("admin", "admin"));
		/*
		 * For the utf8-base64 equivalent to 'user0:user' for curl, use
		 * 'dXNlcjA=:dXNlcg=='.
		 */
		userFactory.addUser(new Login("user0", "user"));
		userFactory.addUser(new Login("élevé", "user"));
		{
			final UserPersistentWithEvents user = userFactory.addUser(new Login("inited", "user"));
			final Judgment judgment = new Judgment(1, 2);
			LOGGER.info("Adding {}.", judgment);
			em.persist(judgment);
			user.persistEvent(ReadEventJudgment.now(judgment));
		}
		{
			final UserPersistentWithEvents user = userFactory.addUser(new Login("seen", "user"));
			final Judgment judgment = new Judgment(3, 1);
			em.persist(judgment);
			user.persistEvent(ReadEventJudgment.now(judgment));
			user.persistEvent(ReadEventSeen.now(videoFactory.getVideo(3)));
		}
	}
}