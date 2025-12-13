package mg.razherana.banking.api;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import mg.razherana.banking.configuration.services.changeServices.ConfigurationService;

/**
 * REST API resource for managing currency exchange rates.
 * 
 * <p>
 * This resource provides endpoints for retrieving currency exchange information
 * used throughout the banking system for foreign currency transactions.
 * </p>
 * 
 * <p>
 * <strong>Available Endpoints:</strong>
 * </p>
 * <ul>
 * <li>GET /api/changes - Retrieve all available currency exchange rates</li>
 * </ul>
 * 
 * @author Banking System
 * @version 1.0
 * @since 1.0
 */
@Path("/configurations")
// @Produces(MediaType.APPLICATION_JSON)
// @Consumes(MediaType.APPLICATION_JSON)
public class ConfigurationResource {

  @EJB
  private ConfigurationService configurationService;

  /**
   * Retrieves all available currency exchange rates.
   * 
   * @return Response containing a list of all currency exchange rates with their
   *         validity periods
   */
  @GET
  public Response getConfigurationByName(@QueryParam("name") String name) {
    var result = configurationService.getConfigurationByName(name);
    if (result == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Configuration not found for name: " + name).build();
    }
    return Response.ok(result.getValue()).build();
  }

  /**
   * Handles CORS preflight OPTIONS requests.
   * 
   * @return Response with appropriate CORS headers for preflight requests
   */
  @OPTIONS
  public Response handlePreflight() {
    return Response.ok().build();
  }
}
