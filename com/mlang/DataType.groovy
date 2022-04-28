package com.mlang;

class DataType {
    public static HashMap<String, String> DATA_TYPE_NAMES = new HashMap<String, String>();
    public static HashMap<String, Runnable> DATA_TYPE_CHECKS = new HashMap<String, Runnable>();
    public static HashMap<String, String> DATA_FULL_NAME_TYPE = new HashMap<String, Runnable>();
    static {
        DATA_TYPE_NAMES.put("boo", "BOOLEAN_TYPE");
        DATA_TYPE_NAMES.put("num", "NUMBER_TYPE");
        DATA_TYPE_NAMES.put("str", "STRING_TYPE");
        DATA_TYPE_NAMES.put("fun", "FUNCTION_TYPE");
        DATA_TYPE_NAMES.put("emp", "NULL_TYPE");
        DATA_TYPE_NAMES.put("any", "ANY_TYPE");
        DATA_TYPE_CHECKS.put("BOOLEAN_TYPE", (value) -> {
            return value instanceof Boolean;
        });
        DATA_TYPE_CHECKS.put("NUMBER_TYPE", (value) -> {
            return (value instanceof Double) || (value instanceof Float) || (value instanceof Integer) || (value instanceof Long);
        });
        DATA_TYPE_CHECKS.put("STRING_TYPE", (value) -> {
            return value instanceof String;
        });
        DATA_TYPE_CHECKS.put("FUNCTION_TYPE", (value) -> {
            return value instanceof MlangFunction || value instanceof MlangCallable;
        });
        DATA_TYPE_CHECKS.put("NULL_TYPE", (value) -> {
            return value == null;
        });
        DATA_TYPE_CHECKS.put("ANY_TYPE", (value) -> {
            return true;
        });
        for (String type : DATA_TYPE_NAMES.keySet()) {
            DATA_FULL_NAME_TYPE.put(DATA_TYPE_NAMES.get(type), type);
        }
        
    }
}
