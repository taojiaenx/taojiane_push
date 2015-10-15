package org.ddpush.im.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ObjectFactory {
	/**
	 * Instantiate a given class.
	 *
	 * @param currentClass
	 *            Class to instantiate.
	 * @param args
	 *            Arguments for the class constructor.
	 * @return New instance, what else?
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unused")
	public static <T> T instantiate(Class<? extends T> currentClass,
			Object... args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		T instance = null;

			if (args != null) {
				Class<? extends Object>[] classes = new Class<?>[args.length];
				for (int i = 0; i < args.length; i++) {
					classes[i] = args[i].getClass();
				}
				final Constructor<? extends T> ctor = currentClass
						.getDeclaredConstructor(classes);
				instance = ctor.newInstance(args);
			} else {
				final Constructor<? extends T> ctor = currentClass
						.getDeclaredConstructor();
				instance = ctor.newInstance();
			}
		return instance;
	}
}
