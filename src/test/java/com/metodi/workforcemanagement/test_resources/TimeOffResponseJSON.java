package com.metodi.workforcemanagement.test_resources;

public interface TimeOffResponseJSON {

    String GET_ALL_RESPONSES_JSON = "{\n" +
            "    \"content\": [\n" +
            "        {\n" +
            "           \"id\": 100,\n" +
            "           \"request\": {\n" +
            "           \"id\": 109,\n" +
            "           \"leaveType\": \"PAID_LEAVE\",\n" +
            "           \"status\": \"AWAITING\",\n" +
            "           \"requester\": \"testUser\"},\n" +
            "           \"approver\": {\n" +
            "                     \"id\": 100,\n" +
            "                     \"username\": \"Administrator\"},\n" +
            "            \"approved\": true\n" +
            "       }\n" +
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
    String APPROVAL_RESPONSE_JSON = "{\n" +
            "        \"id\": 100,\n" +
            "        \"request\": {" +
            "           \"id\": 109," +
            "           \"leaveType\": \"PAID_LEAVE\",\n" +
            "           \"status\": \"AWAITING\"," +
            "           \"requester\": \"testUser\"},\n" +
            "        \"approver\": {" +
            "               \"id\": 100," +
            "               \"username\": \"Administrator\"},\n" +
            "        \"approved\": true\n" +
            "    }";
}
