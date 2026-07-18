package ai.gollek.peutui.quarkus;

import ai.gollek.peutui.project.ProjectRegistry;
import ai.gollek.peutui.session.MultiSessionManager;
import ai.gollek.peutui.session.Session;
import ai.gollek.peutui.session.SessionManager;
import ai.gollek.peutui.session.SessionStore;
import ai.gollek.peutui.session.SingleSessionManager;
import ai.gollek.peutui.session.StorageBackedSessionStore;
import ai.gollek.peutui.storage.StorageBackend;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

/** Produces the {@link SessionStore} (always storage-backed, per configured mode) and the {@link SessionManager} strategy (single vs multi). */
@ApplicationScoped
public final class SessionManagerProducer {

    @Inject
    PeutuiConfig config;

    @Inject
    ProjectRegistry projectRegistry;

    @Inject
    @SessionStorage
    StorageBackend sessionStorageBackend;

    @Produces
    @ApplicationScoped
    public SessionStore sessionStore() {
        return new StorageBackedSessionStore(sessionStorageBackend);
    }

    @Produces
    @ApplicationScoped
    public SessionManager sessionManager(SessionStore store) {
        String projectId = projectRegistry.active().id();
        return switch (config.session().mode()) {
            case SINGLE -> new SingleSessionManager(store, Session.create(projectId, config.agent().defaultAgentId().orElse("default")));
            case MULTI -> {
                MultiSessionManager manager = new MultiSessionManager(store);
                manager.startNew(projectId, config.agent().defaultAgentId().orElse("default"));
                yield manager;
            }
        };
    }
}
