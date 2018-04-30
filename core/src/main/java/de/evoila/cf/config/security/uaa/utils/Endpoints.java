package de.evoila.cf.config.security.uaa.utils;

public class Endpoints {

    public static final String V2_ENDPOINT = "/v2/endpoint";
    public static final String V2_CATALOG = "/v2/catalog";
    public static final String V2_SERVICE_INSTANCES = "/v2/service_instances";
    public static final String V2_SERVICE_INSTANCES_ID ="/service_instances/{instanceId}";
    public static final String V2_SERVICE_INSTANCES_LO = "/service_instances/{instanceId}/last_operation";
    public static final String V2_SERVICE_INSTANCES_BINDINGS = "/{instanceId}/service_bindings/{bindingId}";
    public static final String INFO = "/info";
    public static final String HEALTH = "/health";
    public static final String _ERROR = "/error";
    public static final String ENV = "/env";
    public static final String V2_DASHBOARD = "/v2/dashboard";
    public static final String V2_DASHBOARD_SID = "/v2/dashboard/{serviceInstanceId}";
    public static final String V2_DASHBOARD_SID_CONFIRM = "/v2/dashboard/{serviceInstanceId}/confirm";
    public static final String V2_BACKUP = "/v2/backup";
    public static final String V2_DASHBOARD_MANAGE = "/v2/dashboard/manage";
    public static final String V2_AUTHENTICATION = "/v2/authentication";
    public static final String V2_MANAGE = "/v2/manage";
    public static final String V2_MANAGE_BACKUP = "/v2/manage/backup";
    public static final String V2_MANAGE_SID = "/v2/manage/{serviceInstanceId}"; // GeneralController
    public static final String V2_MANAGE_SERVICE_KEYS = "/v2/manage/servicekeys/{serviceInstanceId}";

}
