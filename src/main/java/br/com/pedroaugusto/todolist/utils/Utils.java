package br.com.pedroaugusto.todolist.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {
  public static void copyNonNullableProperties(Object source, Object target) {
    String[] nullProperties = Utils.getNullablePropertiesName(source);
    BeanUtils.copyProperties(source, target, nullProperties);
  }

  public static String[] getNullablePropertiesName(Object object) {
    final BeanWrapper source = new BeanWrapperImpl(object);

    PropertyDescriptor[] propertyDescriptorsSource = source.getPropertyDescriptors();
    Set<String> emptyNames = new HashSet<>();

    for (PropertyDescriptor property : propertyDescriptorsSource) {
      Object sourceValue = source.getPropertyValue(property.getName());
      if (sourceValue == null) {
        emptyNames.add(property.getName());
      }
    }

    String[] result = new String[emptyNames.size()];
    return emptyNames.toArray(result);
  }
}
