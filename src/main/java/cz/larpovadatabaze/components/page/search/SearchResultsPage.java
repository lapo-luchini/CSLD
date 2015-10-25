package cz.larpovadatabaze.components.page.search;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.AbstractListGamePanel;
import cz.larpovadatabaze.components.panel.search.SearchBoxPanel;
import cz.larpovadatabaze.entities.Game;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 */
public class SearchResultsPage extends CsldBasePage {

    public SearchResultsPage(PageParameters params) {
        String query = params.get(SearchBoxPanel.QUERY_PARAMETER_NAME).toString();

        final Component gamesResultsPanel = new AbstractListGamePanel<String>("games", Model.of(query)) {
            @Override
            protected SortableDataProvider<Game, String> getDataProvider() {
                GameSearchProvider res = new GameSearchProvider();
                res.setQuery(query);
                return res;
            }
        };
        gamesResultsPanel.setOutputMarkupId(true);
        add(gamesResultsPanel);

        /*final UserResultsPanel userResultsPanel = new UserResultsPanel("users", query);
        userResultsPanel.setOutputMarkupId(true);
        add(userResultsPanel);*/
    }

    public static PageParameters paramsForSearchResults(String query) {
        PageParameters params = new PageParameters();

        params.add(SearchBoxPanel.QUERY_PARAMETER_NAME, query);

        return params;
    }

}
