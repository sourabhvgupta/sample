package com.flipkart.sample.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.flipkart.sample.models.Employee;
import com.flipkart.sample.services.EmployeeService;
import com.google.inject.Inject;
import com.sun.jersey.api.client.ClientResponse;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/sample")
@Produces(MediaType.APPLICATION_JSON)
public class SampleResource {

    private final EmployeeService employeeService;

    @Inject
    public SampleResource(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @GET
    @Timed
    @Path("/{employee_id}")
    public Employee getEmployeeDetails(@PathParam("employee_id") String employeeId) {
        try{
            return employeeService.getEmployee(employeeId);
        }catch (Exception e){
            throw new WebApplicationException(Response.status(ClientResponse.Status.NOT_FOUND).entity("Employee Not found").build());
        }

    }

    @POST
    @Timed
    @Path("/employee")
    @ExceptionMetered
    @UnitOfWork(transactional = false)
    public void saveEmployeeDetails(@Valid Employee employee){
        employeeService.saveEmployee(employee);
    }

}
