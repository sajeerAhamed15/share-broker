package com.ntu.sharebroker.utils;

import java.lang.reflect.Method;

public class MapperUtils {

    public static void merge(Object existingItem, Object newChanges){
        if(!existingItem.getClass().isAssignableFrom(newChanges.getClass())){
            return;
        }

        Method[] methods = existingItem.getClass().getMethods();

        for(Method fromMethod: methods){
            if(fromMethod.getDeclaringClass().equals(existingItem.getClass())
                    && fromMethod.getName().startsWith("get")){

                String fromName = fromMethod.getName();
                String toName = fromName.replace("get", "set");

                try {
                    Method toMethod = existingItem.getClass().getMethod(toName, fromMethod.getReturnType());
                    Object value = fromMethod.invoke(newChanges, (Object[])null);
                    if(value != null){
                        toMethod.invoke(existingItem, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
