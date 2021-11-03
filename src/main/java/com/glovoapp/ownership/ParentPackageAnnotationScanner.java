package com.glovoapp.ownership;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Optional;

public class ParentPackageAnnotationScanner<A extends Annotation> {

    private Class<A> annotationClass;

    public ParentPackageAnnotationScanner(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public Optional<A> scan(AnnotatedElement element){

        String packageName = getPackageName(element).orElse("");

        while (packageName.length() > 0) {
            packageName = getSuperPackageName(packageName);

            Class<?> packageInfo = null;
            try {
                packageInfo = Class.forName(packageName+".package-info", false, ParentPackageAnnotationScanner.class.getClassLoader());
                if (packageInfo.getPackage().isAnnotationPresent(annotationClass)) {
                    return Optional.of(packageInfo.getAnnotation(annotationClass));
                }
            } catch (ClassNotFoundException e) {

                //TODO add log
                packageInfo = null;
            }
        }

        return Optional.empty();
    }

    public Optional<String> getPackageName(AnnotatedElement element) {

        if (element instanceof Class) {
            return Optional.of(((Class<?>) element).getPackage().getName());
        }
        else if (element instanceof Method) {
            return Optional.of(((Method) element).getDeclaringClass().getPackage().getName());
        }

        return Optional.empty();
    }

    public String getSuperPackageName(String packageName) {
        return packageName.substring(0, Math.max(packageName.lastIndexOf('.'), 0));
    }
}
