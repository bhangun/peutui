package ai.gollek.peutui.quarkus;

import ai.gollek.peutui.terminal.JLineTerminalDriver;
import ai.gollek.peutui.terminal.TerminalDriver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

/**
 * Produces the default JLine-backed {@link TerminalDriver}. A host
 * application can instead supply its own {@code @ApplicationScoped}
 * {@code TerminalDriver} bean (e.g. a headless test double) - CDI resolves
 * to whichever single bean is present.
 */
@ApplicationScoped
public final class TerminalDriverProducer {

    @Produces
    @ApplicationScoped
    public TerminalDriver terminalDriver() {
        return new JLineTerminalDriver();
    }

    public void disposeTerminalDriver(@Disposes TerminalDriver driver) {
        driver.stop();
    }
}
