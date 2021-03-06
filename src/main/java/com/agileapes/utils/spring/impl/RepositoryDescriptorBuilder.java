package com.agileapes.utils.spring.impl;

import com.agileapes.utils.spring.DataOperation;
import com.agileapes.utils.spring.KeyGeneration;
import com.agileapes.utils.spring.MethodDescriptor;
import com.agileapes.utils.spring.RepositoryDescriptor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2015/1/22 AD, 18:35)
 */
public class RepositoryDescriptorBuilder<E, K extends Serializable, R extends Repository<E, K>> {

    private final RepositoryDescriptor<E, K, R> descriptor;
    
    private RepositoryDescriptorBuilder(RepositoryDescriptor<E, K, R> descriptor) {
        this.descriptor = descriptor;
    }
    
    public RepositoryDescriptorBuilder<E, K, R> addRow(E entity) {
        final Map<K, E> data = new HashMap<K, E>(descriptor.getData());
        final BeanWrapper wrapper = new BeanWrapperImpl(entity);
        final Object value = wrapper.getPropertyValue(descriptor.getKeyProperty());
        Objects.requireNonNull(value, "Key property cannot be null");
        //noinspection unchecked
        data.put((K) value, entity);
        return new RepositoryDescriptorBuilder<E, K, R>(new ImmutableRepositoryDescriptor<E, K, R>(descriptor.getRepositoryType(), data, descriptor.getOperations(), descriptor.getKeyProperty(), descriptor.getKeyGeneration()));
    }

    public RepositoryDescriptorBuilder<E, K, R> addOperation(MethodDescriptor method, DataOperation<E, K> operation) {
        final Map<MethodDescriptor, DataOperation<E, K>> operations = new HashMap<MethodDescriptor, DataOperation<E, K>>(descriptor.getOperations());
        operations.put(method, operation);
        return new RepositoryDescriptorBuilder<E, K, R>(new ImmutableRepositoryDescriptor<E, K, R>(descriptor.getRepositoryType(), descriptor.getData(), operations, descriptor.getKeyProperty(), descriptor.getKeyGeneration()));
    }
    
    public RepositoryDescriptor<E, K, R> build() {
        return new ImmutableRepositoryDescriptor<E, K, R>(descriptor);
    }

    public static <E, K extends Serializable, R extends Repository<E, K>> RepositoryDescriptorBuilder<E, K, R> forRepository(Class<R> repositoryType, String keyProperty) {
        return forRepository(repositoryType, keyProperty, KeyGeneration.NONE);
    }

    public static <E, K extends Serializable, R extends Repository<E, K>> RepositoryDescriptorBuilder<E, K, R> forRepository(Class<R> repositoryType, String keyProperty, KeyGeneration keyGeneration) {
        return new RepositoryDescriptorBuilder<E, K, R>(new ImmutableRepositoryDescriptor<E, K, R>(repositoryType, new HashMap<K, E>(), new HashMap<MethodDescriptor, DataOperation<E, K>>(), keyProperty, keyGeneration));
    }
    
}
