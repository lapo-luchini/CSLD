package cz.larpovadatabaze.components.common.i18n;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by jbalhar on 11/30/2014.
 */
public class LocalePicker extends Panel{

    public LocalePicker(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Set default Locale based on actual Locale.

        List<Locale> locales = Arrays.asList(Locale.ENGLISH, Locale.forLanguageTag("cz"));
        final DropDownChoice<Locale> changeLocale =
                new DropDownChoice<Locale>("changeLocale", new Model<Locale>(Locale.forLanguageTag("cz")), locales);
        StatelessForm form = new StatelessForm("form"){
            @Override
            protected void onSubmit() {
                Session.get().setLocale(changeLocale.getModelObject());
            }
        };

        form.add(new Button("submit"));
        form.add(changeLocale);

        add(form);
    }
}