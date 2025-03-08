package gui;


public enum ConfigWindowType {
    MAIN_FRAME_CONFIG("window.config"),
    GAME_INTERNAL_CONFIG("internalGame.config"),
    LOG_INTERNAL_CONFIG("internalLog.config");

    private final String fileName;

    ConfigWindowType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

