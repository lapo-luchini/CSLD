package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.LabelService;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * It shows all Labels and allows any amount of them to be chosen.
 * It is possible to get chosen labels from this panel.
 */
public class ChooseLabelsPanel extends FormComponentPanel<List<cz.larpovadatabaze.entities.Label>> {
    @SpringBean
    LabelService labelService;

    private SimpleListViewer requiredLabels;
    private SimpleListViewer otherLabels;

    private Set<cz.larpovadatabaze.entities.Label> newLabelSet = new HashSet<cz.larpovadatabaze.entities.Label>();

    public ChooseLabelsPanel(String id, IModel<List<cz.larpovadatabaze.entities.Label>> model){
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();

        requiredLabels = new SimpleListViewer("requiredLabels", labelService.getAuthorizedRequired(logged));
        add(requiredLabels);

        otherLabels = new SimpleListViewer("otherLabels", labelService.getAuthorizedOptional(logged));
        add(otherLabels);
    }

    public void reload(AjaxRequestTarget target) {
        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();

        requiredLabels.setList(labelService.getAuthorizedRequired(logged));
        otherLabels.setList(labelService.getAuthorizedOptional(logged));

        target.add(ChooseLabelsPanel.this);
    }

    private class SimpleListViewer extends ListView<cz.larpovadatabaze.entities.Label> {
        public SimpleListViewer(String id, List<? extends cz.larpovadatabaze.entities.Label> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(ListItem<cz.larpovadatabaze.entities.Label> item) {
            final cz.larpovadatabaze.entities.Label ourLabel = item.getModelObject();

            WebMarkupContainer wrapper = new WebMarkupContainer("label");
            item.add(wrapper);

            if (StringUtils.isNotEmpty(ourLabel.getDescription())) {
                item.add(new AttributeAppender("title", ourLabel.getDescription()));
            }

            wrapper.add(new CheckBox("checkbox", new Model<Boolean>(ChooseLabelsPanel.this.getModelObject().contains(ourLabel)))
            {
                @Override
                protected void convertInput() {
                    super.convertInput();

                    if (Boolean.TRUE.equals(getConvertedInput())) {
                        newLabelSet.add(ourLabel);
                    }
                }

                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);

                    // This is needed, otherwise Chrome does not send anything in multipart form (?)
                    tag.put("value", "on");
                }
            });

            wrapper.add(new Label("name", ourLabel.getName()));

            /*
            Label tooltip = new Label("tooltip", actualLabel.getDescription());
            tooltip.setVisible(StringUtils.isNotBlank(actualLabel.getDescription()));
            item.add(tooltip);
            */
        }
    }

    @Override
    protected void onModelChanged() {
        super.onModelChanged();

        // Prepare empty list for next iteration
        newLabelSet.clear();;
    }

    @Override
    protected void convertInput() {
        // Set newly collected label list
        List<cz.larpovadatabaze.entities.Label> newLabelList = new ArrayList<cz.larpovadatabaze.entities.Label>(newLabelSet);
        setConvertedInput(newLabelList);
    }
}
