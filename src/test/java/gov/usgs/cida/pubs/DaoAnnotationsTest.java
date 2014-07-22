package gov.usgs.cida.pubs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.MpDao;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author jrschoen (back in biodata - copied here by drsteini)
 *
 */
public class DaoAnnotationsTest {

    /**
     * Annotations required on each DAO method.
     */
    @SuppressWarnings("rawtypes")
    static final Class [] REQUIRED_ANNOTATIONS = {
        org.springframework.transaction.annotation.Transactional.class,
         ISetDbContext.class
    };

    /**
     * Classes in DAO package to exclude from the test.
     */
    static final Class < ? > [] EXCLUDE_CLASSES = {
        BaseDao.class,
        MpDao.class
    };

    /**
     * Methods in the DAO package that are not transactional.
     */
    static final String[] EXCLUDE_METHODS = {"getNewProdId"};

    /**
     * Test methods in dao classes have required annotations.
     * @throws IOException e.
     * @throws NoSuchMethodException e.
     * @throws ClassNotFoundException e.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testRequiredAnnotations()  
    throws IOException, NoSuchMethodException, ClassNotFoundException {
        Class[] classes = getClasses("gov.usgs.cida.pubs.dao");
        Collection<Class<?>> exc = Arrays.asList(EXCLUDE_CLASSES);
        Collection<String> excMethod = Arrays.asList(EXCLUDE_METHODS);
        for (Class classe : classes) {
            Class c = classe;
            if (!exc.contains(c)) {
                Method[] methods = c.getDeclaredMethods();
                for (Method method : methods) {
                    if (!method.isBridge()
                            && !excMethod.contains(method.getName())) {
                        for (Class element : REQUIRED_ANNOTATIONS) {
                            if ((method.getName().startsWith("set") && method
                                    .getName().endsWith("Dao"))
                                    || method.getName().endsWith("Test")) {
                                // Skip setxxxDao Methods
                                continue;
                            } else if (Modifier.PUBLIC == method.getModifiers()) {
                                assertTrue("Method " + method.getName()
                                        + " of Class " + c.getName()
                                        + " does not have annotation :"
                                        + element,
                                        method.isAnnotationPresent(element));
                                if (element
                                        .isAssignableFrom(Transactional.class)) {
                                    if (method.getName().startsWith("get")
                                            || method.getName().startsWith(
                                                    "find")) {
                                        assertTrue(
                                                "Method "
                                                        + method.getName()
                                                        + " of Class "
                                                        + c.getName()
                                                        + " has a \"get\" Method that is not ReadOnly",
                                                ((Transactional) method
                                                        .getAnnotation(element))
                                                        .readOnly());
                                    } else {
                                        assertEquals(
                                                "Method "
                                                        + method.getName()
                                                        + " of Class "
                                                        + c.getName()
                                                        + " has a non-\"get\" "
                                                        + "Method that is NOT Transaction required",
                                                ((Transactional) method
                                                        .getAnnotation(element))
                                                        .propagation(),
                                                Propagation.REQUIRED);

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Method gets all the classes for a specific package.
     * Note: The method changes the path to look at classes not test-classes 
     * @param pckgname name of package
     * @throws ClassNotFoundException e.
     * @return Class[] classes in package
     */
    private static Class < ? >[] getClasses(final String pckgname) throws ClassNotFoundException {
        ArrayList<Class < ? >> classes = new ArrayList<Class < ? >>();
        File directory = null;
        try {
            ClassLoader l = Thread.currentThread().getContextClassLoader();
            String packageName = pckgname.replace('.', '/');
            String loaderPth = l.getResource(packageName).getPath();
            String pth = loaderPth.replace("test-classes", "classes");
            directory = new File(pth.replaceAll("%20", " "));
        } catch (Exception x) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
        }

        if (directory.exists()) {
            String[] files = directory.list();
            for (String file : files) {
                if (file.endsWith(".class")) {
                    classes.add(Class.forName(pckgname + '.' 
                            + file.substring(0, file.length() - ".class".length())));
                }
            }
        } else {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
        }
        Class < ? >[] classesA = new Class[classes.size()];
        classes.toArray(classesA);
        return classesA;
    }

}
