package mg.razherana.banking.configuration.services.changeServices;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mg.razherana.banking.configuration.entities.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class ConfigurationServiceImpl implements ConfigurationService {

  @PersistenceContext(unitName = "userPU")
  private EntityManager entityManager;

  private static final Logger logger = Logger.getLogger(ConfigurationServiceImpl.class.getName());

  @Override
  public Configuration getConfigurationByName(String name) {
    if (name == null || name.trim().isEmpty()) {
      logger.warning("Configuration name cannot be null or empty");
      return null;
    }

    // System.out.println("[ConfigurationService] Fetching configuration for name: " + name);

    try {
      // Using TypedQuery for type safety
      TypedQuery<Configuration> query = entityManager.createQuery(
          "SELECT c FROM Configuration c WHERE c.name = :name",
          Configuration.class);
      query.setParameter("name", name.trim());

      return query.getSingleResult();
    } catch (NoResultException e) {
      // This is expected when no configuration with the given name exists
      logger.info("No configuration found with name: " + name);
      return null;
    } catch (Exception e) {
      // Log the exception for debugging
      logger.severe("Error retrieving configuration with name: " + name + " - " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }

  // Optional: Add a method to list all configurations (useful for debugging)
  public List<Configuration> getAllConfigurations() {
    try {
      return entityManager.createQuery(
          "SELECT c FROM Configuration c ORDER BY c.name",
          Configuration.class).getResultList();
    } catch (Exception e) {
      logger.severe("Error retrieving all configurations: " + e.getMessage());
      return Collections.emptyList();
    }
  }
}