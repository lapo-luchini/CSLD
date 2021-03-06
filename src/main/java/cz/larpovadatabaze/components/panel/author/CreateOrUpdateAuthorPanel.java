package cz.larpovadatabaze.components.panel.author;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.validator.UniqueUserValidator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel used for registering new author or adding new Author into the database.
 */
public abstract class CreateOrUpdateAuthorPanel extends Panel {
    @SpringBean
    CsldUserService csldUserService;

    public CreateOrUpdateAuthorPanel(String id, CsldUser author) {
        super(id);

        boolean isEdit = true;
        if(author == null) {
            isEdit = false;
            author = CsldUser.getEmptyUser();
        }

        final ValidatableForm<CsldUser> createOrUpdateUser = new ValidatableForm<CsldUser>("addUser", new CompoundPropertyModel<CsldUser>(author));
        createOrUpdateUser.setMultiPart(false);
        createOrUpdateUser.setOutputMarkupId(true);

        EmailTextField email = new EmailTextField("person.email");
        email.add(new UniqueUserValidator(false, csldUserService));
        createOrUpdateUser.add(addFeedbackPanel(email, createOrUpdateUser, "emailFeedback", "form.loginMail"));

        TextField<String> name = new TextField<String>("person.name");
        name.setRequired(true);
        createOrUpdateUser.add(addFeedbackPanel(name, createOrUpdateUser, "nameFeedback", "form.description.wholeName"));


        TextField<String> nickname = new TextField<String>("person.nickname");
        createOrUpdateUser.add(addFeedbackPanel(nickname, createOrUpdateUser, "nicknameFeedback", "form.description.nickname"));

        TextArea<String> description = new TextArea<String>("person.description");
        createOrUpdateUser.add(description);

        createOrUpdateUser.add(new AjaxSubmitLink("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                if(createOrUpdateUser.isValid()){
                    CsldUser author = createOrUpdateUser.getModelObject();
                    if(csldUserService.saveOrUpdateNewAuthor(author)){
                        onCsldAction(target, form);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);

                // Refresh form
                target.add(form);
            }
        });

        add(createOrUpdateUser);
    }

    private FormComponent addFeedbackPanel(FormComponent addFeedbackTo, Form addingFeedbackTo, String feedbackId, String defaultKey){
        addingFeedbackTo.add(new CsldFeedbackMessageLabel(feedbackId, addFeedbackTo, defaultKey));
        return addFeedbackTo;
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
