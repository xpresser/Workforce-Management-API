package com.metodi.workforcemanagement.test_resources;

public interface TeamJSON {

    String TEAM_GET_ALL_JSON = "{\n" +
            "    \"content\": [\n" +
            "        {\n" +
            "           \"id\": 1,\n" +
            "           \"teamLeader\": {\n" +
            "                 \"id\": 100,\n" +
            "                 \"username\": \"Administrator\"\n" +
            "           },\n" +
            "           \"title\": \"testTeam\",\n" +
            "           \"description\": \"some description\",\n" +
            "           \"createdAt\": \"2020-05-23T20:20:20Z\",\n" +
            "           \"updatedAt\": \"2020-05-23T20:20:20Z\",\n" +
            "           \"teamMembers\": []\n" +
            "        }\n" +
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

    String TEAM_GET_BY_ID_JSON = "{\n" +
            "    \"id\": 1,\n" +
            "    \"teamLeader\": {\n" +
            "                 \"id\": 100,\n" +
            "                 \"username\": \"Administrator\"\n" +
            "           },\n" +
            "    \"title\": \"testTeam\",\n" +
            "    \"description\": \"some description\",\n" +
            "    \"createdAt\": \"2020-05-23T20:20:20Z\",\n" +
            "    \"updatedAt\": \"2020-05-23T20:20:20Z\",\n" +
            "    \"teamMembers\": []\n" +
            "}";

    String TEAM_CREATE_JSON = "{\n" +
            "    \"id\": 1,\n" +
            "    \"teamLeader\": {\n" +
            "                 \"id\": 100,\n" +
            "                 \"username\": \"Administrator\"\n" +
            "           },\n" +
            "    \"title\": \"testTeam\",\n" +
            "    \"description\": \"some description\",\n" +
            "      \"createdBy\": {\n" +
            "           \"id\": 100,\n" +
            "           \"username\": \"Administrator\"\n" +
            "       },\n" +
            "    \"updatedBy\": {\n" +
            "          \"id\": 100,\n" +
            "          \"username\": \"Administrator\"\n" +
            "     },\n" +
            "    \"createdAt\": \"2020-05-23T20:20:20Z\",\n" +
            "    \"updatedAt\": \"2020-05-23T20:20:20Z\",\n" +
            "    \"teamMembers\": []\n" +
            "}";

    String TEAM_EDIT_JSON = "{\"id\": 1," +
            "    \"teamLeader\": {\n" +
            "                 \"id\": 100,\n" +
            "                 \"username\": \"Administrator\"\n" +
            "           },\n" +
            "\"title\": \"editedTestTeam\"," +
            "\"description\": \"some description\"," +
            "\"createdBy\": {\n" +
            "           \"id\": 100,\n" +
            "           \"username\": \"Administrator\"\n" +
            "       },\n" +
            "    \"updatedBy\": {\n" +
            "          \"id\": 100,\n" +
            "          \"username\": \"Administrator\"\n" +
            "     },\n" +
            "    \"createdAt\": \"2020-05-23T20:20:20Z\",\n" +
            "    \"updatedAt\": \"2020-05-23T20:20:20Z\",\n" +
            "    \"teamMembers\": []\n" +
            "}";
}
