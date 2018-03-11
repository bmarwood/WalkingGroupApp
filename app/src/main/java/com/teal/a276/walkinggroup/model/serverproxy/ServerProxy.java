package com.teal.a276.walkinggroup.model.serverproxy;

import com.teal.a276.walkinggroup.model.dataobjects.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Server ServerProxy interface exposing the end points and logout to the views
 * For more information visit http://www.cs.sfu.ca/CourseCentral/276/bfraser/project/APIDocs_WalkingGroupApp.pdf
 *
 * When a method returns Void the result method in the ServerResult Interface should be empty
 */
public interface ServerProxy {
    /**
     *
     * @param user User to create new account with
     * @return A valid user object
     */
    @POST("/users/signup")
    Call<User> createNewUser(@Body User user);

    /**
     *
     * @param userWithEmailAndPassword User to login
     * @return Nothing
     */
    @POST("/login")
    Call<Void> login(@Body User userWithEmailAndPassword);

    /**
     *
     * @return A list of all users
     */
    @GET("/users")
    Call<List<User>> getUsers();

    /**
     *
     * @param userId Id of the user
     * @return The user with the specified id
     */
    @GET("/users/{id}")
    Call<User> getUserById(@Path("id") Long userId);

    /**
     *
     * @param email Email of the user
     * @return The user with the specified email
     */
    @GET("/users/byEmail")
    Call<User> getUserByEmail(@Query("email") String email);

    /**
     * @param monitorId Id of the monitor
     * @return A list of users that the specified user monitors
     */
    @GET("/users/{id}/monitorsUsers")
    Call<List<User>> getMonitors(@Path("id") Long monitorId);

    /**
     * @param userId Id for the user
     * @return A list of all monitors monitoring the user
     */
    @GET("/users/{id}/monitoredByUsers")
    Call<List<User>> getMonitoredBy(@Path("id") Long userId);

    /**
     * Creates a monitor relation between the two users
     * @param monitorId Id for the user who will be the monitor
     * @param userWithId User who the monitor will monitor
     * @return The list of users that the monitor now monitors after adding the new relationship
     */
    @POST("/users/{id}/monitorsUsers")
    Call<List<User>> monitorUser(@Path("id") Long monitorId,
                            @Body User userWithId);

    /**
     * Creates a monitor relation between the two users
     * @param monitorId Id for the user who will be the monitor
     * @param userWithId User who the monitor will monitor
     * @return The list of users that the monitor now monitors after adding the new relationship
     */
    @POST("/users/{id}/monitoredByUsers")
    Call<List<User>> monitoredByUser(@Path("id") Long monitorId,
                                 @Body User userWithId);

    /**
     * Deletes a monitor relationship between A and B.
     * @param idA Id for the monitor
     * @param idB Id for the monitoree
     * @return Nothing
     */
    @DELETE("/users/{idA}/monitorsUsers/{idB}")
    Call<Void> endMonitoring(@Path("idA") Long idA, @Path("idB") Long idB);

    //TODO: add methods for groups and logout
}
