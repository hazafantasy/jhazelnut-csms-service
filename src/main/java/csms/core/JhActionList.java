package csms.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class JhActionList {

    private List<JhAction> actions;

    public JhActionList() {
        actions = new ArrayList<>();
    }

    public void addAction(JhAction.JhActionType actionType, JhFile file) {
        actions.add(new JhAction(actionType, file));
    }

    public List<JhAction> getActions() {
        return actions;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(actions);
        } catch (Exception ex) {
            ex.printStackTrace();
            json = "ERROR mapping actions to JSON";
        }
        return json;
    }

}
