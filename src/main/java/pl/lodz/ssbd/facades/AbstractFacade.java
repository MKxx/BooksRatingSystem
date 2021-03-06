/**
 * 
 */
package pl.lodz.ssbd.facades;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import pl.lodz.ssbd.exceptions.SSBD05Exception;

/**
 * Modul odpowiedzialny za blokady optymistyczne
 * @author Robert Mielczarek 
 * @param <T>
 */
public abstract class AbstractFacade<T> {

    private final Class<T> entityClass;
    
    

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) throws SSBD05Exception {
        try {
            getEntityManager().persist(entity);
            getEntityManager().flush();
        } catch (PersistenceException ex) {
            throw new SSBD05Exception(ex.getMessage());
        }
    }

    public void edit(T entity) throws SSBD05Exception {
        try {
            getEntityManager().merge(entity);
            getEntityManager().flush();
        } catch (OptimisticLockException ex) {
            throw new SSBD05Exception(ex.getMessage());
        }

    }

    public T find(Object id) throws OptimisticLockException {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

}
