package csms.core.jhfiles;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class JhFileActionList {

    private List<JhFileAction> actions;

    public JhFileActionList() {
        actions = new ArrayList<>();
    }

    public void addAction(JhFileAction.JhActionType actionType, JhFile file) {
        actions.add(new JhFileAction(actionType, file));
    }

    public List<JhFileAction> getActions() {
        return actions;
    }

    @Override
    public String toString() {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        ArrayNode actionList = factory.arrayNode();
        for(JhFileAction action: actions) {
            actionList.add(action.toString());
        }
        root.set("Actions", actionList);
        return root.toString();
    }

}
