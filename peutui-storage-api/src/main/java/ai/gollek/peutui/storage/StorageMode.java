package ai.gollek.peutui.storage;

/** Which {@link StorageBackend} strategy to use, typically driven by application configuration. */
public enum StorageMode {
    LOCAL_FILE,
    LOCAL_DATABASE,
    CLOUD
}
