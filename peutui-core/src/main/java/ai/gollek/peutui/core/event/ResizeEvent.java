package ai.gollek.peutui.core.event;

import ai.gollek.peutui.core.layout.Size;

/** Fired when the underlying terminal window is resized. */
public record ResizeEvent(Size newSize) implements InputEvent {
}
