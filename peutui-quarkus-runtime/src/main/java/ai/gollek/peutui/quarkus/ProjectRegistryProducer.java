package ai.gollek.peutui.quarkus;

import ai.gollek.peutui.project.MultiProjectRegistry;
import ai.gollek.peutui.project.Project;
import ai.gollek.peutui.project.ProjectRegistry;
import ai.gollek.peutui.project.SingleProjectRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import java.nio.file.Path;

/** Produces a {@link ProjectRegistry} strategy (single-fixed vs multi-switchable) driven by {@code peutui.project.mode}. */
@ApplicationScoped
public final class ProjectRegistryProducer {

    @Inject
    PeutuiConfig config;

    @Produces
    @ApplicationScoped
    public ProjectRegistry projectRegistry() {
        Path root = Path.of(config.project().rootPath()).toAbsolutePath().normalize();
        Project initial = Project.of(root.getFileName() != null ? root.getFileName().toString() : "default", root.toString(), root);
        return switch (config.project().mode()) {
            case SINGLE -> new SingleProjectRegistry(initial);
            case MULTI -> new MultiProjectRegistry().add(initial);
        };
    }
}
