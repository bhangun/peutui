package ai.gollek.peutui.agent;

import java.util.List;

/** The conversation state an {@link Agent} is invoked with for a single turn. */
public record AgentContext(String sessionId, String projectId, List<AgentMessage> history) {
}
