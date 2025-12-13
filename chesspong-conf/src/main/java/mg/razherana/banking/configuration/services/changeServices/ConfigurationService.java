package mg.razherana.banking.configuration.services.changeServices;

import mg.razherana.banking.configuration.entities.Configuration;

public interface ConfigurationService {
  public Configuration getConfigurationByName(String name);
}
