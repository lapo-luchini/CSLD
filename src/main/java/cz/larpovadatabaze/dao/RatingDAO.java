package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.Rating;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 *
 */
@Repository
public class RatingDAO extends GenericHibernateDAO<Rating, Integer> {
    public int getAmountOfRatings() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Rating.class);
        criteria.setProjection(Projections.rowCount());
        return ((Long)criteria.uniqueResult()).intValue();
    }

    public double getAverageRating() {
        Session session = sessionFactory.getCurrentSession();
        String sqlQuery = String.format("select csld_count_average()");
        Query query = session.createSQLQuery(sqlQuery);
        if (query.uniqueResult() == null) { return 0.0; }
        else { return ((Double) query.uniqueResult()).doubleValue(); }
    }

    public Integer getRatingsForGame(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery("select count(*) from csld_rating where game_id = " + id);
        return ((BigInteger) query.uniqueResult()).intValue();
    }
}
