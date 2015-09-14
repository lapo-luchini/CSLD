package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.ClassContentModel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;

/**
 *  Simple button which remember its state and can return therefore whether Label it is backed by was selected.
 */
public class LabelButton extends Button {
    private IModel<Label> selected;

    public LabelButton(String id, IModel<String> value, IModel<Label> selected) {
        super(id, value);

        this.selected = selected;
        final Label actualLabel = selected.getObject();
        final ClassContentModel classContent = new ClassContentModel();

        add(new AttributeAppender("class", classContent, " "));
        add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                classContent.select();
                actualLabel.select();
                target.add(LabelButton.this);
            }
        });

        if(actualLabel.isSelected()){
            classContent.setSelected(true);
        }

        setOutputMarkupId(true);
    }

    public IModel<Label> getLabelModel(){
        return selected;
    }
}
