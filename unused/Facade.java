package com.cvberry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public class Facade {

	@GET
	@Path("echo")
	@Produces(MediaType.TEXT_PLAIN)
	public String test(@QueryParam("text") String input) {
		return String.format("you sent '%s'\n",input);
	}

    @GET
	@Path("nextPrime")
	@Produces(MediaType.TEXT_PLAIN)
	public String test(@QueryParam("startNum") int startNum) {
        return Integer.toString(Model.findNextBiggerPrime(startNum));
	}
}
