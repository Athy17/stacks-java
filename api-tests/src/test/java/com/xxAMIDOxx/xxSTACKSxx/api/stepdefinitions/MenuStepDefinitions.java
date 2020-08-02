package com.xxAMIDOxx.xxSTACKSxx.api.stepdefinitions;

import com.xxAMIDOxx.xxSTACKSxx.api.ExceptionMessages;
import com.xxAMIDOxx.xxSTACKSxx.api.WebServiceEndPoints;
import com.xxAMIDOxx.xxSTACKSxx.api.menu.MenuActions;
import com.xxAMIDOxx.xxSTACKSxx.api.menu.MenuRequests;
import com.xxAMIDOxx.xxSTACKSxx.api.models.Menu;
import com.xxAMIDOxx.xxSTACKSxx.api.templates.FieldValues;
import com.xxAMIDOxx.xxSTACKSxx.api.templates.MergeFrom;
import com.xxAMIDOxx.xxSTACKSxx.api.templates.TemplateResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Steps;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.rest.SerenityRest.restAssuredThat;
import static org.assertj.core.api.Assertions.assertThat;

public class MenuStepDefinitions {

    private final static Logger LOGGER = LoggerFactory.getLogger(MenuStepDefinitions.class);

    @Steps
    MenuRequests menuRequest;

    @Steps
    TemplateResponse theMenuDetails;

    @Steps
    MenuActions menuActions;

    String menuId;
    String menuBody;
    final String BASE_URL = WebServiceEndPoints.BASE_URL.getUrl();


    @Given("the following menu data:")
    public void create_menu_body_from_data(List<Map<String, String>> menuDetails) throws IOException {
        menuBody = MergeFrom.template("templates/menu.json")
                .withDefaultValuesFrom(FieldValues.in("templates/standard-menu.properties"))
                .withFieldsFrom(menuDetails.get(0));

        Serenity.setSessionVariable("Menu").to(menuBody);
    }

    @Given("the menu list is not empty")
    public void get_all_existing_menus() {
        SerenityRest.get(WebServiceEndPoints.BASE_URL.getUrl()
                + WebServiceEndPoints.MENU.getUrl());

        restAssuredThat(response -> response.statusCode(200));
        restAssuredThat(lastResponse -> lastResponse.body("results", Matchers.notNullValue()));
    }

    @When("I create the menu")
    public void i_create_the_menu() {
        menuRequest.createMenu(menuBody);
    }

    @When("I search the created menu by id")
    public void i_search_the_last_created_menu_by_id() {
        getMenuByParameter(menuId);
    }

    public Response getMenuByParameter(String parameter) {
        return SerenityRest.get(BASE_URL + WebServiceEndPoints.MENU.getUrl() + "/" + parameter);
    }

    @When("I search the menu by:")
    public void search_the_menu_by_parameter(DataTable table) {
        List<List<String>> data = table.asLists(String.class);
        String parameter = data.get(1).get(0);
        String value = data.get(0).get(0);

        getMenuByParameter(parameter);
        Serenity.setSessionVariable(value).to(parameter);
    }

    @When("I search the menu by criteria")
    public void search_the_menu_by_multiple_parameters(DataTable table) {
        List<List<String>> data = table.asLists(String.class);
        String xPath = MenuActions.createUrlWithCriteria(data);
        String parametrisedPath = BASE_URL + WebServiceEndPoints.MENU.getUrl() + "?" + xPath;

        SerenityRest.get(parametrisedPath);
    }

    @When("I get the restaurant id for last created menu")
    public void getLastCreatedRestaurantId() {
        Serenity.setSessionVariable("RestaurantId").to(menuActions.getRestaurantIdOfLastCreatedMenu());
    }

    @When("I search the menu by restaurant id")
    public void getMenuById() {
        String restaurant_id = String.valueOf(Serenity.getCurrentSession().get("RestaurantId"));
        String parametrisedPath = BASE_URL.concat(WebServiceEndPoints.MENU.getUrl()).concat("?restaurantId=") + restaurant_id ;

        SerenityRest.get(parametrisedPath);
    }


