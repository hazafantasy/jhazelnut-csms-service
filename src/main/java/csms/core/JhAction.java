package csms.core;

import csms.core.jhfiles.JhFile;

public class JhAction {
    private JhActionType action;
    private JhFile file;
    public JhAction(JhActionType action, JhFile file) {
        this.setAction(action);
        this.setFile(file);
    }

    public JhActionType getAction() {
        return action;
    }

    public void setAction(JhActionType action) {
        this.action = action;
    }

    public JhFile getFile() {
        return file;
    }

    public void setFile(JhFile file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return action + " - " + file.getPath();
    }

    public enum JhActionType {
        CREATE,
        DELETE,
        UPDATE
    }
}

