package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.GroupDAO;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.lang.LanguageSolver;
import cz.larpovadatabaze.services.GroupService;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.request.resource.ResourceReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Repository
@Transactional
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private ImageService imageService;

    private ResourceReference iconResourceReference;

    @Override
    public boolean insert(CsldGroup group) {
        return groupDAO.saveOrUpdate(group);
    }

    @Override
    public List<CsldGroup> getAll() {
        return groupDAO.findAll();
    }

    @Override
    public List<CsldGroup> getUnique(CsldGroup example) {
        return groupDAO.findByName(example.getName());
    }

    @Override
    public void remove(CsldGroup toRemove) {
        groupDAO.makeTransient(toRemove);
    }

    @Override
    public List<CsldGroup> getFirstChoices(String startsWith, int maxChoices) {
        return groupDAO.getFirstChoices(startsWith, maxChoices);
    }

    @Override
    public CsldGroup getById(Integer id){
        return groupDAO.findById(id);
    }

    @Override
    public List<CsldGroup> getByAutoCompletable(String groupName) throws WrongParameterException {
        return groupDAO.getByAutoCompletable(groupName);
    }

    @Override
    public void saveOrUpdate(CsldGroup group) {
        groupDAO.saveOrUpdate(group);
    }

    @Override
    public ResourceReference getIconReference() {
        synchronized(this) {
            if (iconResourceReference == null) {
                iconResourceReference = imageService.createImageTypeResourceReference(groupDAO);
            }
        }

        return iconResourceReference;
    }
}