    @Then("the {int} items are returned")
    public void the_items_are_returned(Integer expectedSize) {
        String response = SerenityRest.lastResponse().prettyPrint();

        JSONObject js = MenuActions.toJson(response);
        Assert.assertEquals(js.getJSONArray("results").length(), (int) expectedSize);
    }


    @Then("the menu should include the following details:")
    public void the_menu_contains_the_following_details(List<Map<String, String>> menuDetails) {
        Map<String, String> expectedResponse = menuDetails.get(0);
        String actualResponse = theMenuDetails.returned().getOrDefault("results", null);

        assertThat(actualResponse).contains(expectedResponse.values());
    }

    @Then("the menu should include the following data:")
    public void the_menu_contains_the_following_data(List<Map<String, String>> menuDetails) {
        restAssuredThat(response -> response.statusCode(200));

        String id = menuActions.getIdOfLastCreatedObject();
        Menu expectedMenu = MenuActions.mapToMenu(menuDetails.get(0), id);
        Menu actualMenu = menuActions.responseToMenu(lastResponse());

        assertThat(expectedMenu).isEqualToIgnoringGivenFields(actualMenu, "categories");
    }


    @When("I search the updated menu")
    public void i_search_the_updated_menu() {
        i_search_the_last_created_menu_by_id();
    }


    @Then("the menu was successfully created")
    public void the_menu_was_created() {
        restAssuredThat(response -> response.statusCode(201));
        menuId = menuActions.getIdOfLastCreatedObject();
        Serenity.setSessionVariable("MenuId").to(menuId);
    }


    @Then("the returned status code is {int}")
    public void i_check_the_status_code(int statusCode) {
        restAssuredThat(response -> response.statusCode(statusCode));
    }


    @Then("the error {string} is {string}")
    public void i_check_the_error_message(String errorField, String errorMessage) {
        restAssuredThat(status -> status.body(errorField, Matchers.equalToIgnoringCase(errorMessage)));
    }


    @Then("the 'menu does not exist' message is returned")
    public void i_check_the_menu_does_not_exist_message() {
        menuActions.check_exception_message(ExceptionMessages.MENU_DOES_NOT_EXIST, lastResponse());
    }

    @Then("the 'menu already exist' message is returned")
    public void i_check_the_menu_already_exist_message() {
        menuActions.check_exception_message(ExceptionMessages.MENU_ALREADY_EXISTS, lastResponse());
    }

    @Then("I update the menu with the following data:")
    public void i_update_the_menu_with_following_data(List<Map<String, String>> menuDetails) throws IOException {
        String id = menuActions.getIdOfLastCreatedObject();
        create_menu_body_from_data(menuDetails);

        menuRequest.updateMenu(menuBody, id);
    }


    @When("I update the menu for {string} id with data:")
    public void i_update_the_menu_with_data(String id, List<Map<String, String>> menuDetails) throws IOException {
        create_menu_body_from_data(menuDetails);
        menuRequest.updateMenu(menuBody, id);
    }

    @When("I delete the menu")
    public void i_delete_the_menu() {
        menuId = menuActions.getIdOfLastCreatedObject();
        menuRequest.deleteTheMenu(menuId);
        LOGGER.info(String.format("The menu with '%s' id was successfully deleted.", menuId));
    }

    @When("I delete the menu with {string} id")
    public void i_delete_the_menu_by_id(String menuId) {
        menuRequest.deleteTheMenu(menuId);
    }

    @Then("the menu is successfully deleted")
    public void the_menu_is_successfully_deleted() {
        restAssuredThat(response -> response.statusCode(200));
        menuRequest.getMenu(menuId);

        restAssuredThat(response -> response.statusCode(404));
        menuActions.check_exception_message(ExceptionMessages.MENU_DOES_NOT_EXIST, lastResponse());
    }
}
