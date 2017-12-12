package rikka.librikka.container;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.LinkedList;

import javax.annotation.Nonnull;

import net.minecraft.inventory.Container;
import rikka.librikka.ByteSerializer;

public class ContainerSynchronizer {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface SyncField {
		/**
		 * Alias
		 */
		String value() default "";
	}

	public static Object[] detectChanges(@Nonnull Container container, @Nonnull Class toSuperClass, @Nonnull Object source) {
		LinkedList changeList = new LinkedList();

		for (Class cls = container.getClass(); cls != toSuperClass; cls = cls.getSuperclass()) {
			for (Field field: cls.getDeclaredFields()) {
				if (field.isAnnotationPresent(SyncField.class)) {
					SyncField annotation = field.getAnnotation(SyncField.class);
					String fieldName = annotation.value();
					if (fieldName.length() == 0)
						fieldName = field.getName();

					try {
						Field sourceField = source.getClass().getField(fieldName);
						Object sourceVal = sourceField.get(source);
						Object containerVal = field.get(container);

						if (ByteSerializer.detectChange(sourceVal, containerVal)) {
							changeList.add(fieldName);
							changeList.add(sourceVal);

							field.set(container, sourceVal);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return changeList.isEmpty() ? null : changeList.toArray();
	}
	
	public static void syncClientFields(Object[] changeList, @Nonnull Container container) {
		for (int i=0; i<changeList.length; i+=2) {
			String fieldName = (String) changeList[i];
			Object val = changeList[i+1];
			
			try {
				Field field = container.getClass().getField(fieldName);
				field.set(container, val);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
