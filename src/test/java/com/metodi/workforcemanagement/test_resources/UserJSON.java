package com.metodi.workforcemanagement.test_resources;

public interface UserJSON {
    String GET_ALL_USERS_JSON = "{\n" +
            "    \"content\": [\n" +
            "        {\n" +
            " \"id\" : 1,\n" +
            " \"email\" : \"admin@gmail.com\", \n " +
            " \"remainingDaysOff\" : {}, \n" +
            " \"username\": \"admin\", \n" +
            " \"firstName\" : \"admin\", \n" +
            " \"lastName\" : \"administrator\", \n" +
            " \"admin\" : false, \n" +
            " \"onLeave\" : false, \n" +
            " \"createdAt\" : \"2020-05-23T20:20:20Z\", \n" +
            " \"createdBy\": {\n" +
            "       \"id\": 1,\n" +
            "       \"username\": \"admin\"\n" +
            "   }," +
            " \"updatedAt\" : \"2020-05-23T20:20:20Z\", \n" +
            "\"updatedBy\": {\n" +
            "        \"id\": 1,\n" +
            "         \"username\": \"admin\"\n" +
            "   }" +
            "}" +
            "    ],\n" +
            "    \"pageable\": {\n" +
            "        \"sort\": {\n" +
            "            \"sorted\": false,\n" +
            "            \"unsorted\": true,\n" +
            "            \"empty\": true\n" +
            "        },\n" +
            "        \"offset\": 0,\n" +
            "        \"pageNumber\": 0,\n" +
            "        \"pageSize\": 10,\n" +
            "        \"paged\": true,\n" +
            "        \"unpaged\": false\n" +
            "    },\n" +
            "    \"totalElements\": 1,\n" +
            "    \"totalPages\": 1,\n" +
            "    \"last\": true,\n" +
            "    \"number\": 0,\n" +
            "    \"sort\": {\n" +
            "        \"sorted\": false,\n" +
            "        \"unsorted\": true,\n" +
            "        \"empty\": true\n" +
            "    },\n" +
            "    \"size\": 10,\n" +
            "    \"first\": true,\n" +
            "    \"numberOfElements\": 1,\n" +
            "    \"empty\": false\n" +
            "}";

    String USER_RESPONSE_JSON = "{\n" +
            " \"id\" : 1,\n" +
            " \"email\" : \"admin@gmail.com\", \n " +
            " \"remainingDaysOff\" : {}, \n" +
            " \"username\": \"admin\", \n" +
            " \"firstName\" : \"admin\", \n" +
            " \"lastName\" : \"administrator\", \n" +
            " \"admin\" : false, \n" +
            " \"onLeave\" : false, \n" +
            " \"createdAt\" : \"2020-05-23T20:20:20Z\", \n" +
            " \"createdBy\": {\n" +
            "       \"id\": 1,\n" +
            "       \"username\": \"admin\"\n" +
            "   }," +
            " \"updatedAt\" : \"2020-05-23T20:20:20Z\", \n" +
            "\"updatedBy\": {\n" +
            "        \"id\": 1,\n" +
            "         \"username\": \"admin\"\n" +
            "   }" +
            "}";

    String EDIT_USER_RESPONSE_JSON = "{\n" +
            " \"id\" : 1,\n" +
            " \"email\" : \"editAdmin@gmail.com\", \n " +
            " \"remainingDaysOff\" : {}, \n" +
            " \"username\": \"editAdmin\", \n" +
            " \"firstName\" : \"editAdmin\", \n" +
            " \"lastName\" : \"editAdministrator\", \n" +
            " \"admin\" : false, \n" +
            " \"onLeave\" : false, \n" +
            " \"createdAt\" : \"2020-05-23T20:20:20Z\", \n" +
            " \"createdBy\": {\n" +
            "       \"id\": 1,\n" +
            "       \"username\": \"admin\"\n" +
            "   }," +
            " \"updatedAt\" : \"2020-05-23T20:20:20Z\", \n" +
            "\"updatedBy\": {\n" +
            "        \"id\": 1,\n" +
            "         \"username\": \"admin\"\n" +
            "   }" +
            "}";

    String EDIT_USER_REQUEST_JSON  = "{\"username\": \"editAdmin\",\n" +
            "    \"email\": \"editAdmin@gmail.com\",\n" +
            "    \"password\": \"editPass\",\n" +
            "    \"firstName\": \"editAdmin\",\n" +
            "    \"lastName\": \"editAdministrator\",\n" +
            "    \"isAdmin\": false}";

    String USER_REQUEST_JSON = "{\"username\": \"admin\",\n" +
            "    \"email\": \"admin@gmail.com\",\n" +
            "    \"password\": \"pass\",\n" +
            "    \"firstName\": \"admin\",\n" +
            "    \"lastName\": \"administrator\",\n" +
            "    \"isAdmin\": false}";
}
