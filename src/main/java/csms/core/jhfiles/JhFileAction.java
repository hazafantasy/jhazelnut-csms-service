package csms.core.jhfiles;

public class JhFileAction {
    private JhActionType action;
    private JhFile file;
    public JhFileAction(JhActionType action, JhFile file) {
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

