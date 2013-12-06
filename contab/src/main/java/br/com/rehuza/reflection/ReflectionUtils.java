package br.com.rehuza.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Transient;

public class ReflectionUtils {
	
	public static Field getField(Class<?> type,String name) {
		for(Field field : getFields(type)){
			if(field.getName().equals(name)) {
				return field;
			}
		}
		return null;
	}
	
	public static List<Field> getFields(Class<?> type) {
		return getFields(new ArrayList<Field>(), type);
	}

	private static List<Field> getFields(List<Field> fields, Class<?> type) {
		for (Field field : type.getDeclaredFields()) {
			fields.add(field);
		}
		if (type.getSuperclass() != null) {
			fields = getFields(fields, type.getSuperclass());
		}
		return fields;
	}

	public static Map<String, Object> describe(Object entity) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		if (entity == null) {
			return result;
		}
		Class<?> type = entity.getClass();
		Object value = null;
		String name = null;
		if (!type.isAnnotationPresent(Entity.class)
				&& !type.isAnnotationPresent(Embeddable.class)) {
			return result;
		}
		List<Field> fields = getFields(type);
		List<Class<?>> datatypes = getDatatypes();
		for (Field field : fields) {
			name = null;
			value = null;
			if (!field.isAnnotationPresent(Transient.class) && !Modifier.isAbstract(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
				field.setAccessible(true);
				name = field.getName();
				if (datatypes.contains(field.getType()) || field.getType().isEnum()) {
					value = field.get(entity);
					if (value != null) {
						result.put(name, field.get(entity));
					}
				} else if (field.getType().isAnnotationPresent(Entity.class) || field.getType().isAnnotationPresent(Embeddable.class)) {
					Map<String, Object> values = describe(field.get(entity));
					for (Entry<String, Object> entry : values.entrySet()) {
						if (entry.getValue() != null) {
							result.put(String.format("%s.%s", name,
									entry.getKey()), entry.getValue());
						}
					}
				} else if (Collection.class.isAssignableFrom(field.getType())) {
					// TODO
					// result.put(name, new HashMap<String, Object>());
				}
			}
		}
		return result;
	}

	public static List<Class<?>> getDatatypes() {
		
		List<Class<?>> types = new ArrayList<Class<?>>();
		
		types.add(boolean.class);
		types.add(byte.class);
		types.add(short.class);
		types.add(char.class);
		types.add(int.class);
		types.add(long.class);
		types.add(float.class);
		types.add(double.class);
		types.add(java.lang.Boolean.class);
		types.add(java.lang.Byte.class);
		types.add(java.lang.Short.class);
		types.add(java.lang.Character.class);
		types.add(java.lang.Integer.class);
		types.add(java.lang.Long.class);
		types.add(java.lang.Float.class);
		types.add(java.lang.Double.class);
		types.add(java.lang.String.class);
		types.add(java.lang.Class.class);
		types.add(java.math.BigInteger.class);
		types.add(java.math.BigDecimal.class);
		types.add(java.util.Date.class);
		types.add(java.util.Calendar.class);
		types.add(java.sql.Date.class);
		types.add(java.sql.Time.class);
		types.add(java.sql.Timestamp.class);
		
		return types;
	}

	public static boolean isEntity(Object object) {
		
		if (object == null) {
			return false;
		}
		
		Class<?> type = object.getClass();
		if (!type.isAnnotationPresent(Entity.class) && !type.isAnnotationPresent(Embeddable.class)) {
			return false;
		}
		
		return true;
	}

	public static Query createQuery(EntityManager em, Object entity) throws Exception {
		
		if (!isEntity(entity)) {
			return null;
		}
		
		Class<?> eType = entity.getClass();
		
		Map<String, Object> map = describe(entity);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT e FROM ");
		sb.append(String.format("%s e", eType.getSimpleName()));
		Set<Entry<String, Object>> entries = map.entrySet();
		Boolean first = true;
		
		if (!entries.isEmpty()) {
			sb.append(" WHERE ");
			for (Entry<String, Object> entry : map.entrySet()) {
				if (first) {
					first = false;
				} else {
					sb.append("AND ");
				}
				sb.append(String.format("e.%s = ? ", entry.getKey()));
			}
		}
		
		int i = 1;
		Query query = em.createQuery(sb.toString(),eType);
		if (!entries.isEmpty()) {
			for (Entry<String, Object> entry : entries) {
				query.setParameter(i, entry.getValue());
				i++;
			}
		}
		
		return query;
	}
}
