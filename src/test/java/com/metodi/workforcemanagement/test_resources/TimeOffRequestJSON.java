package com.metodi.workforcemanagement.test_resources;

public interface TimeOffRequestJSON {

    String TIME_OFF_REQUEST_REASON = "I want some time off";
    String START_DATE = "2020-06-22";
    String END_DATE = "2020-06-28";
    long RESPONSE_ID = 1;
    String TOKEN = "Bearer Token";
    String CREATED_AT = "2020-06-22T15:53:15Z";
    long TIME_OFF_REQUEST_ID = 1;
    String RESPONSE_APPROVER = "test";
    Long ADMIN_USER_ID = 100L;

    String TIME_OFF_REQUEST_JSON = "{\n" +
            "   \"id\": 1,\n" +
            "   \"leaveType\": \"PAID_LEAVE\",\n" +
            "   \"status\": \"AWAITING\",\n" +
            "   \"requester\": {\n" +
            "       \"id\": 100,\n" +
            "       \"username\": \"admin\"\n"+
            "   },\n" +
            "   \"startDate\": \"2020-06-22\",\n" +
            "   \"endDate\": \"2020-06-28\",\n" +
            "   \"reason\": \"I want some time off\",\n" +
            "   \"leaveWorkDays\": 5,\n" +
            "   \"responses\": [{\n" +
            "       \"id\": 1,\n" +
            "       \"approver\": \"test\",\n" +
            "       \"approved\": true\n" +
            "   }],\n" +
            "   \"createdAt\": \"2020-06-22T15:53:15Z\",\n" +
            "   \"createdBy\": {\n" +
            "       \"id\": 100,\n" +
            "       \"username\": \"admin\"\n" +
            "   },\n" +
            "   \"updatedAt\": \"2020-06-22T15:53:15Z\",\n" +
            "   \"updatedBy\": {\n" +
            "       \"id\": 100,\n" +
            "       \"username\": \"admin\"\n" +
            "   }\n" +
            "}";

    String TIME_OFF_REQUEST_CREATE_JSON = "{\n" +
            "   \"typeOfLeave\": \"PAID_LEAVE\",\n" +
            "   \"startDate\": \"2020-06-22\",\n" +
            "   \"endDate\": \"2020-06-28\",\n" +
            "   \"reason\": \"I want some time off\"}";

    String TIME_OFF_REQUEST_EDIT_JSON = "{\n" +
            "   \"typeOfLeave\": \"SICK_LEAVE\",\n" +
            "   \"startDate\": \"2020-06-22\",\n" +
            "   \"endDate\": \"2020-06-28\",\n" +
            "   \"reason\": \"I want to rest\"}";

    String PAGEABLE_GET_ALL_JSON = "{\n" +
            "    \"content\": [\n" +
            "        {\n" +
            "           \"id\": 1,\n" +
            "           \"leaveType\": \"PAID_LEAVE\",\n" +
            "           \"status\": \"AWAITING\",\n" +
            "           \"requester\": {\n" +
            "               \"id\": 100,\n" +
            "               \"username\": \"admin\"\n"+
            "           },\n" +
            "           \"startDate\": \"2020-06-22\",\n" +
            "           \"endDate\": \"2020-06-28\",\n" +
            "           \"reason\": \"I want some time off\",\n" +
            "           \"leaveWorkDays\": 5,\n" +
            "           \"responses\": [{\n" +
            "               \"id\": 1,\n" +
            "               \"approver\": \"test\",\n" +
            "           \"approved\": true\n" +
            "           }],\n" +
            "           \"createdAt\": \"2020-06-22T15:53:15Z\",\n" +
            "           \"createdBy\": {\n" +
            "               \"id\": 100,\n" +
            "               \"username\": \"admin\"\n" +
            "           },\n" +
            "           \"updatedAt\": \"2020-06-22T15:53:15Z\",\n" +
            "           \"updatedBy\": {\n" +
            "               \"id\": 100,\n" +
            "               \"username\": \"admin\"\n" +
            "           }\n" +
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
}
