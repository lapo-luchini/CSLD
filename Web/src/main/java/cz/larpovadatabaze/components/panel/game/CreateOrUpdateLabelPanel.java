package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.lang.CodeLocaleProvider;
import cz.larpovadatabaze.lang.LanguageSolver;
import cz.larpovadatabaze.lang.LocaleProvider;
import cz.larpovadatabaze.lang.SessionLanguageSolver;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.LabelService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * It is used for creating and updating Labels for games.
 */
public abstract class CreateOrUpdateLabelPanel extends Panel {
    @SpringBean
    LabelService labelService;
    LocaleProvider localeProvider = new CodeLocaleProvider();
    LanguageSolver sessionLanguageSolver = new SessionLanguageSolver();

    public CreateOrUpdateLabelPanel(String id){
        this(id,Label.getEmptyLabel());
    }

    public CreateOrUpdateLabelPanel(String id, Label label) {
        super(id);
        if(label == null){
            label = Label.getEmptyLabel();
        }

        final ValidatableForm<Label> createOrUpdateLabel =
                new ValidatableForm<Label>("createOrUpdateLabel", new CompoundPropertyModel<Label>(label)){};

        createOrUpdateLabel.add(new TextField<String>("name"));
        createOrUpdateLabel.add(new TextArea<String>("description"));
        List<String> availableLanguages = new ArrayList<String>();
        LocaleProvider provider = new CodeLocaleProvider();
        List<Locale> availableLocale = provider.availableLocale();
        for(Locale available: availableLocale) {
            availableLanguages.add(provider.transformLocaleToName(available));
        }
        final DropDownChoice<String> changeLocale =
                new DropDownChoice<String>("lang", availableLanguages);
        createOrUpdateLabel.add(changeLocale);

        createOrUpdateLabel.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                if(createOrUpdateLabel.isValid()){
                    Label label = createOrUpdateLabel.getModelObject();
                    processLanguage(label);
                    if(saveOrUpdateLabel(label)){
                        onCsldAction(target, form);
                    }
                }
            }
        });

        add(createOrUpdateLabel);
    }

    private void processLanguage(Label label){
        Locale toBeSaved;
        if(label.getLang() != null) {
            toBeSaved = localeProvider.transformToLocale(label.getLang());
        } else {
            label.setLabelHasLanguages(new ArrayList<LabelHasLanguages>());
            toBeSaved = sessionLanguageSolver.getLanguagesForUser().get(0);
        }

        if(label.getLabelHasLanguages().isEmpty()) {
            LabelHasLanguages firstLanguage = new LabelHasLanguages();
            firstLanguage.setLabel(label);
            firstLanguage.setLanguage(new Language(toBeSaved));
            firstLanguage.setName(label.getName());
            firstLanguage.setDescription(label.getDescription());
            label.getLabelHasLanguages().add(firstLanguage);
        } else {
            // Find existing locale
            List<LabelHasLanguages> actualLanguages = label.getLabelHasLanguages();
            Language actualToSave = new Language(toBeSaved);
            for(LabelHasLanguages language: actualLanguages) {
                if(language.getLanguage().equals(actualToSave)){
                    language.setName(label.getName());
                    language.setDescription(label.getDescription());
                }
            }
        }
    }

    private boolean saveOrUpdateLabel(Label label) {
        CsldUser loggedUser = CsldAuthenticatedWebSession.get().getLoggedUser();
        label.setAddedBy(loggedUser);
        label.setRequired(false);
        return labelService.saveOrUpdate(label);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}